package com.lhh.lkdb;

/**
 * Created by Linhh on 2017/11/6.
 */

public interface ILKDBModel {

    public String getTableName();

    public void setDb_inserting(boolean status);

    public boolean dbWillInsert();

    /**
     * android这边默认返回false，所以如果true说明要拦截
     * @return
     */
    public boolean dbWillUpdate();

    public boolean dbWillDelete();
}
