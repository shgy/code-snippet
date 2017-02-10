读取客户端数据块后, `RemoteBlockReader2`会发送`CHECKSUM_OK`确认信息.
DataNode管理的数据有一下几个原语:
block: 比较大, 一般是64M
chunk: A block is divided into chunks, each comes with a checksum.
       We want transfers to be chunk-aligned, to be able to
       verify checksums.
packet:A grouping of chunks used for transport. It contains a
       header, followed by checksum data, followed by real data.