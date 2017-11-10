package com.lhh.lkdb.android;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.lhh.lkdb.ILKDBModel;
import com.lhh.lkdb.LKDBTableUtils;
import com.lhh.lkdb.LKDBWrapper;
import com.lhh.lkdb.sql.LKDBQueryParams;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Linhh on 2017/11/7.
 */

public final class AndroidSQLiteDatabaseWrapper extends LKDBWrapper {

    private static final String TAG = "AndroidSQLiteDatabase";

    private SQLiteDatabase mSQLiteDatabase;
    public AndroidSQLiteDatabaseWrapper(SQLiteDatabase sqLiteDatabase){
        mSQLiteDatabase= sqLiteDatabase;
    }

    @Override
    public boolean close(){
        mSQLiteDatabase.close();
        return true;
    }

    @Override
    public boolean executeSQL(String sql) {
        Log.i(TAG, "executeSQL:" + sql);
       return executeSQL(sql,null);
    }

    @Override
    public boolean executeSQL(String sql, List<Object> objs) {
        Log.i(TAG, "executeSQL and objs:" + sql);
        try {
            if (objs == null) {
                mSQLiteDatabase.execSQL(sql);
                return true;
            }
            mSQLiteDatabase.execSQL(sql, objs.toArray());
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Cursor executeQuery(String sql) {
        Log.i(TAG, "executeQuery:" + sql);
        return executeQuery(sql, null);
    }

    @Override
    public Cursor executeQuery(String sql, List<Object> objects) {
        Log.i(TAG, "executeQuery and objs:" + sql);
        if(objects == null){
            return mSQLiteDatabase.rawQuery(sql, null);
        }
        return mSQLiteDatabase.rawQuery(sql, LKDBTableUtils.listToStrings(objects));
    }

    @Override
    public List<String> onQueryDBTable() {
        List<String> tmp = new ArrayList<String>();
        String[] columns = new String[1];
        columns[0] = "name";
        String selection = "type=\"table\"";

        Cursor cur = mSQLiteDatabase.query("sqlite_master", columns, selection, null, null, null, null);
        if (null == cur) {
            Log.e(TAG, "[onQueryTable] fail to query the table sqlite_master");
        } else if (cur.moveToFirst()) {
            do {
                tmp.add(cur.getString(0));
            } while (cur.moveToNext());

        }
        if (null != cur)
            cur.close();

        return tmp;
    }

    @Override
    public boolean beginTransaction() {
        mSQLiteDatabase.beginTransaction();
        return true;
    }

    @Override
    public boolean endTransaction() {
        mSQLiteDatabase.endTransaction();
        return true;
    }

    @Override
    public boolean getTableCreatedWithClass(Class<? extends ILKDBModel> clazz) {
        String tableName = LKDBTableUtils.getTableByClass(clazz);
        return tableIsExist(tableName);
    }

    @Override
    public boolean getTableCreatedWithTableName(String tableName) {
        return tableIsExist(tableName);
    }

    @Override
    public boolean dropAllTable() {
        List<String> list = onQueryDBTable();
        String element = null;
        List<String> tmp = new ArrayList<String>();
        for (Iterator<String> it = list.iterator(); it.hasNext();) {
            element = it.next();
            if ( 0 == element.compareTo("android_metadata")
                    || 0 == element.compareTo("sqlite_sequence")) {
                continue;
            } else {
                tmp.add(element);
            }
        }

        for (Iterator<String> it = tmp.iterator(); it.hasNext();) {
            element = it.next();
            deleteTable(element);
        }
        if(null != tmp){
            tmp.clear();
        }
        if(null != list){
            list.clear();
        }
        return true;
    }

    @Override
    public <T extends ILKDBModel> List<T> search(Class<T> modelClass, String sql, List<Object> objects) {
        sql = LKDBTableUtils.replaceTableNameIfNeeded(sql,modelClass);
        Cursor cursor = null;
        if(objects == null){
            cursor = mSQLiteDatabase.rawQuery(sql, null);
        }else {
            cursor = mSQLiteDatabase.rawQuery(sql, LKDBTableUtils.listToStrings(objects));
        }
        return executeResult(cursor, modelClass);
    }

    @Override
    public <T extends ILKDBModel> List<T> searchWithSQL(String sql, Class<T> toClass) {
        return search(toClass, sql, null);
    }

    @Override
    public <T extends ILKDBModel> List<T> search(Class<T> modelClass, Object column, Object where, String orderBy, int offset, int count) {
        return searchBase(modelClass, column, where, orderBy, offset, count);
    }

    @Override
    public <T extends ILKDBModel> List<T> searchWithRAWSQL(String sql, Class<T> toClass) {
        Cursor cursor = mSQLiteDatabase.rawQuery(sql, null);
        return executeResult(cursor, toClass);
    }

    @Override
    public <T extends ILKDBModel> List<T> searchWithParams(LKDBQueryParams<T> params) {
        return searchBaseWithParams(params);
    }

    @Override
    public <K extends ILKDBModel> List<K> search(Class<K> modelClass, Object where, String orderBy, int offset, int count) {
        return searchBase(modelClass,null, where,orderBy, offset, count);
    }

    public boolean tableIsExist(String tableName){
//        if(mCreatedTableNames.containsKey(tableName)){
//            return true;
//        }
        boolean result = false;
        if(TextUtils.isEmpty(tableName)){
            return false;
        }
        Cursor cursor = null;
        try {
            String sql = "select count(*) as c from sqlite_master  where type ='table' and name ='"+tableName.trim()+"' ";
            cursor = mSQLiteDatabase.rawQuery(sql, null);
            if(cursor.moveToNext()){
                int count = cursor.getInt(0);
                if(count>0){
                    result = true;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected long executeInsert(String tableName, Map<String, Object> arg) {
        ContentValues contentValues = null;
        if(arg != null) {
            contentValues = new ContentValues();
            for (String key : arg.keySet()) {
                Object v = arg.get(key);
                if (v.getClass() == int.class || v.getClass() == Integer.class) {
                    contentValues.put(key, (Integer) v);
                } else {
                    contentValues.put(key, (String) v);
                }
            }
        }
        return mSQLiteDatabase.replace(tableName, null, contentValues);
    }
}
