1 使用select 轮询等待请求的到来,（/usr/lib/python2.7/SocketServer）

```

 while not self.__shutdown_request:

    r, w, e = _eintr_retry(select.select, [self], [], [],poll_interval)

     if self in r:

         self._handle_request_noblock()

```

2 创建新的线程来处理请求：

```

    def process_request(self, request, client_address):

        """Start a new thread to process the request."""

        t = threading.Thread(target = self.process_request_thread,

                             args = (request, client_address))

        t.daemon = self.daemon_threads

        t.start()

```

3 实例化一个WSGIRequestHandler, 并在__init__方法中处理请求

```

class WSGIRequestHandler(simple_server.WSGIRequestHandler, object):

    def __init__(self, *args, **kwargs):

        self.style = color_style()

        super(WSGIRequestHandler, self).__init__(*args, **kwargs)

```

在WSGIRequestHandler的父类SocketServer中可以看到， Django允许的url最大长为65536

```

# django.core.servers.basehttp.py

    def handle(self):

        """Copy of WSGIRequestHandler, but with different ServerHandler"""

        self.raw_requestline = self.rfile.readline(65537)

        if len(self.raw_requestline) > 65536:

            self.requestline = ''

            self.request_version = ''

            self.command = ''

            self.send_error(414)

            return

```

4 创建一个ServerHandler，并执行run()方法处理请求：

```

    def run(self, application):

        try:

            self.setup_environ()

            self.result = application(self.environ, self.start_response)

            self.finish_response()

        except:

            try:

                self.handle_error()

            except:

                # If we get an error handling an error, just give up already!

                self.close()

                raise   # ...and let the actual server figure it out.

```

5 如果中间件没有加载，则加载其中间件

```

        if self._request_middleware is None:

            with self.initLock:

                try:

                    # Check that middleware is still uninitialized.

                    if self._request_middleware is None:

                        self.load_middleware()

                except:

                    # Unload whatever middleware we got

                    self._request_middleware = None

                    raise

```

6 接下来进入Django 处理逻辑的核心，即django.core.handlers.base.BaseHandler.get_response()方法，在这个方法中会做如下的事情：

1. 使用`urlresolvers`解析url，找到对应的view.

2. 调用middleware的各种方法：`process_request` `process_view` 等。

3. 执行view方法，view的执行是一个原子操作。




