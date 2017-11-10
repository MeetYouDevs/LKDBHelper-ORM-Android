package com.lhh.lkdb;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * Created by Linhh on 2017/11/8.
 */

public class LKModelInfos {
    public List<String> getPrimaryKeys() {
        return primaryKeys;
    }

    public void setPrimaryKeys(List<String> primaryKeys) {
        this.primaryKeys = primaryKeys;
    }

    public Map<String, Field> getFieldMaps() {
        return fieldMaps;
    }

    public void setFieldMaps(Map<String, Field> fieldMaps) {
        this.fieldMaps = fieldMaps;
    }

    private List<String> primaryKeys;
    private Map<String, Field> fieldMaps;
    private List<String> notNulls;

    public List<String> getNotNulls() {
        return notNulls;
    }

    public void setNotNulls(List<String> notNulls) {
        this.notNulls = notNulls;
    }

    public List<String> getChecks() {
        return checks;
    }

    public void setChecks(List<String> checks) {
        this.checks = checks;
    }

    public List<String> getForeignKeys() {
        return foreignKeys;
    }

    public void setForeignKeys(List<String> foreignKeys) {
        this.foreignKeys = foreignKeys;
    }

    public List<String> getUniques() {
        return uniques;
    }

    public void setUniques(List<String> uniques) {
        this.uniques = uniques;
    }

    private List<String> checks;
    private List<String> foreignKeys;
    private List<String> uniques;

    public Map<String, String> getDefaultMap() {
        return defaultMap;
    }

    public void setDefaultMap(Map<String, String> defaultMap) {
        this.defaultMap = defaultMap;
    }

    private Map<String,String> defaultMap;

    public String getDb_rowidAliasName() {
        return db_rowidAliasName;
    }

    public void setDb_rowidAliasName(String db_rowidAliasName) {
        this.db_rowidAliasName = db_rowidAliasName;
    }

    private String db_rowidAliasName;
}
