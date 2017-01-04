操作HDFS时, 像 rmname, mkdir 这些操作clent只需要和Namenod进行交互.

整个路径如下:
org.apache.hadoop.hdfs.TestDFSMkdirs.testDFSMkdirs()
 --> DistributedFileSystem.mkdirs()
    --> DFSClient.mkdirs()
      --> NameNodeRpcServer.mkdirs()
         --> FSNamesystem.mkdirs()
            --> FSDirectory.getExistingPathINodes() # 检测是否存在
            --> FSDirectory.unprotectedMkdir()
最后把 INode 添加到FSDirectory的INodeMap中.

验证:
    既然是RPC交互, 那么,如果知道本地HDFS的RPC端口, 就可以直接调用NameNodeRpcServer.mkdirs()方法了.
    [目前对HDFS的代码理解不够, 美没有试成功]

