在一些测试场景中, 我们希望知道确切的调用次序. 怎么做呢?
```
		List list = mock(List.class);  
	    List list2 = mock(List.class);  
	    list.add(1);  
	    list2.add("hello");  
	    list.add(2);  
	    list2.add("world");  
	    //将需要排序的mock对象放入InOrder  
	    InOrder inOrder = inOrder(list,list2);  
	    //下面的代码不能颠倒顺序，验证执行顺序  
	    inOrder.verify(list).add(1);  
	    inOrder.verify(list2).add("hello");  
	    inOrder.verify(list).add(2);  
	    inOrder.verify(list2).add("world");  
```
