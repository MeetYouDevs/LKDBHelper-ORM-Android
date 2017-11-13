package demo.lhh.com.lkdbhelper;

import com.lhh.lkdb.ILKDBModel;
import com.lhh.lkdb.annotations.PrimaryKey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    public boolean insertToDB() {
        return false;
    }

    @Override
    public boolean updateToDB(Object where) {
        return false;
    }

    @Override
    public boolean updateToDBWithTableName(String sets, Object where) {
        return false;
    }

    @Override
    public boolean isExistsModel() {
        return false;
    }

    @Override
    public long rowCountWithWhere(String where) {
        return 0;
    }

    @Override
    public long rowCountWithWhere(Map<String, Object> where) {
        return 0;
    }

    @Override
    public boolean insertWhenNotExists() {
        return false;
    }

    @Override
    public boolean deleteToDB() {
        return false;
    }

    @Override
    public String getDBName() {
        return null;
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
