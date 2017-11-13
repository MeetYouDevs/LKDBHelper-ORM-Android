package com.lhh.lkdb;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.lhh.lkdb.annotations.Check;
import com.lhh.lkdb.annotations.Default;
import com.lhh.lkdb.annotations.ForeignKey;
import com.lhh.lkdb.annotations.No4LKDB;
import com.lhh.lkdb.annotations.NotNull;
import com.lhh.lkdb.annotations.PrimaryKey;
import com.lhh.lkdb.annotations.RowidAlias;
import com.lhh.lkdb.annotations.Unique;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Linhh on 2017/11/7.
 */

public class LKDBTableUtils {
    private static final Map<Class<? extends ILKDBModel>, String> mTables = new ConcurrentHashMap<>();
    private static final Map<String, LKModelInfos> mModelInfos = new ConcurrentHashMap<>();

    public static final String LKSQL_Type_Text = "TEXT";
    public static final String LKSQL_Type_Integer = "INTEGER";
    public static <T extends ILKDBModel> String getTable(T model){
        String table = model.getTableName();
        if(TextUtils.isEmpty(table)){
            return getTableByClass(model.getClass());
        }else{
            return table;
        }
    }
    public static String getTableByClass(Class<? extends ILKDBModel> clazz){
        String tableName = null;
        synchronized (LKDBTableUtils.class) {
            tableName = mTables.get(clazz);
            if (!TextUtils.isEmpty(tableName)) {
                return tableName;
            }
            try {
                Object o = clazz.newInstance();
                Method method = clazz.getMethod("getTableName", new Class[0]);
                tableName = (String) method.invoke(o);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (TextUtils.isEmpty(tableName)) {
                tableName = LKDBUtils.getNameByClazz(clazz);
            }
            mTables.put(clazz, tableName.trim());
        }
        return tableName;
    }

    public static <T extends ILKDBModel> long getRowid(T mode){
        LKModelInfos infos =  getModelInfos(mode.getClass());
        String rowidName = infos.getDb_rowidAliasName();
        if(TextUtils.isEmpty(rowidName)){
            return 0;
        }
        Field field = infos.getFieldMaps().get(rowidName);
        long rowid = 0;
        try {
            rowid = field.getLong(mode);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return rowid;
    }


    public static String getType(Class<?> type){
        if(type == int.class || type == Integer.class) {
            return LKSQL_Type_Integer;
        }else{
            return LKSQL_Type_Text;
        }
    }

    public static Object formatV(Object o){
        if(isNum(o.getClass())){
            return o;
        }else{
            if(needJson(o.getClass())){
                return JSON.toJSONString(o);
            }else{
                return String.valueOf(o);
            }
        }
    }

    public static boolean isFloat(Class<?> type){
        if(type == float.class || type == Float.class){
            return true;
        }else{
            return false;
        }
    }

    public static boolean isLong(Class<?> type){
        if(type == Long.class || type == long.class){
            return true;
        }else{
            return false;
        }
    }

    public static boolean isShort(Class<?> type){
        if(type == short.class || type == Short.class){
            return true;
        }else{
            return false;
        }
    }


    public static String[] listToStrings(List<Object> objects){
        String[] strings = new String[objects.size()];
        for(int i = 0; i < objects.size(); i++){
            strings[i] = String.valueOf(objects.get(i));
        }
        return strings;
    }

    public static boolean isDouble(Class<?> type){
        if(type == double.class || type == Float.class){
            return true;
        }else{
            return false;
        }
    }

    public static boolean isNum(Class<?> type){
        if(type == int.class || type == Integer.class) {
            return true;
        }else{
            return false;
        }
    }

    public static boolean needJson(Class<?> type){
        if(type == int.class
                || type == String.class
                || type == long.class
                || type == double.class
                || type == float.class
                || type == Number.class
                || type == Float.class
                || type == Double.class
                || type == Integer.class
                || type == Long.class
                || type == boolean.class
                || type == Boolean.class
                || type == short.class
                || type == Short.class){
            return false;
        }
        return true;
    }

    public static LKModelInfos getModelInfos(Class<? extends ILKDBModel> clazz){
        if (mModelInfos.containsKey(clazz.getName())) {
            return mModelInfos.get(clazz.getName());
        }
        LKModelInfos lkModelInfos = new LKModelInfos();
        synchronized (LKDBTableUtils.class) {
            Field[] fields = clazz.getDeclaredFields();
            Map<String, Field> map = new HashMap<>();
            Map<String, String> default_map = new HashMap<>();
            List<String> primaryKeys = new ArrayList<>();
            List<String> checks = new ArrayList<>();
            List<String> foreignKeys = new ArrayList<>();
            List<String> notNulls = new ArrayList<>();
            List<String> uniques = new ArrayList<>();
            for (Field field : fields){
                Annotation[] annotations = field.getDeclaredAnnotations();
                boolean no4LKDB = false;
                boolean primaryKey = false;
                boolean check = false;
                boolean foreignKey = false;
                boolean notNull = false;
                boolean unique = false;
                boolean defaultF = false;
                String defaultV = null;
                for(Annotation annotation :annotations){
                    if(annotation.annotationType() == No4LKDB.class){
                        //不加入DB数据库，通知外部循环这个字段不加入db
                        no4LKDB = true;
                        break;
                    }
                    if(annotation.annotationType() == PrimaryKey.class){
                        //发现主字段
                        primaryKey = true;
                    }
                    if(annotation.annotationType() == RowidAlias.class){
                        lkModelInfos.setDb_rowidAliasName(field.getName());
                    }
                    if(annotation.annotationType() == Check.class){
                        check = true;
                    }
                    if(annotation.annotationType() == ForeignKey.class){
                        foreignKey = true;
                    }
                    if(annotation.annotationType() == NotNull.class){
                        notNull = true;
                    }
                    if(annotation.annotationType() == Unique.class){
                        unique = true;
                    }
                    if(annotation.annotationType() == Default.class){
                        defaultF = true;
                        defaultV = ((Default)annotation).value();
                    }
                }
                if(no4LKDB){
                    continue;
                }
                if(primaryKey) {
                    //如果主字段不为空就加入链表
                    primaryKeys.add(field.getName());
                }
                if(check){
                    checks.add(field.getName());
                }
                if(foreignKey){
                    foreignKeys.add(field.getName());
                }
                if(notNull){
                    notNulls.add(field.getName());
                }
                if(unique){
                    uniques.add(field.getName());
                }
                map.put(field.getName(), field);
                if(defaultF){
                    default_map.put(field.getName(), defaultV);
                }
            }
            lkModelInfos.setFieldMaps(map);
            lkModelInfos.setPrimaryKeys(primaryKeys);
            lkModelInfos.setChecks(checks);
            lkModelInfos.setForeignKeys(foreignKeys);
            lkModelInfos.setNotNulls(notNulls);
            lkModelInfos.setUniques(uniques);
            lkModelInfos.setDefaultMap(default_map);
            mModelInfos.put(clazz.getName(), lkModelInfos);
        }
        return lkModelInfos;
    }

    public static String replaceTableNameIfNeeded(String sql, Class<? extends ILKDBModel> clazz){
        if(clazz == null){
            return sql;
        }
        String tableName = getTableByClass(clazz);
        if(sql.endsWith("@t")){
            sql = sql + " ";
        }
        String[] froms = sql.split(" from ");
        if(froms.length == 2 && !sql.contains(" join ")){
            sql = sql.replaceAll(" from ", "," + tableName + ".rowid from ");
        }

        sql = sql.replaceAll(" @t ", tableName);
        sql = sql.replaceAll(" @t,", tableName);
        sql = sql.replaceAll(",@t ", tableName);
        return sql;
    }
}
