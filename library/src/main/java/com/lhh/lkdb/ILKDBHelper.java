package com.lhh.lkdb;

import android.database.Cursor;

import com.lhh.lkdb.sql.LKDBQueryParams;
import com.lhh.lkdb.sql.Transaction;

import java.util.List;
import java.util.Map;

/**
 * Created by Linhh on 2017/11/7.
 */

public interface ILKDBHelper {
    /**
     * 关闭close
     */
    public boolean close();

    /**
     * 直接执行sql语句
     * @param sql
     */
    public boolean executeSQL(String sql);

    public boolean executeSQL(String sql, List<Object> objects);

    public Cursor executeQuery(String sql);
    public Cursor executeQuery(String sql, List<Object> objects);

    public void clearTableData(Class<? extends ILKDBModel> clazz);

    public <T extends ILKDBModel> boolean updateToDB(T model, Object where);

    public boolean updateToDBWithTableName(String tableName, String sets, Object where);

    public boolean updateToDB(Class<? extends ILKDBModel> clazz, String set, Object where);
    /**
     * 查询当前database内的table
     */
    public List<String> onQueryDBTable();

    public boolean beginTransaction();
    public boolean endTransaction();

    public void executeForTransaction(Transaction transaction);

    public boolean getTableCreatedWithClass(Class<? extends ILKDBModel> clazz);

    public boolean getTableCreatedWithTableName(String tableName);

    /**
     * 只匹配主键
     * @param model
     * @param <T>
     * @return
     */
    public  <T extends ILKDBModel> boolean isExistsModel(T model);

    public boolean isExistsClass(Class<? extends ILKDBModel> clazz, Object where);

    public boolean isExistsWithTableName(String tableName, Object where);

    public boolean dropAllTable();

    public boolean dropTableWithClass(Class<? extends ILKDBModel> clazz);

    public boolean dropTableWithTableName(String tableName);

    public <T extends ILKDBModel> long rowCount(T model);

    public long rowCount(Class<? extends ILKDBModel> clazz, Object where);

    public long rowCountWithTableName(String tableName, Object where);

    public <T extends ILKDBModel> List<T> search(Class<T> modelClass, String sql, List<Object> objects);

    public <T extends ILKDBModel> List<T> searchWithSQL(String sql, Class<T> toClass);

    public <T extends ILKDBModel> List<T> search(Class<T> modelClass, Object column, Object where, String orderBy, int offset, int count);

    public <T extends ILKDBModel> List<T> searchWithRAWSQL(String sql, Class<T> toClass);

    public <T extends ILKDBModel> List<T> searchWithParams(LKDBQueryParams<T> params);

    public <T extends ILKDBModel> List<T> search(Class<T> modelClass, Object where, String orderBy, int offset, int count);

    public <T extends ILKDBModel> T searchSingle(Class<T> modelClass, Object where, String orderBy);

    public <T extends ILKDBModel> boolean insertToDB(T model);

    public <T extends ILKDBModel> boolean insertWhenNotExists(T model);

    public <T extends ILKDBModel> boolean deleteToDB(T model);

    public boolean deleteWithClass(Class<? extends ILKDBModel> clazz, Object where);

    public boolean deleteWithTableName(String tableName, Object where);
}
