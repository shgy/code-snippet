日志是一个项目非常基础, 非常重要的功能. 清晰有调理的日志, 能帮助开发者快速定位bug.
1. 引入相关的jar包
```
	<properties>
		<spring.version>4.1.6.RELEASE</spring.version>
		<log4j.version>1.2.17</log4j.version>
	</properties>

	<dependencies>

		<!-- Spring -->
		<dependency>
			<groupId>org.springframework</groupId>
			<artifactId>spring-webmvc</artifactId>
			<version>${spring.version}</version>
		</dependency>

		<!-- Log4j -->
		<dependency>
			<groupId>log4j</groupId>
			<artifactId>log4j</artifactId>
			<version>${log4j.version}</version>
		</dependency>
	<dependencies>
```

2. log4j.properties
```
# Root logger option
log4j.rootLogger = DEBUG, stdout, file

# Redirect log messages to console
log4j.appender.stdout = org.apache.log4j.ConsoleAppender
log4j.appender.stdout.Target = System.out
log4j.appender.stdout.layout = org.apache.log4j.PatternLayout
log4j.appender.stdout.layout.ConversionPattern = %d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n

# Redirect log messages to a log file
log4j.appender.file = org.apache.log4j.RollingFileAppender
#outputs to Tomcat home
log4j.appender.file.File = ${catalina.home}/logs/myapp.log
log4j.appender.file.MaxFileSize = 5MB
log4j.appender.file.MaxBackupIndex = 10
log4j.appender.file.layout = org.apache.log4j.PatternLayout
log4j.appender.file.layout.ConversionPattern = %d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n
```

3. 记录日志
```
@Controller
@RequestMapping(name="/")
public class IndexController {
	private static final Logger LOGGER = Logger.getLogger(IndexController.class);
	@RequestMapping(method=RequestMethod.GET)
	public String index(ModelMap model) {
		String catalina_home = System.getProperty("catalina.home");
		LOGGER.info("printHello started."+catalina_home);

		//logs debug message
		if(LOGGER.isDebugEnabled()){
			LOGGER.debug("Inside:  printHello");
		}
		//logs exception
		LOGGER.error("Logging a sample exception", new Exception("Testing"));
		model.addAttribute("msg", "Hello world!");
		return "index";
	}
}
```

通过输出catalina_home, 这样就能知道日志输出的位置了.