package com.lhh.lkdb;

import java.util.List;

/**
 * Created by Linhh on 2017/11/9.
 */

public class LKDBLifecycle {
    private static LKDBLifecycleListener mLKDBLifecycleListener;

    public static void setLKDBLifecycleListener(LKDBLifecycleListener lkdbLifecycleListener){
        mLKDBLifecycleListener = lkdbLifecycleListener;
    }

    public static void dbDidDeleted(String tableName, boolean result){
        if(mLKDBLifecycleListener != null){
            mLKDBLifecycleListener.dbDidDeleted(tableName, result);
        }
    }

    public static void dbDidAlterTable(String tableName, List<String> alterAddColumns){
        if(mLKDBLifecycleListener != null){
            mLKDBLifecycleListener.dbDidAlterTable(tableName, alterAddColumns);
        }
    }
}
