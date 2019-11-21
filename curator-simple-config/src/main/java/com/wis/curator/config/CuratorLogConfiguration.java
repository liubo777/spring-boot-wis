package com.wis.curator.config;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liuBo
 * 2019/11/21.
 */

@ConditionalOnProperty(name = "wis.curatorlog.config.enabled" , havingValue = "true")
@Configuration
@Slf4j
public class CuratorLogConfiguration {
    @Value("${wis.curatorlog.config.connect-string:}")
    private String connectString;
    @Value("${wis.curatorlog.config.namespace:}")
    private String namespace;
    @Value("${wis.curatorlog.config.watcher-path:}")
    private String logpath;
    @Autowired(required = false)
    private CuratorFramework curatorFramework;
    @PostConstruct
    public void init(){
        if (curatorFramework==null){
            log.warn("curatorFramework is null so doesn`t register watcher from zk");
            return ;
        }
        String path = "/"+logpath;
        addListeners(path);
    }
    private void addListeners(String path){
        try {
            PathChildrenCache cache = new PathChildrenCache(curatorFramework,path,true);
            cache.start();
            List<ChildData> childData = cache.getCurrentData();
            initLogLevel(childData);
            cache.getListenable().addListener(((client, event) -> {
                switch (event.getType()){
                    case CHILD_ADDED:
                        String addPath = event.getData().getPath();
                        setLogLevelByPath(addPath);
                        break;
                    case CHILD_UPDATED:
                        String updPath = event.getData().getPath();
                        setLogLevelByPath(updPath);
                        break;
                    case CHILD_REMOVED:
                        String delath = event.getData().getPath();
                        removeLogLevelByPath(delath);
                        break;
                    default:
                        break;
                }
            }));
        } catch (Exception e) {
            log.debug(e.getMessage());
        }
    }
    private void initLogLevel(List<ChildData> childData){
        try {

            childData.stream().forEach(childByte ->{
                setLogLevelByPath(new String(childByte.getPath()));
            });
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private void setLogLevelByPath(String child){
        String packageName = child.substring(child.lastIndexOf("/")+1);
        Logger logger = (Logger) LoggerFactory.getLogger(packageName);
        try {
            byte[] vB = curatorFramework.getData().forPath(child);
            String v = new String(vB).trim().toLowerCase();
            log.debug("set "+packageName+" `s level "+v+"("+levelMap.get(v)+")");
            logger.setLevel(levelMap.get(v));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private void removeLogLevelByPath(String child){
        Logger logger = (Logger) LoggerFactory.getLogger(child);
        try {
            logger.setLevel(null);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private static Map<String, Level> levelMap = new HashMap(){
        {
            put("info",Level.INFO);
            put("debug",Level.DEBUG);
            put("warn",Level.WARN);
            put("error",Level.ERROR);
            put("all",Level.ALL);
        }
    };

    @Bean
    @ConditionalOnMissingBean
    public CuratorFramework curatorFramework(){
        if ("".equals(connectString)||"".equals(namespace)||"".equals(logpath)){
            return null;
        }
        CuratorFramework cc = CuratorFrameworkFactory.builder()
                .connectString(connectString)
                .sessionTimeoutMs(2000)
                .connectionTimeoutMs(5000)
                .retryPolicy(new ExponentialBackoffRetry(1000,3))
                .namespace(namespace)
                .build();
        cc.start();
        return cc;
    }

}
