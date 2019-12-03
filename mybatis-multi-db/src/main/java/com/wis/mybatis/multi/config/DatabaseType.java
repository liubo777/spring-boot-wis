package com.wis.mybatis.multi.config;

/**
 * Created by liuBo
 * 2019/12/2.
 */
public enum DatabaseType {
    one("db1"),
    two("db2"),
    three("db3"),
    four("db4"),
    five("db5"),
    six("db6"),
    seven("db7"),
    eight("db8"),
    nine("db9"),
    ten("db10");


    DatabaseType(String name){
        this.name = name;
    }

    static  DatabaseType pickType(String name){
        switch (name){
            case "db1":
                return DatabaseType.one;
            case "db2":
                return DatabaseType.two;
            case "db3":
                return DatabaseType.three;
            case "db4":
                return DatabaseType.four;
            case "db5":
                return DatabaseType.five;
            case "db6":
                return DatabaseType.six;
            case "db7":
                return DatabaseType.seven;
            case "db8":
                return DatabaseType.eight;
            case "db9":
                return DatabaseType.nine;
            case "db10":
                return DatabaseType.ten;
        }
        return DatabaseType.one;

    }

    private String name;

    public String getName(){
        return name;
    }





}
