NodeStatusUpdaterImpl 会向RM注册。在注册的过程中，提交了哪些信息呢？
```
List<NMContainerStatus> containerReports = getNMContainerStatuses();
    RegisterNodeManagerRequest request =
        RegisterNodeManagerRequest.newInstance(nodeId, httpPort, totalResource,
          nodeManagerVersionId, containerReports, getRunningApplications());
```
nodeId = host:port