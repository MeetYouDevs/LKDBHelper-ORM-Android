package com.lhh.lkdb.sql;

import com.lhh.lkdb.ILKDBModel;

import java.util.List;
import java.util.Map;

/**
 * Created by Linhh on 2017/11/7.
 */

public class LKDBQueryParams<T> {
    private String columns;

    public String getColumns() {
        return columns;
    }

    public void setColumns(String columns) {
        this.columns = columns;
    }

    public List<String> getColumnArray() {
        return columnArray;
    }

    public void setColumnArray(List<String> columnArray) {
        this.columnArray = columnArray;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getWhere() {
        return where;
    }

    public void setWhere(String where) {
        this.where = where;
    }

    public Map<String, Object> getWhereDic() {
        return whereDic;
    }

    public void setWhereDic(Map<String, Object> whereDic) {
        this.whereDic = whereDic;
    }

    public String getGroupBy() {
        return groupBy;
    }

    public void setGroupBy(String groupBy) {
        this.groupBy = groupBy;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Class<? extends ILKDBModel> getToClass() {
        return toClass;
    }

    public void setToClass(Class<? extends ILKDBModel> toClass) {
        this.toClass = toClass;
    }

    public ResultsCallback<T> getCallback() {
        return callback;
    }

    public void setCallback(ResultsCallback<T> callback) {
        this.callback = callback;
    }

    private List<String> columnArray;
    private String tableName;
    private String where;
    private Map<String,Object> whereDic;
    private String groupBy;
    private String orderBy;
    private int offset;
    private int count;
    private Class<? extends ILKDBModel> toClass;
    private ResultsCallback<T> callback;
}
