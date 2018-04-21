用_bulk批量导入数据一定要注意换行的问题。如果数据没有换行，会导致

{"error":"ActionRequestValidationException[Validation Failed: 1: no requests add

ed;]","status":500}

错误的出现。对于数据是否真正换行，在windows下面，可以用记事本打开，一看便知。



该错误出现的地方，通过源码跟踪的方式，可以看到，在BulkRequest.add()方法中(第243行)，可以看到，读取数据的时候，如果是JSON文件，则是以 \n 作为标记的。
