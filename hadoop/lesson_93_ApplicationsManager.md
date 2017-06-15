RMAppManager 即负责管理Application.

1. 移除过期的已经完成的application
   TestAppManager.testRMAppRetireNone()  -- application数量没有超过队列的限制，不移除
   TestAppManager.testRMAppRetireSome()  -- application数量超过队列的限制， 移除

