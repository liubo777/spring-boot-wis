package com.wis.mybatis.multi.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

/**
 * Created by liuBo
 * 2019/12/3.
 */
@Component
@Aspect
@Slf4j
public class DynamicDataSourceAspect {
    @Before("@annotation(DynamicDataSourceAnnotation)")
    public void before(JoinPoint joinPoint){

        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
        DynamicDataSourceAnnotation annotation = signature.getMethod().getAnnotation(DynamicDataSourceAnnotation.class);
        if (annotation!=null){
            DatabaseType type = annotation.value();
            log.debug(joinPoint.getTarget().getClass()+"("+signature.getMethod().getName()+")-->切换数据源为"+type.getName());
            DatabaseContextHolder.setDatabaseType(type);
        }

    }
    @After("@annotation(DynamicDataSourceAnnotation)")
    public void after(JoinPoint joinPoint){
        DatabaseContextHolder.remove();
    }
}
