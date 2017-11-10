package demo.lhh.com.lkdbhelper;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.lhh.lkdb.ILKDBHelper;
import com.lhh.lkdb.LKDBHelper;
import com.lhh.lkdb.sql.LKDBQueryParams;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LKDBHelper.init(getApplicationContext());
        ILKDBHelper lkdbHelper = LKDBHelper.getHelper("test.db");
        TestModel testModel = new TestModel();
        testModel.maptest.put("test1","ttt");
        testModel.listtest.add("listtest1");
//        testModel.id=1;
//        testModel.a = 2;
        boolean f = lkdbHelper.insertToDB(testModel);
        if(f){
            Toast.makeText(this,"插入成功,",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this,"插入失败,",Toast.LENGTH_SHORT).show();
        }

//        long row = lkdbHelper.rowCount(testModel);
//        Toast.makeText(this,"行数：" + row,Toast.LENGTH_SHORT).show();


        Map<String,Object> map = new HashMap<>();
        map.put("a", 88);
        TestModel testModel1 = lkdbHelper.searchSingle(testModel.getClass(),map,null);

        testModel.a = 5;
//        lkdbHelper.updateToDB(testModel, map);
//        testModel1.a = 2;

//        boolean result = lkdbHelper.isExistsModel(testModel);

//        Toast.makeText(this,"存在情况：" + result,Toast.LENGTH_SHORT).show();

//        List<TestModel> list = lkdbHelper.searchWithSQL("select * from demo_lhh_com_lkdbhelper_TestModel", TestModel.class);

//        if(list != null){
//            Toast.makeText(this,"行数：" + list.size(),Toast.LENGTH_SHORT).show();
//
//        }
    }
}
