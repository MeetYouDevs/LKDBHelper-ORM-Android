package com.lhh.lkdb.sql;

import java.util.List;

/**
 * Created by Linhh on 2017/11/7.
 */

public interface ResultsCallback<T> {
    public void onResults(List<T> result);
}
