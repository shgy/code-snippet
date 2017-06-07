学习Yarn的源码，看到了FSAction 这一枚举类型。 深感设计之巧妙， 就把代码贴出来了。
```
public enum FsAction {
  // POSIX style
  NONE("---"),
  EXECUTE("--x"),
  WRITE("-w-"),
  WRITE_EXECUTE("-wx"),
  READ("r--"),
  READ_EXECUTE("r-x"),
  READ_WRITE("rw-"),
  ALL("rwx");

  /** Retain reference to value array. */
  private final static FsAction[] vals = values();

  /** Symbolic representation */
  public final String SYMBOL;

  private FsAction(String s) {
    SYMBOL = s;
  }

  /**
   * Return true if this action implies that action.
   * @param that
   */
  public boolean implies(FsAction that) {
    if (that != null) {
      return (ordinal() & that.ordinal()) == that.ordinal();
    }
    return false;
  }

  /** AND operation. */
  public FsAction and(FsAction that) {
    return vals[ordinal() & that.ordinal()];
  }
  /** OR operation. */
  public FsAction or(FsAction that) {
    return vals[ordinal() | that.ordinal()];
  }
  /** NOT operation. */
  public FsAction not() {
    return vals[7 - ordinal()];
  }

  /**
   * Get the FsAction enum for String representation of permissions
   *
   * @param permission
   *          3-character string representation of permission. ex: rwx
   * @return Returns FsAction enum if the corresponding FsAction exists for permission.
   *         Otherwise returns null
   */
  public static FsAction getFsAction(String permission) {
    for (FsAction fsAction : vals) {
      if (fsAction.SYMBOL.equals(permission)) {
        return fsAction;
      }
    }
    return null;
  }
}

```

《Think In Java》使用了一整章讲解枚举类型。
首先，枚举也是对象（万物皆对象）。
其特殊之处在于 `创建enum时， 编译器会为你生成一个相关的类，这个类继承自java.lang.Enum`。

ordinal()返回 enum实例在生成时的次序，从0开始。
values() 是有编译器插入到enum定义中的static方法。
