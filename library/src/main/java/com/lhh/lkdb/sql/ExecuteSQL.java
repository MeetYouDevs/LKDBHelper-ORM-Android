package com.lhh.lkdb.sql;

/**
 * Created by Linhh on 2017/11/6.
 */

public final class ExecuteSQL {
    private final String mSQL;
    private Object[] mObjs;
    private ExecuteSQL(String sql){
        mSQL = sql;
    }

    public static ExecuteSQL from(String sql){
        return new ExecuteSQL(sql);
    }

    public ExecuteSQL arguments(Object[] objects){
        mObjs = objects;
        return this;
    }

    public String getSQL(){
        return mSQL;
    }

    public Object[] getArguments(){
        return mObjs;
    }
}
