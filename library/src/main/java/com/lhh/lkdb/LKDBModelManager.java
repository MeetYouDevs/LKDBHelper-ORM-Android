package com.lhh.lkdb;

import android.database.Cursor;
import android.text.TextUtils;

import com.lhh.lkdb.sql.LKDBQueryParams;
import com.lhh.lkdb.sql.Transaction;

import java.util.List;
import java.util.Map;

/**
 * manager all lkdbmodel
 * Created by Linhh on 2017/11/13.
 */

public class LKDBModelManager {
    public static <T extends ILKDBModel> boolean insertToDB(T model){
        String db = model.getDBName();
        if(TextUtils.isEmpty(db)){
            return LKDBHelper.getHelper().insertToDB(model);
        }else{
            return LKDBHelper.getHelper(db).insertToDB(model);
        }
    }

    public static <T extends ILKDBModel> boolean updateToDB(T model, Object where){
        String db = model.getDBName();
        if(TextUtils.isEmpty(db)){
            return LKDBHelper.getHelper().updateToDB(model, where);
        }else{
            return LKDBHelper.getHelper(db).updateToDB(model, where);
        }
    }

    public static <T extends ILKDBModel> boolean  updateToDBWithTableName(T model, String sets, Object where){
        String db = model.getDBName();
        String tableName = LKDBTableUtils.getTable(model);
        if(TextUtils.isEmpty(db)){
            return LKDBHelper.getHelper().updateToDBWithTableName(tableName, sets, where);
        }else{
            return LKDBHelper.getHelper(db).updateToDBWithTableName(tableName, sets, where);
        }
    }

    /**
     * 只匹配主键
     * @param model
     * @param <T>
     * @return
     */
    public  static <T extends ILKDBModel> boolean isExistsModel(T model){
        String db = model.getDBName();
        if(TextUtils.isEmpty(db)){
            return LKDBHelper.getHelper().isExistsModel(model);
        }else{
            return LKDBHelper.getHelper(db).isExistsModel(model);
        }
    }

    public static <T extends ILKDBModel> long rowCount(T model){
        String db = model.getDBName();
        if(TextUtils.isEmpty(db)){
            return LKDBHelper.getHelper().rowCount(model);
        }else{
            return LKDBHelper.getHelper(db).rowCount(model);
        }
    }

    public  static <T extends ILKDBModel> long rowCountWithWhere(T model, Object where){
        String db = model.getDBName();
        String tableName = LKDBTableUtils.getTable(model);
        if(TextUtils.isEmpty(db)){
            return LKDBHelper.getHelper().rowCountWithTableName(tableName,  where);
        }else{
            return LKDBHelper.getHelper(db).rowCountWithTableName(tableName,  where);
        }
    }

    public static <T extends ILKDBModel> boolean insertWhenNotExists(T model){
        String db = model.getDBName();
        if(TextUtils.isEmpty(db)){
            return LKDBHelper.getHelper().insertWhenNotExists(model);
        }else{
            return LKDBHelper.getHelper(db).insertWhenNotExists(model);
        }
    }

    public static <T extends ILKDBModel> boolean deleteToDB(T model){
        String db = model.getDBName();
        if(TextUtils.isEmpty(db)){
            return LKDBHelper.getHelper().deleteToDB(model);
        }else{
            return LKDBHelper.getHelper(db).deleteToDB(model);
        }
    }

}
