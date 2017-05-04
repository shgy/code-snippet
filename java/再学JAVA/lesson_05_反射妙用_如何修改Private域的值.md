```
import java.lang.reflect.Field;

class Employee{
	private String name = "employee";

	public String getName(){
		return name;
	}
}


public class NoMain {

    public static void main(String[] args) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
    	Employee e = new Employee();
    	Field f = e.getClass().getDeclaredField("name");
    	 f.setAccessible(true);
    	System.out.println(e.getName());
    	f.set(e, "manager");
    	System.out.println(e.getName());

    	System.out.println("==========================================");

    	Method m = e.getClass().getDeclaredMethod("setName", String.class);
    	m.setAccessible(true);
    	m.invoke(e, "newName");
    	System.out.println(e.getName());
	}
}

```