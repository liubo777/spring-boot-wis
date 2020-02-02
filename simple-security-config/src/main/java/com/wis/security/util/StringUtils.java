package com.wis.security.util;

/**
 * Created by liuBo
 * 2020/1/30.
 */
public class StringUtils {
    public static boolean isEmpty(String str){
        return str==null||"".equals(str);
    }

    public static boolean isNotEmpty(String str){
        return !isEmpty(str);
    }
}
