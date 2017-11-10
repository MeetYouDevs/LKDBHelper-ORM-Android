package com.lhh.lkdb.android;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.lhh.lkdb.ISQLiteOpenHelper;
import com.lhh.lkdb.LKDBWrapper;

/**
 * Created by Linhh on 2017/11/7.
 */

public class AndroidSQLiteOpenHelper extends android.database.sqlite.SQLiteOpenHelper implements ISQLiteOpenHelper {
    private final Context mContext;
    private final String mName;
    private final int mVersion;
    private final AndroidSQLiteDatabaseWrapper mAndroidSQLiteDatabaseWrapper;

    public AndroidSQLiteOpenHelper(Context context, String name, int version) {
        super(context, name, null, version);
        mContext = context;
        mName = name;
        mVersion = version;
        getReadableDatabase().close();
        mAndroidSQLiteDatabaseWrapper = new AndroidSQLiteDatabaseWrapper(getWritableDatabase());
//        mAndroidSQLiteDatabaseWrapper.close();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    @Override
    public Context getContext() {
        return mContext;
    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public int getVersion() {
        return mVersion;
    }

    @Override
    public LKDBWrapper getDBWrapper() {
        return mAndroidSQLiteDatabaseWrapper;
    }
}
