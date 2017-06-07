```
import org.junit.Test;
import org.mockito.ArgumentMatcher;

import java.util.Arrays;
import java.util.List;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.verify;
/**
 * Created by shgy on 17-6-1.
 * argThat用来 判别 函数的参数是否符合 定义的规则。
 * 比如： 下面的例子要求 list.addAll()的参数 size 必须是2.
 */
public class ArgThatDemo {
    // 自定义的规则
    class IsListOfTwoElements extends ArgumentMatcher<List> {
        public boolean matches(Object list) {
            return ((List) list).size() == 2;
        }
    }

    @Test
    public void argumentMatchersTest(){
        List mock = mock(List.class);
        // 设定规则
        when(mock.addAll(argThat(new IsListOfTwoElements()))).thenReturn(true);

        mock.addAll(Arrays.asList("one", "two", "three"));

        // 验证 失败
        verify(mock).addAll(argThat(new IsListOfTwoElements()));
        
//        Expected :list.addAll(<Is list of two elements>);
//        Actual   :list.addAll([one, two, three]);
    }
}
```
