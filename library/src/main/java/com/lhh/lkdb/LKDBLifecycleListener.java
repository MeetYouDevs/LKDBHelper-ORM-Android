package com.lhh.lkdb;

import java.util.List;

/**
 * Created by Linhh on 2017/11/9.
 */

public interface LKDBLifecycleListener {
    void dbDidAlterTable(String tableName, List<String> alterAddColumns);
    void dbDidDeleted(String tableName, boolean result);
}
