### 简单介绍 ###
作用是可以动态的修改日志等级，需要再application.yml中配置

#####application.yml:
```$xslt
wis:
  curatorlog:
    config:
      enabled: true
      connect-string: your zk ip:port
      namespace: ${spring.application.name}
      watcher-path: log    
```
#####pom:
```xml
    <repositories>
        <repository>
            <name>mvn-release-repository</name>
            <id>mvn-release-repository</id>
            <url>https://raw.github.com/liubo777/mvn-repository/release</url>
        </repository>
    </repositories>
    
    <dependencies>
         <dependency>
              <groupId>com.wis.mybatis</groupId>
              <artifactId>mybatis-multi-db</artifactId>
              <version>1.0</version>
         </dependency>        
    </dependencies>        
    
```

该项目基druid的starter做了一些额外的配置。
目前最多支持10个数据源，配置位置如下：
数据源的配置参数和druid的配置完全相同
```yaml
spring:
  datasource:
    druid:
      db1: 
        xxx: xxx
        xxx: xxx
        ...: xxx
      db2:
        xxx: xxx
        xxx: xxx
        ...: xxx
      ...
      db10:
         ...         
```

db1 是默认数据源
在需要切换数据源的方法上加注释就可以了，在相应的mapper或者service方法上加注解就可以了
因为有默认数据源，所以仅在需要更改数据源的方法上加注释就可以了。
```java

    @Select("select * from a where loginname = #{username}")
    @DynamicDataSourceAnnotation(DatabaseType.two)
    List findLoginDtoByuserName(@Param("username") String userName);

```
