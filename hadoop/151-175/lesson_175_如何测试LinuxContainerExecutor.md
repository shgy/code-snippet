
如何测试LinuxContainerExecutor ?
```
/**
 * This is intended to test the LinuxContainerExecutor code, but because of
 * some security restrictions this can only be done with some special setup
 * first.
 * <br><ol>
 * <li>Compile the code with container-executor.conf.dir set to the location you
 * want for testing.
 * <br><pre><code>
 * > mvn clean install -Pnative -Dcontainer-executor.conf.dir=/etc/hadoop
 *                          -DskipTests
 * </code></pre>
 * 
 * <li>Set up <code>${container-executor.conf.dir}/container-executor.cfg</code>
 * container-executor.cfg needs to be owned by root and have in it the proper
 * config values.
 * <br><pre><code>
 * > cat /etc/hadoop/container-executor.cfg
 * yarn.nodemanager.linux-container-executor.group=mapred
 * #depending on the user id of the application.submitter option
 * min.user.id=1
 * > sudo chown root:root /etc/hadoop/container-executor.cfg
 * > sudo chmod 444 /etc/hadoop/container-executor.cfg
 * </code></pre>
 * 
 * <li>Move the binary and set proper permissions on it. It needs to be owned 
 * by root, the group needs to be the group configured in container-executor.cfg, 
 * and it needs the setuid bit set. (The build will also overwrite it so you
 * need to move it to a place that you can support it. 
 * <br><pre><code>
 * > cp ./hadoop-yarn-project/hadoop-yarn/hadoop-yarn-server/hadoop-yarn-server-nodemanager/src/main/c/container-executor/container-executor /tmp/
 * > sudo chown root:mapred /tmp/container-executor
 * > sudo chmod 4550 /tmp/container-executor
 * </code></pre>
 * 
 * <li>Run the tests with the execution enabled (The user you run the tests as
 * needs to be part of the group from the config.
 * <br><pre><code>
 * mvn test -Dtest=TestLinuxContainerExecutor -Dapplication.submitter=nobody -Dcontainer-executor.path=/tmp/container-executor
 * </code></pre>
 * </ol>
 */
```

待验证。。。