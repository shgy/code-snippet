NodesListManager 主要的逻辑在handle()方法中。 handle方法接收两种类型的事件：NODE_UNUSABLE 和 NODE_USABLE.

其处理逻辑为 将事件通知到所有RMApp.
```
for(RMApp app: rmContext.getRMApps().values()) {
        this.rmContext
            .getDispatcher()
            .getEventHandler()
            .handle(
                new RMAppNodeUpdateEvent(app.getApplicationId(), eventNode,
                    RMAppNodeUpdateType.NODE_UNUSABLE));
      }
```