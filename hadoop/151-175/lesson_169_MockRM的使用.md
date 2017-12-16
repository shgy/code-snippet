在Yarn中， RM, NM都是在各自的进程中。 如何方便编写测试用例呢？  这就需要用到MockRM和MockNM

先看TestRM中，Mockito 自定义的参数匹配器如何使用。 假设有一个服务ServiceA， 依赖一个模块ModuleA.

在测试的时候， 如果参数满足某种规则， 比如等于"asdf", 则不调用ModuleA的funcA()方法， 代码样例如下：
```
package shgy;

import org.mockito.ArgumentMatcher;

import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;

/**
 * Created by shgy on 17-11-19.
 */
class ModuleA{

    public void funcA(String aa){
        System.out.println(aa + " hello world\n");
    }
}

class ServiceA{

    private ModuleA m;

    public void init(){
        this.m = createModuleA();
    }
    protected ModuleA createModuleA(){

        return new ModuleA();
    }

    public void func(String arg){
        m.funcA(arg);
    }
}
public class TestArgMatcher {



    public static void main(String[] args) {

        ServiceA as = new ServiceA(){
            /*
            *  如果 参数符合match的规则， 那么
            * */
            class MyArgMatcher extends ArgumentMatcher<String> {
                @Override
                public boolean matches(Object argument) {
                    if (argument.equals("asdf")) {
                            return true;
                    }
                    return false;
                }
            }

            class MyArgMatcher2 extends ArgumentMatcher<Integer> {
                @Override
                public boolean matches(Object argument) {
                    if (argument instanceof Integer) {
                        if(((Integer) argument).intValue() < 10)
                        return true;
                    }
                    return false;
                }
            }

            @Override
            public ModuleA createModuleA(){
                 ModuleA ma = spy(new ModuleA());
                doNothing().when(ma).funcA(argThat(new MyArgMatcher()));
                return ma;
            }
        };

        as.init();

        as.func("asdf");
    }
}

```

这里有个疑问： 如果该方法有多个参数， 怎么处理呢？
