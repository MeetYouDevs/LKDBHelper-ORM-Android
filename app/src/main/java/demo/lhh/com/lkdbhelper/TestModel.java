package demo.lhh.com.lkdbhelper;

import com.lhh.lkdb.ILKDBModel;
import com.lhh.lkdb.annotations.PrimaryKey;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Linhh on 2017/11/10.
 */

public class TestModel implements ILKDBModel{

    public int a = 88;
    public String b ="test";
    @PrimaryKey
    public int id;
    public float c = 1.2f;
    public String test = "我是测试数据";
    public ArrayList<String> listtest = new ArrayList<>();
    public HashMap<String,Object> maptest = new HashMap<>();
    @Override
    public String getTableName() {
        return null;
    }

    @Override
    public void setDb_inserting(boolean status) {

    }

    @Override
    public boolean dbWillInsert() {
        return false;
    }

    @Override
    public boolean dbWillUpdate() {
        return false;
    }

    @Override
    public boolean dbWillDelete() {
        return false;
    }

}
