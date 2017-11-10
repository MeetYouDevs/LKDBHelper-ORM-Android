package com.lhh.lkdb;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.lhh.lkdb.android.AndroidSQLiteOpenHelper;
import com.lhh.lkdb.sql.LKDBQueryParams;
import com.lhh.lkdb.sql.Transaction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Linhh on 2017/11/6.
 */

public class LKDBHelper implements ILKDBHelper{
    private boolean s_LogError = false;
    private boolean s_NullToEmpty = false;
    private final static String TAG = "LKDBHelper";

    private String mEncryptionKey;//后续提供的加密功能
    private final ISQLiteOpenHelper mISQLiteOpenHelper;

    private static Map<String, ILKDBHelper> s_lkdbHelperMap = new HashMap<>();

    private static Context mContext;

    public static void init(Context context){
        if(mContext != null){
            Log.e(TAG ,"context is exists!!! don't init more than twice");
        }
        mContext = context;
    }

    public static ILKDBHelper getHelper(String dbname){
        synchronized (LKDBHelper.class){
            if(!dbname.endsWith(".db")){
                dbname = dbname + ".db";
            }
            if(s_lkdbHelperMap.containsKey(dbname)){
                return s_lkdbHelperMap.get(dbname);
            }else{
                LKDBHelper lkdbHelper = new LKDBHelper(mContext, dbname);
                s_lkdbHelperMap.put(dbname, lkdbHelper);
                return lkdbHelper;
            }
        }
    }

    public void setLogError(boolean logError){
        s_LogError = logError;
    }

    public void setNullToEmpty(boolean nullToEmpty){
        s_NullToEmpty = nullToEmpty;
    }

    LKDBHelper(Context context, String dbname){
        this(context, dbname, 1);
    }

    LKDBHelper(Context context, String dbname, int version){
        //TODO:if there has another sql application, change the impl
        mISQLiteOpenHelper = new AndroidSQLiteOpenHelper(context, dbname, version);
    }

    @Override
    public boolean close() {
        try {
            mISQLiteOpenHelper.getDBWrapper().close();
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean executeSQL(String sql) {
        try {
            mISQLiteOpenHelper.getDBWrapper().executeSQL(sql);
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean executeSQL(String sql, List<Object> objects) {
        try {
            mISQLiteOpenHelper.getDBWrapper().executeSQL(sql, objects);
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Cursor executeQuery(String sql) {
        try {
            return mISQLiteOpenHelper.getDBWrapper().executeQuery(sql);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Cursor executeQuery(String sql, List<Object> objects) {
        try {
            return mISQLiteOpenHelper.getDBWrapper().executeQuery(sql,objects);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void clearTableData(Class<? extends ILKDBModel> clazz) {
        try{
            mISQLiteOpenHelper.getDBWrapper().clearTableData(clazz);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public <T extends ILKDBModel> boolean updateToDB(T model, Object where) {
        try{
            mISQLiteOpenHelper.getDBWrapper().updateToDB(model, where);
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updateToDBWithTableName(String tableName, String sets, Object where) {
        try{
            mISQLiteOpenHelper.getDBWrapper().updateToDBWithTableName(tableName, sets, where);
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updateToDB(Class<? extends ILKDBModel> clazz, String set, Object where) {
        try{
            mISQLiteOpenHelper.getDBWrapper().updateToDB(clazz, set, where);
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<String> onQueryDBTable() {
        try {

            return mISQLiteOpenHelper.getDBWrapper().onQueryDBTable();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean beginTransaction() {
        try {
            mISQLiteOpenHelper.getDBWrapper().beginTransaction();
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean endTransaction() {
        try {
            mISQLiteOpenHelper.getDBWrapper().endTransaction();
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void executeForTransaction(Transaction transaction) {
        try{
            mISQLiteOpenHelper.getDBWrapper().executeForTransaction(transaction);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean getTableCreatedWithClass(Class<? extends ILKDBModel> clazz) {
        try{
            mISQLiteOpenHelper.getDBWrapper().getTableCreatedWithClass(clazz);
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean getTableCreatedWithTableName(String tableName) {
        try{
            mISQLiteOpenHelper.getDBWrapper().getTableCreatedWithTableName(tableName);
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public <T extends ILKDBModel> boolean isExistsModel(T model) {
        try{
            return mISQLiteOpenHelper.getDBWrapper().isExistsModel(model);
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean isExistsClass(Class<? extends ILKDBModel> clazz, Object where) {
        try{
            return mISQLiteOpenHelper.getDBWrapper().isExistsClass(clazz, where);
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean isExistsWithTableName(String tableName, Object where) {
        try{
            return mISQLiteOpenHelper.getDBWrapper().isExistsWithTableName(tableName, where);
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean dropAllTable() {
        try{
            mISQLiteOpenHelper.getDBWrapper().dropAllTable();
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean dropTableWithClass(Class<? extends ILKDBModel> clazz) {
        try{
            mISQLiteOpenHelper.getDBWrapper().dropTableWithClass(clazz);
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean dropTableWithTableName(String tableName) {
        try{
            mISQLiteOpenHelper.getDBWrapper().dropTableWithTableName(tableName);
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public <T extends ILKDBModel> long rowCount(T model) {
        try{
            return mISQLiteOpenHelper.getDBWrapper().rowCount(model);

        }catch (Exception e){
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public long rowCount(Class<? extends ILKDBModel> clazz, String where) {
        try{
            return mISQLiteOpenHelper.getDBWrapper().rowCount(clazz, where);

        }catch (Exception e){
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public long rowCount(Class<? extends ILKDBModel> clazz, Map<String, Object> where) {
        try{
            return mISQLiteOpenHelper.getDBWrapper().rowCount(clazz, where);

        }catch (Exception e){
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public long rowCountWithTableName(String tableName, String where) {
        try{
            return mISQLiteOpenHelper.getDBWrapper().rowCountWithTableName(tableName, where);

        }catch (Exception e){
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public long rowCountWithTableName(String tableName, Map<String, Object> where) {
        try{
            return mISQLiteOpenHelper.getDBWrapper().rowCountWithTableName(tableName, where);

        }catch (Exception e){
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public <T extends ILKDBModel> List<T> search(Class<T> modelClass, String sql, List<Object> objects) {
        try{
            return mISQLiteOpenHelper.getDBWrapper().search(modelClass, sql, objects);

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public <T extends ILKDBModel> List<T> searchWithSQL(String sql, Class<T> toClass) {
        try{
            return mISQLiteOpenHelper.getDBWrapper().searchWithSQL(sql, toClass);

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public <T extends ILKDBModel> List<T> search(Class<T> modelClass, Object column, Object where, String orderBy, int offset, int count) {
        try{
            return mISQLiteOpenHelper.getDBWrapper().search(modelClass, column, where, orderBy, offset, count);

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public <T extends ILKDBModel> List<T> searchWithRAWSQL(String sql, Class<T> toClass) {
        try{
            return mISQLiteOpenHelper.getDBWrapper().searchWithRAWSQL(sql, toClass);

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public <T extends ILKDBModel> List<T> searchWithParams(LKDBQueryParams<T> params) {
        try{
            return mISQLiteOpenHelper.getDBWrapper().searchWithParams(params);

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public <T extends ILKDBModel> List<T> search(Class<T> modelClass, Object where, String orderBy, int offset, int count) {
        try{
            return mISQLiteOpenHelper.getDBWrapper().search(modelClass, where, orderBy, offset, count);

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public <T extends ILKDBModel> T searchSingle(Class<T> modelClass, Object where, String orderBy) {
        try{
            return mISQLiteOpenHelper.getDBWrapper().searchSingle(modelClass, where, orderBy);

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public <T extends ILKDBModel> boolean insertToDB(T model) {
        try{
            return mISQLiteOpenHelper.getDBWrapper().insertToDB(model);

        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public <T extends ILKDBModel> boolean insertWhenNotExists(T model) {
        try{
            return mISQLiteOpenHelper.getDBWrapper().insertWhenNotExists(model);

        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public <T extends ILKDBModel> boolean deleteToDB(T model) {
        try{
            return mISQLiteOpenHelper.getDBWrapper().deleteToDB(model);

        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteWithClass(Class<? extends ILKDBModel> clazz, Object where) {
        try{
            return mISQLiteOpenHelper.getDBWrapper().deleteWithClass(clazz, where);

        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteWithTableName(String tableName, Object where) {
        try{
            return mISQLiteOpenHelper.getDBWrapper().deleteWithTableName(tableName, where);

        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
}
