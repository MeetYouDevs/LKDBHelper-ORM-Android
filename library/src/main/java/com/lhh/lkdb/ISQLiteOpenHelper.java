package com.lhh.lkdb;

import android.content.Context;

/**
 * Created by Linhh on 2017/11/7.
 */

public interface ISQLiteOpenHelper {
    public Context getContext();
    public String getName();
    public int getVersion();
    public LKDBWrapper getDBWrapper();
}
