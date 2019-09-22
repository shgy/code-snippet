使用health api可以查看es集群的健康度。 health api的用法如下:

```
curl 'http://localhost:9200/_cluster/health' 
```

health api的返回值中有一个核心的字段`status`， `status` 有3种取值: green, yellow, red。分别代表集群的3种状态: 主分片和副本都已经分配，主分片已经分配副本分片没有，制定分片没有分配。

也就是说， health api关注的核心在于数据的高可用。

看health api的实现: 
请求会路由到master节点，然后读取clusterState中的`routing_table`.

基于`routing_table`, 判断索引的各个分片状态
```
    public ClusterShardHealth(int shardId, final IndexShardRoutingTable shardRoutingTable) {
        this.shardId = shardId;
        for (ShardRouting shardRouting : shardRoutingTable) {
            if (shardRouting.active()) {
                activeShards++;
                if (shardRouting.relocating()) {
                    // the shard is relocating, the one it is relocating to will be in initializing state, so we don't count it
                    relocatingShards++;
                }
                if (shardRouting.primary()) {
                    primaryActive = true;
                }
            } else if (shardRouting.initializing()) {
                initializingShards++;
            } else if (shardRouting.unassigned()) {
                unassignedShards++;
            }
        }
        if (primaryActive) {
            if (activeShards == shardRoutingTable.size()) {
                status = ClusterHealthStatus.GREEN;
            } else {
                status = ClusterHealthStatus.YELLOW;
            }
        } else {
            status = ClusterHealthStatus.RED;
        }
    }
```


基于分片，决定索引的状态

```
    public ClusterIndexHealth(IndexMetaData indexMetaData, IndexRoutingTable indexRoutingTable) {
        this.index = indexMetaData.getIndex();
        this.numberOfShards = indexMetaData.getNumberOfShards();
        this.numberOfReplicas = indexMetaData.getNumberOfReplicas();
        this.validationFailures = indexRoutingTable.validate(indexMetaData);

        for (IndexShardRoutingTable shardRoutingTable : indexRoutingTable) {
            int shardId = shardRoutingTable.shardId().id();
            shards.put(shardId, new ClusterShardHealth(shardId, shardRoutingTable));
        }

        // update the index status
        status = ClusterHealthStatus.GREEN;

        for (ClusterShardHealth shardHealth : shards.values()) {
            if (shardHealth.isPrimaryActive()) {
                activePrimaryShards++;
            }
            activeShards += shardHealth.getActiveShards();
            relocatingShards += shardHealth.getRelocatingShards();
            initializingShards += shardHealth.getInitializingShards();
            unassignedShards += shardHealth.getUnassignedShards();

            if (shardHealth.getStatus() == ClusterHealthStatus.RED) {
                status = ClusterHealthStatus.RED;
            } else if (shardHealth.getStatus() == ClusterHealthStatus.YELLOW && status != ClusterHealthStatus.RED) {
                // do not override an existing red
                status = ClusterHealthStatus.YELLOW;
            }
        }
        if (!validationFailures.isEmpty()) {
            status = ClusterHealthStatus.RED;
        } else if (shards.isEmpty()) { // might be since none has been created yet (two phase index creation)
            status = ClusterHealthStatus.RED;
        }
    }
```

基于索引，决定集群的状态。
```
    public ClusterStateHealth(ClusterState clusterState, String[] concreteIndices) {
        RoutingTableValidation validation = clusterState.routingTable().validate(clusterState.metaData());
        validationFailures = validation.failures();
        numberOfNodes = clusterState.nodes().size();
        numberOfDataNodes = clusterState.nodes().dataNodes().size();

        for (String index : concreteIndices) {
            IndexRoutingTable indexRoutingTable = clusterState.routingTable().index(index);
            IndexMetaData indexMetaData = clusterState.metaData().index(index);
            if (indexRoutingTable == null) {
                continue;
            }

            ClusterIndexHealth indexHealth = new ClusterIndexHealth(indexMetaData, indexRoutingTable);

            indices.put(indexHealth.getIndex(), indexHealth);
        }

        status = ClusterHealthStatus.GREEN;

        for (ClusterIndexHealth indexHealth : indices.values()) {
            activePrimaryShards += indexHealth.getActivePrimaryShards();
            activeShards += indexHealth.getActiveShards();
            relocatingShards += indexHealth.getRelocatingShards();
            initializingShards += indexHealth.getInitializingShards();
            unassignedShards += indexHealth.getUnassignedShards();
            if (indexHealth.getStatus() == ClusterHealthStatus.RED) {
                status = ClusterHealthStatus.RED;
            } else if (indexHealth.getStatus() == ClusterHealthStatus.YELLOW && status != ClusterHealthStatus.RED) {
                status = ClusterHealthStatus.YELLOW;
            }
        }

        if (!validationFailures.isEmpty()) {
            status = ClusterHealthStatus.RED;
        } else if (clusterState.blocks().hasGlobalBlock(RestStatus.SERVICE_UNAVAILABLE)) {
            status = ClusterHealthStatus.RED;
        }

        // shortcut on green
        if (status.equals(ClusterHealthStatus.GREEN)) {
            this.activeShardsPercent = 100;
        } else {
            List<ShardRouting> shardRoutings = clusterState.getRoutingTable().allShards();
            int activeShardCount = 0;
            int totalShardCount = 0;
            for (ShardRouting shardRouting : shardRoutings) {
                if (shardRouting.active()) activeShardCount++;
                totalShardCount++;
            }
            this.activeShardsPercent = (((double) activeShardCount) / totalShardCount) * 100;
        }
    }
```

理解health api, 需要理解clusterState。 好在这些都是只读的信息，不难理解。





