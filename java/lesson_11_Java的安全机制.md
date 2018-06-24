
什么是安全？

安全就是控制。门锁了，外人进不来，这就是安全。撞车了，有气囊，人没事，这就是安全。
同样，计算机的安全也是如此。没有密码，你登录不进来，这是安全。 没有授权，你访问不了这是安全。

Java使用SecurityManager来控制访问权限，保证安全。

参考样例给得很好。Java的反射功能很强大，能访问private方法，修改private字段的类型。这个操作很危险，如果需要阻止，
就需要用到SecurityManager.




参考:
https://blog.frankel.ch/java-security-manager/
