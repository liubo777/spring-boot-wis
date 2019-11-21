### 简单介绍 ###
作用是可以动态的修改日志等级，需要再application.yml中配置

```$xslt
wis:
  curatorlog:
    config:
      enabled: true
      connect-string: your zk ip:port
      namespace: ${spring.application.name}
      watcher-path: log    
```

使用的节点是
/${spring.application.name}/log/下的节点，比如
K-V为
com.xxx.xxx      debug
那么会自动将com.xxx.xxx的日志等级设为debug
日志使用的lombok封装的slf4j
