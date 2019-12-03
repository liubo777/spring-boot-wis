### 简单介绍 
作用是可以动态的修改日志等级，需要再application.yml中配置

#### application.yml:   
```$xslt
wis:
  curatorlog:
    config:
      enabled: true
      connect-string: your zk ip:port
      namespace: ${spring.application.name}
      watcher-path: log    
```
#### pom:  
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
            <groupId>com.wis.curator</groupId>
            <artifactId>curator-simple-config</artifactId>
            <version>1.0</version>
        </dependency>            
    </dependencies>        
    
```

#### 备注 
1. /${spring.application.name}/log/下的节点，比如
K-V为 com.xxx.xxx      debug，
那么会自动将com.xxx.xxx的日志等级设为debug。
2. 日志使用的lombok封装的slf4j。
3. log可以在watcher-path中重新指定。
4. V的值分为5种 info、debug、warn、error、all。
