问题1: 
Failed to execute goal on project oozie-core: Could not resolve dependencies for project org.apache.oozie:oozie-core:jar:4.2.0: Could not transfer artifact org.apache.hbase:hbase:jar:1.1.1 from/to Codehaus repository (http://repository.codehaus.org/): Unknown host repository.codehaus.org

The Codehaus hosting platform was ended, i.e., their public Maven repository is gone, too. You should try to follow their advice and add the following to your ~/.m2/settings.xml file:

<repositories>
     <repository>
       <id>Codehaus repository</id>
       <name>codehaus-mule-repo</name>
       <url>https://repository-master.mulesoft.org/nexus/content/groups/public/
       </url>
       <layout>default</layout>
     </repository>
   </repositories>
This should use a backup repository to get the missing dependency.

问题2:

/HCatURIHandler.java:[273,47] error: cannot find symbol

./mkdistro.sh -Phadoop-2 -DskipTests -Dhadoop.auth.version=2.6.0 -Ddistcp.version=2.6.0 -Dsqoop.version=1.4.4 -Dhive.version=0.13.1 -Dpig.version=0.15.0

hive不要使用1.1.1的版本, 而要用老一点的版本. 现阶段, 感觉oozie稍难了一些. 先学习hadoop基础.
