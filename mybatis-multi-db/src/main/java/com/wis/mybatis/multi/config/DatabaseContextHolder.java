package com.wis.mybatis.multi.config;

/**
 * Created by liuBo
 * 2019/12/2.
 */
public class DatabaseContextHolder {
    private static final ThreadLocal<DatabaseType> contextHolder = new ThreadLocal<>();

    public static void setDatabaseType(DatabaseType type){
        contextHolder.set(type);
    }
    public static DatabaseType getDatabaseType(){
        return contextHolder.get();
    }
    public static void remove(){
        contextHolder.remove();
    }

}
