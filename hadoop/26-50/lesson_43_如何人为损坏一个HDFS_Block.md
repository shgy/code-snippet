hdfs在测试的时候, 会人为损坏block, 来测试各个模块是否会按照预想的方式工作. 如何人为损坏block呢?
```
Corrupt a block on a data node. Replace the block file content with content
   * of 1, 2, ...BLOCK_SIZE.

```
直接找到block对应的文件
```
current/BP-1484840750-127.0.0.1-1483863656181/current/finalized/subdir0/subdir0/blk_1073741825

 final File f = DataNodeTestUtils.getBlockFile(
        dn, block.getBlockPoolId(), block.getLocalBlock());
    final RandomAccessFile raFile = new RandomAccessFile(f, "rw");
    final byte[] bytes = new byte[(int) BLOCK_SIZE];
    for (int i = 0; i < BLOCK_SIZE; i++) {
      bytes[i] = (byte) (i);
    }
    raFile.write(bytes);
    raFile.close();

```
其中 getBlockFile()方法如下:
```
 public static File getBlockFile(FsDatasetSpi<?> fsd, String bpid, Block b
      ) throws IOException {
    return ((FsDatasetImpl)fsd).getBlockFile(bpid, b.getBlockId());
  }
```
这样 block在读取的时候,就会checksum失败.


测试中有: 损坏