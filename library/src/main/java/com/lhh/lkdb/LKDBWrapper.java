package com.lhh.lkdb;

import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.lhh.lkdb.sql.LKDBQueryParams;
import com.lhh.lkdb.sql.Transaction;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Linhh on 2017/11/7.
 */

public abstract class LKDBWrapper implements ILKDBHelper{
    private static final String TAG = "LKDBWrapper";

    protected final Map<String, Boolean> mCreatedTableNames  = new ConcurrentHashMap<>();
    public final static String LKSQL_Attribute_NotNull = "NOT NULL";
    public final static String LKSQL_Attribute_PrimaryKey = "PRIMARY KEY";
    public final static String LKSQL_Attribute_Default = "DEFAULT";
    public final static String LKSQL_Attribute_Unique = "UNIQUE";
    public final static String LKSQL_Attribute_Check = "CHECK";
    public final static String LKSQL_Attribute_ForeignKey = "FOREIGN KEY";

    protected boolean deleteTable(String table) {
        Log.d(TAG, "[onDeleteTable] table="+table);
        boolean r = true;
        if (null != table && 0 != table.length()) {
            String statement = "DROP TABLE IF EXISTS " + table;
            r = executeSQL(statement);
        }
        mCreatedTableNames.remove(table);
        return r;
    }

    @Override
    public <T extends ILKDBModel> T searchSingle(Class<T> modelClass, Object where, String orderBy) {
        List<T> array = searchBase(modelClass, null, where, orderBy,0 ,1);

        if (array != null && array.size() > 0) {
            return array.get(0);
        }

        return null;
    }

    protected abstract long executeInsert(String tableName, Map<String,Object> inserts);

    @Override
    public void executeForTransaction(Transaction transaction) {
        if(transaction == null){
            return;
        }
        beginTransaction();
        boolean result = transaction.onTransaction(this);
        if(result){
            endTransaction();
        }
    }

    @Override
    public <T extends ILKDBModel> boolean updateToDB(T model, Object where) {
        return updateToDBBase(model, where);
    }

    @Override
    public boolean updateToDB(Class<? extends ILKDBModel> clazz, String set, Object where) {
        return updateToDBWithTableName(LKDBTableUtils.getTableByClass(clazz), set, where);
    }

    @Override
    public void clearTableData(Class<? extends ILKDBModel> clazz) {
        String delete = "DELETE FROM " + LKDBTableUtils.getTableByClass(clazz);
        executeSQL(delete, null);
    }

    @Override
    public boolean updateToDBWithTableName(String tableName, String sets, Object where){
        try {
            LKDBCheck_tableNameIsInvalid(tableName);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        StringBuilder updateSQL = new StringBuilder();
        updateSQL.append("update ").append(tableName).append(" set ").append(sets).append(" ");
        List<Object> updateValues = extractQuery(updateSQL, where);

        boolean execute = executeSQL(updateSQL.toString(),updateValues);

        return execute;
    }

    protected <T extends ILKDBModel> boolean updateToDBBase(T model, Object where) {
        try {
            LKDBCheck_modelIsInvalid(model);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        Class modelClass = model.getClass();

        // callback
        if (model.dbWillUpdate()) {
            Log.e(TAG, "you cancel " + modelClass + " update.");
            return false;
        }

        String db_tableName = TextUtils.isEmpty(model.getTableName()) ? LKDBTableUtils.getTableByClass(modelClass) : model.getTableName();

        // 检测是否创建过表
        if (!mCreatedTableNames.containsKey(db_tableName)) {
            _createTableWithModelClass(modelClass, db_tableName);
        }

        LKModelInfos infos = LKDBTableUtils.getModelInfos(modelClass);

        StringBuilder updateKey = new StringBuilder();
        List<Object> updateValues = new ArrayList<>();

        Map<String,Field> propertys = infos.getFieldMaps();
        for (String sqlColumnName : propertys.keySet()) {
            if (TextUtils.isEmpty(sqlColumnName)) {
                continue;
            }
            Field property = propertys.get(sqlColumnName);
            Object value = null;
            try {
                value = property.get(model);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            if(value == null){
                continue;
            }
            ///跳过 rowid = 0 的属性
            if (sqlColumnName.equals(infos.getDb_rowidAliasName()) || sqlColumnName.toLowerCase().trim().equals("rowid")) {
                long rowid = (long)value;
                if (rowid > 0) {
                    ///如果rowid 已经存在就不修改
                    String rowidWhere = "rowid=" + rowid;
                    long rowCount = rowCountWithTableName(db_tableName,rowidWhere);
                    if (rowCount > 0) {
                        continue;
                    }
                } else {
                    continue;
                }
            }
            if (updateKey.length() > 0) {
                updateKey.append(",");
            }
            updateKey.append(sqlColumnName).append("=?");
            updateValues.add(LKDBTableUtils.formatV(value));
        }

        StringBuilder updateSQL = new StringBuilder();
        updateSQL.append("update ").append(db_tableName).append(" set ").append(updateKey).append(" where ");
        // 添加where 语句
        if (where instanceof String && !TextUtils.isEmpty((String)where)) {
            updateSQL.append(where);
        } else if (where instanceof Map && ((Map<String,Object>)where).size() > 0) {
            List<Object> valuearray = new ArrayList<>();
            String sqlwhere = dictionaryToSqlWhere((Map<String,Object>)where ,valuearray);

            updateSQL.append(sqlwhere);
            updateValues.addAll(valuearray);
        } else {
            // 如果不通过 rowid 来 更新数据  那 primarykey 一定要有值
            StringBuilder pwhere = primaryKeyWhereSQLWithModel(model,updateValues);

            if (pwhere.length() == 0) {
                Log.e(TAG,"database update fail : " + modelClass + " no find primary key!");
                return false;
            }

            updateSQL.append(pwhere);
        }

        boolean execute = executeSQL(updateSQL.toString(), updateValues);
        // callback

        return execute;
    }

    protected void sqlString(StringBuilder sql, String groupBy, String orderBy, int offset, int count){
        if (!TextUtils.isEmpty(groupBy)) {
            sql.append(" group by ").append(groupBy);
        }

        if (!TextUtils.isEmpty(orderBy)) {
            sql.append(" order by ").append(orderBy);
        }

        if (count > 0) {
            sql.append(" limit ").append(count).append(" offset ").append(offset);
        } else if (offset > 0) {
            sql.append(" limit ").append(Integer.MAX_VALUE).append(" offset ").append(offset);
        }
    }

    protected boolean LKDBCheck_tableNameIsInvalid(String tableName) throws Exception{
        if(TextUtils.isEmpty(tableName)){
            throw new Exception("tableName is null");
        }
        return true;
    }

    @Override
    public <T extends ILKDBModel> boolean insertToDB(T model) {
        return insertBase(model);
    }

    @Override
    public <T extends ILKDBModel> boolean insertWhenNotExists(T model) {
        if (!isExistsModel(model)) {
            return insertToDB(model);
        }
        return false;
    }

    @Override
    public boolean isExistsWithTableName(String tableName, Object where) {
        if(where instanceof StringBuilder){
            return rowCountWithTableName(tableName, ((StringBuilder)where).toString()) > 0;
        }
        return rowCountWithTableName(tableName,(Map<String,Object>)where) > 0;
    }

    @Override
    public boolean isExistsClass(Class<? extends ILKDBModel> clazz, Object where) {
        return isExistsWithTableName(LKDBTableUtils.getTableByClass(clazz), where);
    }

    protected <T extends ILKDBModel> StringBuilder primaryKeyWhereSQLWithModel(T model, List<Object> addPValues){
        LKModelInfos infos = LKDBTableUtils.getModelInfos(model.getClass());
        List<String> primaryKeys = infos.getPrimaryKeys();
        StringBuilder pwhere = new StringBuilder();

        if (primaryKeys.size() > 0) {
            Map<String,Field> propertys = infos.getFieldMaps();
            for (int i = 0; i < primaryKeys.size(); i++) {
                String pk = primaryKeys.get(i);

                if (!TextUtils.isEmpty(pk)) {

                    Field property = propertys.get(pk);
                    Object pvalue = null;
                    try {
                        pvalue = property.get(model);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        continue;
                    }

                    if (pvalue != null) {
                        if (pwhere.length() > 0) {
                            pwhere.append("and");
                        }

                        if (addPValues != null) {
                            pwhere.append(" ").append(pk).append("=?");
                            addPValues.add(LKDBTableUtils.formatV(pvalue));
                        } else {
                            pwhere.append(" ").append(pk).append("='").append(LKDBTableUtils.formatV(pvalue)).append("' ");
                        }
                    }
                }
            }
        }

        return pwhere;
    }

    @Override
    public <T extends ILKDBModel> boolean isExistsModel(T model) {
        try {
            LKDBCheck_modelIsInvalid(model);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        StringBuilder pwhere = primaryKeyWhereSQLWithModel(model, null);


        if (pwhere.length() == 0) {
            Log.e(TAG,"exists model fail: primary key is null or invalid");
            return false;
        }

        return isExistsClass(model.getClass(), pwhere);
    }

    @Override
    public boolean dropTableWithClass(Class<? extends ILKDBModel> clazz) {
        deleteTable(LKDBTableUtils.getTableByClass(clazz));
        return true;
    }

    protected boolean LKDBCheck_modelIsInvalid(ILKDBModel model) throws Exception{
        if (model == null) {
            throw new Exception("model is null");
        }
        if (LKDBTableUtils.getModelInfos(model.getClass()).getFieldMaps().size() == 0) {
            throw new Exception("class: " + model.getClass() + "  property count is 0!!");
        }
        if (TextUtils.isEmpty(model.getTableName()) && TextUtils.isEmpty(LKDBTableUtils.getTableByClass(model.getClass()))) {
            throw new Exception("model class name " + model.getClass() + " table name is invalid!");
        }
        return true;
    }

    @Override
    public boolean dropTableWithTableName(String tableName) {
        deleteTable(tableName);
        return true;
    }

    protected <T extends ILKDBModel> boolean insertBase(T model){
        try{
            LKDBCheck_modelIsInvalid(model);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

        Class modelClass = model.getClass();

        // callback
        if (model.dbWillInsert()) {
            Log.e(TAG, "your cancel insert");
            return false;
        }

        model.setDb_inserting(true);

        String db_tableName = !TextUtils.isEmpty(model.getTableName()) ? model.getTableName(): LKDBTableUtils.getTableByClass(modelClass);

        // 检测是否创建过表
        if(!mCreatedTableNames.containsKey(db_tableName)){
            _createTableWithModelClass(modelClass, db_tableName);
        }

        // --
        LKModelInfos infos = LKDBTableUtils.getModelInfos(modelClass);
        List<String> primaryProperty = infos.getPrimaryKeys();
//        StringBuilder insertKey = new StringBuilder();
//        StringBuilder insertValuesString = new StringBuilder();
        Map<String, Object> insertValues = new HashMap<>();

        Field rowid = null;
        for(String sqlColumnName : infos.getFieldMaps().keySet()){
            if (TextUtils.isEmpty(sqlColumnName)) {
                continue;
            }

            Field property = infos.getFieldMaps().get(sqlColumnName);

            //如果是主键，且是int=0就跳过，让其自增
            if (primaryProperty.contains(sqlColumnName)) {
                int a = 0;
                try {
                    a = property.getInt(model);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    Log.e(TAG,"主键不能为整型外的类型");
                    return false;
                }
                //如果为0说明要自增
                if (a == 0
                        /*&& LKDBTableUtils.getType(property.getType()).equals(LKDBTableUtils.LKSQL_Type_Integer)*/) {
                    continue;
                }
            }

            ///如果是rowid，跳过 rowid = 0 的属性
            if (sqlColumnName.equals(infos.getDb_rowidAliasName()) || sqlColumnName.toLowerCase().trim().equals("rowid")) {
                try {
                    rowid = property;
                    if (property.getInt(model) == 0){
                        continue;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    Log.e(TAG,"rowid不能为整型外的类型");
                    return false;
                }
            }

            try {
                Object object = property.get(model);
                if(object.getClass() != int.class && object.getClass() != Integer.class){
                    //如果不是数字
                    String v = null;
                    if(LKDBTableUtils.needJson(object.getClass())){
                        //需要json
                        v = JSON.toJSONString(object);
                    }else{
                        v = String.valueOf(object);
                    }
                    insertValues.put(sqlColumnName, v);
                }else{
                    insertValues.put(sqlColumnName,object);
                }
            }catch (Exception e){
                e.printStackTrace();
                continue;
            }

//            if (insertKey.length() > 0) {
//                insertKey.append(",");
//                insertValuesString.append(",");
//            }

//            insertKey.append(sqlColumnName);
//            insertValuesString.append("?");

        }

        // 拼接insertSQL 语句  采用 replace 插入
//        String insertSQL = "replace into " + db_tableName + "(" + insertKey.toString() + ") values(" + insertValuesString.toString() + ")";

        long lastInsertRowId = 0;

        lastInsertRowId = executeInsert(db_tableName, insertValues);
        if(lastInsertRowId == -1){
            return false;
        }
        if(rowid != null) {
            rowid.setAccessible(true);
            try {
                rowid.set(model, lastInsertRowId);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        model.setDb_inserting(false);
        return true;
    }

    protected void fixSqlColumnsWithClass(Class<? extends ILKDBModel> clazz, String tableName){
        LKModelInfos modelInfos = LKDBTableUtils.getModelInfos(clazz);

        String select = "select * from " + tableName + " limit 0";
        Cursor cursor = executeQuery(select);
        List<String> columnArray = Arrays.asList(cursor.getColumnNames());
        cursor.close();
        List<String> alterAddColumns = new ArrayList<>();
        List<String> notNulls = modelInfos.getNotNulls();
        List<String> checks = modelInfos.getChecks();
        List<String> foreignKeys = modelInfos.getForeignKeys();
        List<String> uniques = modelInfos.getUniques();
        Map<String,String> defaultMap = modelInfos.getDefaultMap();


        Map<String, Field> infos = modelInfos.getFieldMaps();
        for (String sqlColumnName : infos.keySet()) {
            Field property = infos.get(sqlColumnName);
            if (sqlColumnName.toLowerCase().trim().equals("rowid")) {
                continue;
            }

            ///数据库中不存在 需要alter add
            if(!columnArray.contains(sqlColumnName)){
                StringBuilder addColumePars = new StringBuilder();
                addColumePars.append(sqlColumnName).append(" ").append(LKDBTableUtils.getType(property.getType()));

                if(notNulls.contains(sqlColumnName)){
                    addColumePars.append(" ").append(LKSQL_Attribute_NotNull);
                }

                String alertSQL = "alter table " + tableName + " add column " + addColumePars;

                String defaultValue = defaultMap.containsKey(sqlColumnName)? defaultMap.get(sqlColumnName) : null;
                if(LKDBTableUtils.LKSQL_Type_Text.equals(LKDBTableUtils.getType(property.getType()))){
                    if(defaultValue == null) {
                        defaultValue = "''";
                    }else{
                        defaultValue = "'" + defaultValue + "'";
                    }
                }else{
                    if(defaultValue == null) {
                        defaultValue = "0";
                    }
                }

                String initColumnValue = "update " + tableName + " set " + sqlColumnName + "=" + defaultValue;

                boolean success = executeSQL(alertSQL, null);
                if (success) {
                    executeSQL(initColumnValue, null);
                    alterAddColumns.add(sqlColumnName);
                }
            }
        }
        if(alterAddColumns.size() > 0){
            LKDBLifecycle.dbDidAlterTable(tableName, alterAddColumns);
        }
    }

    @Override
    public <T extends ILKDBModel> boolean deleteToDB(T model) {
        return deleteToDBBase(model);
    }

    @Override
    public boolean deleteWithTableName(String tableName, Object where) {
        try {
            LKDBCheck_tableNameIsInvalid(tableName);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        StringBuilder deleteSQL = new StringBuilder();
        deleteSQL.append("delete from ").append(tableName);
        List<Object> values = extractQuery(deleteSQL, where);
        boolean result = executeSQL(deleteSQL.toString(), values);
        return result;
    }

    @Override
    public boolean deleteWithClass(Class<? extends ILKDBModel> clazz, Object where) {
        return deleteWithTableName(LKDBTableUtils.getTableByClass(clazz), where);
    }

    @Override
    public <T extends ILKDBModel> long rowCount(T model) {
        String db_tableName = TextUtils.isEmpty(model.getTableName()) ? LKDBTableUtils.getTableByClass(model.getClass()): model.getTableName();
        return rowCountWithTableName(db_tableName, "");
    }

    protected <T extends ILKDBModel> boolean deleteToDBBase(T model){
        try {
            LKDBCheck_modelIsInvalid(model);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        Class modelClass = model.getClass();

        // callback
        if (model.dbWillDelete()) {
            Log.e(TAG, "you cancel " + model + " delete");
            return false;
        }

        String db_tableName = TextUtils.isEmpty(model.getTableName()) ? LKDBTableUtils.getTableByClass(modelClass): model.getTableName();

        StringBuilder deleteSQL = new StringBuilder();
        deleteSQL.append("delete from ").append( db_tableName).append(" where ");
        List<Object> parsArray = new ArrayList<>();

//        LKModelInfos infos = LKDBTableUtils.getModelInfos(modelClass);
        long rowid = LKDBTableUtils.getRowid(model);
        if (rowid > 0) {
            deleteSQL.append("rowid = ").append(rowid);
        } else {
            StringBuilder pwhere = primaryKeyWhereSQLWithModel(model ,parsArray);

            if (pwhere.length() == 0) {
                Log.e(TAG, "delete fail : " + modelClass + " primary value is null");
                return false;
            }
            deleteSQL.append(pwhere);

        }

        if (parsArray.size() == 0) {
            parsArray = null;
        }

        boolean execute = executeSQL(deleteSQL.toString(),parsArray);

        // callback
        LKDBLifecycle.dbDidDeleted(db_tableName, execute);

        return execute;
    }

    protected boolean _createTableWithModelClass(Class<? extends ILKDBModel> clazz, String tableName) {
        boolean isCreated = false;
        synchronized (this) {
            if (TextUtils.isEmpty(tableName)) {
                return false;
            }
            boolean created = getTableCreatedWithTableName(tableName);
            if (created) {
                if (!mCreatedTableNames.containsKey(tableName)) {
                    mCreatedTableNames.put(tableName, created);
                }
                fixSqlColumnsWithClass(clazz, tableName);
                return true;
            }

            LKModelInfos modelInfos = LKDBTableUtils.getModelInfos(clazz);
            if (modelInfos == null || modelInfos.getFieldMaps() == null || modelInfos.getFieldMaps().size() == 0) {
                return false;
            }

            List<String> primaryKeys = modelInfos.getPrimaryKeys();
            List<String> checks = modelInfos.getChecks();
            List<String> foreignKeys = modelInfos.getForeignKeys();
            List<String> notNulls = modelInfos.getNotNulls();
            List<String> uniques = modelInfos.getUniques();
            Map<String, String> defaultMap = modelInfos.getDefaultMap();
            String db_rowidAliasName = modelInfos.getDb_rowidAliasName();
            Map<String, Field> infos = modelInfos.getFieldMaps();

            StringBuilder table_pars = new StringBuilder();

            int index = 0;

            for (String key : infos.keySet()) {
                if (index > 0) {
                    table_pars.append(",");
                }

                table_pars.append(key).append(" ").append(LKDBTableUtils.getType(infos.get(key).getType()));
                if (notNulls.contains(key)) {
                    table_pars.append(" ").append(LKSQL_Attribute_NotNull);
                }
                if (uniques.contains(key)) {
                    table_pars.append(" ").append(LKSQL_Attribute_Unique);
                }
                if (defaultMap.containsKey(key)) {
                    table_pars.append(" ").append(LKSQL_Attribute_Default).append(" ").append(defaultMap.get(key));
                }
                if (db_rowidAliasName != null && key.equals(db_rowidAliasName)) {
                    table_pars.append(" primary key autoincrement");
                }
                index++;

            }

            StringBuilder pksb = new StringBuilder();
            if (TextUtils.isEmpty(db_rowidAliasName) && primaryKeys.size() > 0) {
                for (int i = 0; i < primaryKeys.size(); i++) {
                    String pk = primaryKeys.get(i);

                    if (pksb.length() > 0) {
                        pksb.append(",");
                    }

                    pksb.append(pk);
                }

                if (pksb.length() > 0) {
                    pksb.insert(0, ",primary key(");
                    pksb.append(")");
                }
            }

            String createTableSQL = "CREATE TABLE IF NOT EXISTS " + tableName + "(" + table_pars.toString() + pksb.toString() + ")";

            isCreated = executeSQL(createTableSQL);

            if (isCreated) {
                mCreatedTableNames.put(tableName, isCreated);
            }
        }

        return isCreated;
    }

    private long row(String tableName, Object where){
        try {
            LKDBCheck_tableNameIsInvalid(tableName);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }

        StringBuilder rowCountSql = new StringBuilder();
        rowCountSql.append("select count(rowid) from ").append(tableName);

        List<Object> valuesarray = extractQuery(rowCountSql,where);
        Cursor cursor = executeQuery(rowCountSql.toString(), valuesarray);
        cursor.moveToFirst();
        long rowid = cursor.getLong(0);
        cursor.close();
        return rowid;
    }


    @Override
    public long rowCount(Class<? extends ILKDBModel> clazz, Object where) {
        return rowCountWithTableName(LKDBTableUtils.getTableByClass(clazz), where);
    }

    @Override
    public long rowCountWithTableName(String tableName, Object where) {
        return row(tableName, where);
    }

    // splice 'where' 拼接where语句
    protected List<Object> extractQuery(StringBuilder query ,Object where){
        List<Object> values = null;

        if (where instanceof String && !TextUtils.isEmpty((String)where)) {
            query.append(" where ").append(where);
        } else if (where instanceof Map) {
            Map<String, Object> dicWhere = (Map<String, Object>)where;

            if (dicWhere.size() > 0) {
                values = new ArrayList<>(dicWhere.size());
                String wherekey = dictionaryToSqlWhere(dicWhere, values);
                query.append(" where ").append(wherekey);
            }
        }

        return values;
    }

    protected String dictionaryToSqlWhere(Map<String,Object> dic, List<Object> values){
        if (dic.size() == 0) {
            return "";
        }
        StringBuilder wherekey = new StringBuilder();
        for(String key :dic.keySet()){
            Object o = dic.get(key);
            if(o instanceof List){
                List<String> vlist = (List<String>) o;
                if(vlist.size() == 0){
                    continue;
                }
                if(wherekey.length() > 0){
                    wherekey.append(" and");
                }
                wherekey.append(" ").append(key).append(" in(");
                for(int i = 0 ; i < vlist.size() ;i ++){
                    String vlist_obj = vlist.get(i);
                    if(i > 0){
                        wherekey.append(",");
                    }
                    wherekey.append("?");
                    values.add(vlist_obj);
                }
                wherekey.append(")");
            }else {
                if(wherekey.length() > 0){
                    wherekey.append(" and ").append(key).append("=?");
                }else{
                    wherekey.append(" ").append(key).append("=?");
                }
                values.add(LKDBTableUtils.formatV(o));
            }
        }
        return wherekey.toString();
    }

    protected <T extends ILKDBModel> List<T> searchBaseWithParams(LKDBQueryParams<T> params){
        if(params.getToClass() == null){
            return null;
        }

        String db_tableName = params.getTableName();
        if(TextUtils.isEmpty(db_tableName)){
            db_tableName = LKDBTableUtils.getTableByClass(params.getToClass());
        }
        if(TextUtils.isEmpty(db_tableName)){
            return null;
        }

        if(!mCreatedTableNames.containsKey(db_tableName)){
            //不存在表
            _createTableWithModelClass(params.getToClass(), db_tableName);
        }

        String columnsString = null;
        int columnCount = 0;

        if (params.getColumnArray().size() > 0) {
            columnCount = params.getColumnArray().size();
            StringBuilder stringBuilder = new StringBuilder();
            for(String s: params.getColumnArray()){
                if(stringBuilder.length() > 0){
                    stringBuilder.append(",");
                }
                stringBuilder.append(s);
            }
            columnsString = stringBuilder.toString();
        } else if (!TextUtils.isEmpty(params.getColumns())) {
            columnsString = params.getColumns();
            String[] s = columnsString.split(",");
            columnCount = s.length;
        } else {
            columnsString = "*";
        }

        StringBuilder query = new StringBuilder();
        query.append("select ").append(columnsString).append(",rowid from @t");
        List<Object> whereValues = null;

        if (params.getWhereDic().size() > 0) {
            whereValues = new ArrayList<>(params.getWhereDic().size());
            String wherekey = dictionaryToSqlWhere(params.getWhereDic(),whereValues);
            query.append(" where ").append(wherekey);
        } else if (!TextUtils.isEmpty(params.getWhere())) {
            query.append(" where ").append(params.getWhere());
        }


        sqlString(query,params.getGroupBy(), params.getOrderBy(), params.getOffset(), params.getCount());

        // replace @t to model table name
        String replaceTableName = " " + db_tableName + " ";

        if (query.toString().endsWith(" @t")) {
            query.append(" ");
        }

        String q = query.toString().replaceAll(" @t ", replaceTableName);

        List<T> results = null;

        Cursor set = null;

        if (whereValues.size() == 0) {

            set = executeQuery(q);
        } else {
            set = executeQuery(q,whereValues);
        }

//        if (columnCount == 1) {
//            results = [self executeOneColumnResult:set];
//        } else {
//            results = [self executeResult:set Class:params.toClass tableName:db_tableName];
//        }
        results = executeResult(set, params.getToClass());
        set.close();
        return results;
    }

    protected <T extends ILKDBModel> List<T> executeResult(Cursor set, Class<? extends ILKDBModel> clazz){
        List<T> list = new ArrayList<>();

        LKModelInfos infos = LKDBTableUtils.getModelInfos(clazz);
        if (set != null && set.moveToFirst()) {
            Map<String, Field> fieldMaps = infos.getFieldMaps();
            String[] cs = set.getColumnNames();
            do{

                try {
                    T o = (T) clazz.newInstance();
                    for(String c : cs){
                        int i = set.getColumnIndex(c);
                        if(i == -1){
                            continue;
                        }
                        Field field = fieldMaps.get(c);
                        field.setAccessible(true);
                        if(LKDBTableUtils.isNum(field.getType())){
                            int r = set.getInt(i);
                            field.set(o, r);
                        }else if(LKDBTableUtils.isDouble(field.getType())){
                            double r = set.getDouble(i);
                            field.set(o, r);
                        }else if(LKDBTableUtils.isFloat(field.getType())){
                            float r = set.getFloat(i);
                            field.set(o, r);
                        }else if(LKDBTableUtils.isLong(field.getType())){
                            long r = set.getLong(i);
                            field.set(o, r);
                        }else if(LKDBTableUtils.isShort(field.getType())){
                            short r = set.getShort(i);
                            field.set(o, r);
                        }else{
                            String s = set.getString(i);
                            if(LKDBTableUtils.needJson(field.getType())){
                                //需要json
                                //json解析
                                Object result = JSON.parseObject(s, field.getType());
                                field.set(o, field.getType().cast(result));
                            }else {
                                field.set(o, field.getType().cast(s));
                            }
                        }
                    }
                    list.add(o);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }while (set.moveToNext());
        }
        return list;
    }

    protected <T extends ILKDBModel> List<T> searchBase(Class<T> modelClass, Object column, Object where, String orderBy, int offset, int count){
        LKDBQueryParams<T> lkdbQueryParams = new LKDBQueryParams<>();
        lkdbQueryParams.setToClass(modelClass);
        if(column == null){
            lkdbQueryParams.setColumnArray(new ArrayList<String>());
        }else {
            if (column instanceof List) {
                lkdbQueryParams.setColumnArray((List<String>) column);
            } else if (column instanceof String) {
                lkdbQueryParams.setColumns((String) column);
            }
        }

        if(where == null){
            lkdbQueryParams.setWhereDic(new HashMap<String, Object>());
        }else {
            if (where instanceof Map) {
                lkdbQueryParams.setWhereDic((Map<String, Object>) where);
            } else if (where instanceof String) {
                lkdbQueryParams.setWhere((String) where);
            }
        }

        lkdbQueryParams.setOrderBy(orderBy);
        lkdbQueryParams.setOffset(offset);
        lkdbQueryParams.setCount(count);

        return searchBaseWithParams(lkdbQueryParams);
    }
}
