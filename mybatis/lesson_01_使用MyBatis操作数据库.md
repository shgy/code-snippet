mybatis作为一个优秀的sql mapping框架. 小巧灵活, 入门门槛低. 开发者完全可以先学mybatis, 再学hibernate.
由于需要跟数据库打交道, 所以尽管是入门级的学习教程, 但是步骤还是有点多.
Step1: 创建MySQL的数据库和表,并插入样例数据
```
create database if not exists mybatis default charset utf8 collate utf8_general_ci;

create table if not exists user(
  id int primary key auto_increment,
  name varchar(50)
);
insert into user (name) values('hello');
```
这样,数据端就准备好了, 接下来是工程端.

Step2: 准备配置文件:
```
$ tree resources/
resources/
├── jdbc.properties
├── mappers
│   └── UserMapper.xml
├── mybatis-config.xml
```
一共是3个配置文件.

```
# ---------------jdbc 参数 ----------------

$ cat resources/jdbc.properties 
jdbc.driver=com.mysql.jdbc.Driver
jdbc.url=jdbc:mysql://localhost:3305/mybatis
jdbc.username=root

# ------------------mybatis-config.xml

$ cat resources/mybatis-config.xml 
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
    <properties resource="jdbc.properties"/>
    <typeAliases>
        <typeAlias alias="User" type="com.springapp.mybatis.entity.User"/>
    </typeAliases>
    <environments default="development">
        <environment id="development">
            <transactionManager type="JDBC"/>
            <dataSource type="POOLED">
                <property name="driver" value="${jdbc.driver}"/>
                <property name="url" value="${jdbc.url}"/>
                <property name="username" value="${jdbc.username}"/>
                <property name="password" value="${jdbc.password}"/>
            </dataSource>
        </environment>
    </environments>
    <mappers>
        <mapper resource="mappers/UserMapper.xml"/>
    </mappers>
</configuration>

# ----------------------------- mappers/UserMapper.xml

$ cat resources/mappers/UserMapper.xml 
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.springapp.mybatis.dao.UserDao">
  <select id="findUserById" parameterType="Integer" resultType="User">
    select * from user where id=#{id}
  </select>
</mapper> 

```

Step3: 代码端
```
$ tree java/com/springapp/mybatis/
java/com/springapp/mybatis/
├── dao
│   └── UserDao.java
├── entity
│   └── User.java
├── main
│   └── Main.java
└── util
    └── SqlSessionFactoryUtil.java

4 directories, 4 files
```
一共是4个代码文件, 结构非常清晰.
```
$ cat java/com/springapp/mybatis/entity/User.java 
package com.springapp.mybatis.entity;

/**
 * Created by shgy on 18-4-15.
 */
import java.io.Serializable;

@SuppressWarnings("serial")
public class User implements Serializable{

    private Integer id;
    private String password;

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public User(Integer id, String password) {
        super();
        this.id = id;
        this.password = password;
    }
    public User() {
        super();
        // TODO Auto-generated constructor stub
    }
}

$ cat java/com/springapp/mybatis/dao/UserDao.java 
package com.springapp.mybatis.dao;

import com.springapp.mybatis.entity.User;

/**
 * Created by shgy on 18-4-15.
 */
public interface UserDao {
    User findUserById(Integer id);
}

$ cat java/com/springapp/mybatis/util/SqlSessionFactoryUtil.java 
package com.springapp.mybatis.util;

/**
 * Created by shgy on 18-4-15.
 */
import java.io.InputStream;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public class SqlSessionFactoryUtil {

    private static SqlSessionFactory sqlSessionFactory;

    public static SqlSessionFactory getSqlSessionFactory(){
        if(sqlSessionFactory==null){
            InputStream inputStream=null;
            try{
                inputStream=Resources.getResourceAsStream("mybatis-config.xml");
                sqlSessionFactory=new SqlSessionFactoryBuilder().build(inputStream);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return sqlSessionFactory;
    }

    public static SqlSession openSession(){
        return getSqlSessionFactory().openSession();
    }
}


$ cat java/com/springapp/mybatis/main/Main.java 
package com.springapp.mybatis.main;

import com.springapp.mybatis.dao.UserDao;
import com.springapp.mybatis.entity.User;
import com.springapp.mybatis.util.SqlSessionFactoryUtil;
import org.apache.ibatis.session.SqlSession;

/**
 * Created by shgy on 18-4-15.
 */
public class Main {
    public static void main(String[] args) {
        SqlSession sqlSession= SqlSessionFactoryUtil.openSession();
        UserDao userDao = sqlSession.getMapper(UserDao.class);
        Integer id = 1;
        User curUser = userDao.findUserById(id);
        if(curUser!=null){
            System.out.println("HelloWorld:"+curUser.getId());
        }
    }
}

```
看着文件多, 3个配置+4个Java代码文件. 实际上, 每个文件承载的东西都非常轻, 职责十分清晰.


参考:
https://blog.csdn.net/abcd898989/article/details/51174325

