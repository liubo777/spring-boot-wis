package com.wis.mybatis.multi.config;

import java.lang.annotation.*;

/**
 * Created by liuBo
 * 2019/12/3.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface DynamicDataSourceAnnotation {
    DatabaseType value();
}
