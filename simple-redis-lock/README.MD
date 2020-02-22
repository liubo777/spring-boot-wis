### 简单介绍  
基于redisson的分布式锁实现，参数参考[WisRedisProperty](https://github.com/liubo777/spring-boot-wis/blob/master/simple-redis-lock/src/main/java/com/wis/redis/pojo/WisRedisProperty.java)

#### application.yml: 
```$xslt
wis:
  redisson:
    password: xxxxxx
    singleServerProperty:
      address: ip:port
      connectionMinimumIdleSize: 2
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
                     <groupId>com.wis.redis</groupId>
                     <artifactId>simple-redis-lock</artifactId>
                     <version>1.0</version>
                 </dependency> 
    </dependencies>        
    
```
 
#### 使用:  
```java

    @RedisLock(attemptTimeout = 20000l,lockWatchdogTimeout = 30000l,index = 0,prefix = LOCK_PREFIX)
    public boolean removeNumber(String type,String id){
                int rank = numberIndex(type, id);
                return redisTemplate.opsForZSet().removeRange(type,rank,rank)==1l;
    }    

```
 
##### 注意：  

