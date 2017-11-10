package com.lhh.lkdb.sql;

import com.lhh.lkdb.ILKDBHelper;

/**
 * Created by Linhh on 2017/11/7.
 */

public interface Transaction {
    public boolean onTransaction(ILKDBHelper ilkdbHelper);
}
