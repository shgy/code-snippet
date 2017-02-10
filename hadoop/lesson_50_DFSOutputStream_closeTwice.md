`testCloseTwice()`的测试用例关联着Hadoop的一个bug, HDFS-5335.
DFSOutputStream的细节还有待挖掘. 这里提出一个问题:
FileOutputStream.close()的实现如下:
```
    public void close() throws IOException {
        synchronized (closeLock) {
            if (closed) {
                return;
            }
            closed = true;
        }
        ......
```
而DFSOutputStream.close()的实现如下:
```
  public synchronized void close() throws IOException {
    if (closed) {
      IOException e = lastException.getAndSet(null);
      if (e == null)
        return;
      else
        throw e;
    }
    ......
```
两者的实现不同, why ?
