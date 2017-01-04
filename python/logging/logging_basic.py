# -*- coding: utf-8 -*-
"""

最基础的日志配置

"""
import logging, traceback, sys, chardet, os.path, os


def traceinfo(e):
    """
    获取异常堆栈信息
    :param e: (Exception) 异常
    :return:  （str) 异常堆栈信息
    """
    result = u""

    excep_list = traceback.format_exception(*sys.exc_info())

    for ex in excep_list:
        info = chardet.detect(ex)
        enc = info['encoding']
        result += ex.decode(enc, "ignore")
    return result


def getLogger(name,path='.'):

    formatter = logging.Formatter('%(asctime)-15s  %(levelname)s (line  %(lineno)d): %(message)s')
    logger = logging.getLogger(name)
    logger.setLevel(logging.DEBUG)

    if not os.path.exists(path): os.makedirs(path)
    # create file handler which logs even debug messages
    fh = logging.FileHandler('%s/%s.log' % (path, name))
    fh.setLevel(logging.DEBUG)
    # create console handler with a higher log level
    ch = logging.StreamHandler()
    ch.setLevel(logging.DEBUG)
    fh.setFormatter(formatter)
    ch.setFormatter(formatter)
    # add the handlers to the logger
    logger.addHandler(fh)
    logger.addHandler(ch)

    return logger

if __name__ == '__main__':
    pass