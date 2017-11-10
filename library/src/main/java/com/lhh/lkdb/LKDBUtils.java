package com.lhh.lkdb;

/**
 * Created by Linhh on 2017/11/6.
 */

public class LKDBUtils {
    public static String getNameByClazz(Class<? extends ILKDBModel> clazz){
        String clazzName = clazz.getName();
        clazzName = clazzName.replace(".", "_");
        return clazzName;
    }
}
