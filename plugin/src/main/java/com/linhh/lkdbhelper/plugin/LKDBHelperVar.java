package com.linhh.lkdbhelper.plugin;

import com.linhh.lkdbhelper.plugin.apply.DeleteToDBApply;
import com.linhh.lkdbhelper.plugin.apply.ILKDBPlguinApply;
import com.linhh.lkdbhelper.plugin.apply.InsertToDBApply;
import com.linhh.lkdbhelper.plugin.apply.InsertWhenNotExistsApply;
import com.linhh.lkdbhelper.plugin.apply.IsExistsModelApply;
import com.linhh.lkdbhelper.plugin.apply.RowCountWithWhereApply;
import com.linhh.lkdbhelper.plugin.apply.UpdateToDBApply;
import com.linhh.lkdbhelper.plugin.apply.UpdateToDBWithTableNameApply;

import java.util.HashMap;

/**
 * Created by Linhh on 2017/11/13.
 */

public class LKDBHelperVar {

    public final static HashMap<String, ILKDBPlguinApply> mLKDBs = new HashMap<>();

    static {
        mLKDBs.put("insertToDB", new InsertToDBApply());
        mLKDBs.put("updateToDB", new UpdateToDBApply());
        mLKDBs.put("updateToDBWithTableName", new UpdateToDBWithTableNameApply());
        mLKDBs.put("isExistsModel", new IsExistsModelApply());
        mLKDBs.put("rowCountWithWhere", new RowCountWithWhereApply());
        mLKDBs.put("insertWhenNotExists", new InsertWhenNotExistsApply());
        mLKDBs.put("deleteToDB", new DeleteToDBApply());
    };
}
