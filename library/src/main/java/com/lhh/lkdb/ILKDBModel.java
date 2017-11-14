package com.lhh.lkdb;

import java.util.Map;

/**
 * Created by Linhh on 2017/11/6.
 */

public interface ILKDBModel {

    public String getTableName();

    public void setDb_inserting(boolean status);

    public boolean insertToDB();

    public boolean updateToDB(Object where);

    public boolean updateToDBWithTableName(String sets, Object where);

    public boolean isExistsModel();

    public long rowCountWithWhere(Object where);

    public boolean insertWhenNotExists();

    public boolean deleteToDB();

    public String getDBName();

    public boolean dbWillInsert();

    /**
     * android这边默认返回false，所以如果true说明要拦截
     * @return
     */
    public boolean dbWillUpdate();

    public boolean dbWillDelete();
}
