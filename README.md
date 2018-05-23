##项目概述
这是一个多工具集合的项目，包含了接入微信和支付宝支付的核心代码，单点登录服务cas的客户端代码，shiro基本使用方法，以及一个坑爹的第三方支付系统《宝付》
##日志配置
springboot默认使用slf4j的日志工具，默认加载名字为logback-spring.xml的日志配置文件。logback的日志文件路径最好在logback-spring.xml中配置。另外，logback日志配置支持多环境配置如下：```xml
<springProfile name="localtest">
     <property name="LOG_HOME" value="D:/logs/${APPLICATION_NAME}"/>
     <property name="LOG_COMMON_HISTORY" value="1"/>
     <property name="LOG_ERROR_HISTORY" value="1"/>
</springProfile>
<springProfile name="prod">
     <property name="LOG_HOME" value="/home/logs/atesi/${APPLICATION_NAME}"/>
     <property name="LOG_COMMON_HISTORY" value="5"/>
     <property name="LOG_ERROR_HISTORY" value="10"/>
</springProfile>
```