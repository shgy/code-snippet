
在使用hiveserver2的python客户端pyhs2时， 遇到了一些问题。 看github, 官方已经不维护了。推荐的两个代替品也没有比较好的文档。

看pyhs2的代码， 发现也不甚复杂。 就决定重复造轮子。 ， 其实算不上重复造轮子，只是将PyHive的代码拿过来， 做了裁剪和加工而已。
```
# -*- coding: utf-8 -*-

import sys
import time
import sasl
import collections
import logging
import threading

from future.utils import iteritems

from TCLIService import TCLIService
from thrift.transport.TSocket import TSocket
from thrift.protocol.TBinaryProtocol import TBinaryProtocol
from TCLIService import ttypes

from thrift_sasl import TSaslClientTransport

_logger = logging.getLogger(__name__)

# PEP 249
apilevel = '2.0'
threadsafety = 2

def connect(*args, **kwargs):

    return Connection()


class Connection(object):
    def __init__(self):
        host = '172.18.0.2'
        port = 10000
        username = 'username'
        password = 'password'
        mode = TSocket(host=host, port=port)
        mode.setTimeout(50 * 1000.0)

        def sasl_factory():
            saslc = sasl.Client()
            saslc.setAttr("host", str(host))
            # saslc.setAttr("service", str(conf.kerberos_principal))
            saslc.setAttr("username", str(username))
            saslc.setAttr("password", str(password))  # Defaults to 'hue' for a non-empty string unless using LDAP
            saslc.init()
            return saslc

        transport = TSaslClientTransport(sasl_factory, 'PLAIN', mode)
        self.transport = transport
        protocol = TBinaryProtocol(transport)
        self.client = TCLIService.Client(protocol)

        transport.open()
        # step 4 open session
        sessionRes = self.client.OpenSession(
            ttypes.TOpenSessionReq(username=username,
                            password=password,
                            configuration={}))

        self.sessionHandle = sessionRes.sessionHandle

    def cursor(self, *args, **kwargs):
        """Return a new :py:class:`Cursor` object using the connection."""
        return Cursor(self, **kwargs)

    def close(self):
        # step end close session
        req = ttypes.TCloseSessionReq(sessionHandle=self.sessionHandle)
        self.client.CloseSession(req)
        self.transport.close()

lock = threading.Lock()

class Cursor(object):

    _STATE_NONE = 0
    _STATE_RUNNING = 1
    _STATE_FINISHED = 2

    def __init__(self, connection):
        self._connection = connection
        self._operationHandle = None
        self.arraysize = 1000
        self._poll_interval = 1

        self._reset_state()

    def _reset_state(self):
        """Reset state about the previous query in preparation for running another query"""
        # State to return as part of DB-API
        self._rownumber = 0

        # Internal helper state
        self._state = self._STATE_NONE
        self._data = collections.deque()
        self._columns = None

    def execute(self,sql, runAsync=True):
        self._reset_state()

        self._state = self._STATE_RUNNING
        _logger.info('%s', sql)

        req = ttypes.TExecuteStatementReq(self._connection.sessionHandle,
                                          sql, runAsync=runAsync)
        _logger.debug(req)
        with lock:
            response = self._connection.client.ExecuteStatement(req)
        _check_status(response)
        self._operationHandle = response.operationHandle

    def poll(self):
        """Poll for and return the raw status data provided by the Hive Thrift REST API.
        :returns: ``ttypes.TGetOperationStatusResp``
        :raises: ``ProgrammingError`` when no query has been started
        .. note::
            This is not a part of DB-API.
        """
        if self._state == self._STATE_NONE:
            raise Exception("No query yet")

        req = ttypes.TGetOperationStatusReq(operationHandle=self._operationHandle)
        with lock:
            response = self._connection.client.GetOperationStatus(req)
        _check_status(response)

        return response

    def fetch_logs(self):
        """Retrieve the logs produced by the execution of the query.
        Can be called multiple times to fetch the logs produced after the previous call.
        :returns: list<str>
        :raises: ``ProgrammingError`` when no query has been started
        .. note::
            This is not a part of DB-API.
        """
        if self._state == self._STATE_NONE:
            raise Exception("No query yet")

        req = ttypes.TFetchResultsReq(
            operationHandle=self._operationHandle,
            orientation=ttypes.TFetchOrientation.FETCH_NEXT,
            maxRows=self.arraysize,
            fetchType=1  # 0: results, 1: logs
        )
        with lock:
            response = self._connection.client.FetchResults(req)
        _check_status(response)
        assert not response.results.rows, 'expected data in columnar format'
        assert len(response.results.columns) == 1, response.results.columns
        logs = _unwrap_column(response.results.columns[0])
        return logs

    def _fetch_while(self, fn):
        while fn():
            self._fetch_more()
            if fn():
                time.sleep(self._poll_interval)

    def _fetch_more(self):
        """Send another TFetchResultsReq and update state"""
        assert(self._state == self._STATE_RUNNING), "Should be running when in _fetch_more"
        assert(self._operationHandle is not None), "Should have an op handle in _fetch_more"
        if not self._operationHandle.hasResultSet:
            raise Exception("No result set")
        req = ttypes.TFetchResultsReq(
            operationHandle=self._operationHandle,
            orientation=ttypes.TFetchOrientation.FETCH_NEXT,
            maxRows=self.arraysize,
        )
        with lock:
            response = self._connection.client.FetchResults(req)
        _check_status(response)
        assert not response.results.rows, 'expected data in columnar format'
        columns = map(_unwrap_column, response.results.columns)
        new_data = list(zip(*columns))
        self._data += new_data
        # response.hasMoreRows seems to always be False, so we instead check the number of rows
        # https://github.com/apache/hive/blob/release-1.2.1/service/src/java/org/apache/hive/service/cli/thrift/ThriftCLIService.java#L678
        # if not response.hasMoreRows:
        if not new_data:
            self._state = self._STATE_FINISHED

    def fetchall(self):
        """Fetch all (remaining) rows of a query result, returning them as a sequence of sequences
        (e.g. a list of tuples).
        An :py:class:`~pyhive.exc.Error` (or subclass) exception is raised if the previous call to
        :py:meth:`execute` did not produce any result set or no call was issued yet.
        """
        result = []
        while True:
            one = self.fetchone()
            if one is None:
                break
            else:
                result.append(one)
        return result

    def fetchone(self):
        """Fetch the next row of a query result set, returning a single sequence, or ``None`` when
        no more data is available.
        An :py:class:`~pyhive.exc.Error` (or subclass) exception is raised if the previous call to
        :py:meth:`execute` did not produce any result set or no call was issued yet.
        """
        if self._state == self._STATE_NONE:
            raise Exception("No query yet")

        # Sleep until we're done or we have some data to return
        self._fetch_while(lambda: not self._data and self._state != self._STATE_FINISHED)

        if not self._data:
            return None
        else:
            self._rownumber += 1
            return self._data.popleft()

    def cancel(self):
        req = ttypes.TCancelOperationReq(
            operationHandle=self._operationHandle,
        )
        response = self._connection.client.CancelOperation(req)
        _check_status(response)

def _unwrap_column(col):
    """Return a list of raw values from a TColumn instance."""
    for attr, wrapper in iteritems(col.__dict__):
        if wrapper is not None:
            result = wrapper.values
            nulls = wrapper.nulls  # bit set describing what's null
            assert isinstance(nulls, bytes)
            for i, char in enumerate(nulls):
                byte = ord(char) if sys.version_info[0] == 2 else char
                for b in range(8):
                    if byte & (1 << b):
                        result[i * 8 + b] = None
            return result
    raise Exception("Got empty column value {}".format(col))  # pragma: no cover

def _check_status(response):
    """Raise an OperationalError if the status is not success"""
    _logger.debug(response)
    if response.status.statusCode != ttypes.TStatusCode.SUCCESS_STATUS:
        raise Exception(response)

conn = connect()

def do_in_thread(sql):
    print sql
    import random
    sql = sql % random.randint(1000,10000)

    cursor = conn.cursor()
    cursor.execute(sql)
    status = cursor.poll().operationState
    while status in (ttypes.TOperationState.INITIALIZED_STATE, ttypes.TOperationState.RUNNING_STATE):
        # logs = cursor.fetch_logs()
        # for message in logs:
        #     print message
        time.sleep(1)

        status = cursor.poll().operationState

    print cursor.fetchall()


if __name__ == '__main__':
    sql = u"""
        select count(1) from
        (
            select repeat('aaaa,',%d) as s
        )t lateral view explode(split(s,','))vs as v
        """

    threads = []

    for i in range(5):
        threads.append(threading.Thread(target=do_in_thread, args=(sql,)))

    for t in threads:
        t.start()

    for t in threads:
        t.join()

    conn.close()

```
一共就200多行代码， 这里为了添加多线程的支持， 使用了threading.Lock


pyhive的客户端 使用多线程还是有问题
