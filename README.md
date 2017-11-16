
LKDBHelper-Android
====================================

一款Android端的数据库框架。

iOS版本查看：[LKDB-iOS](https://github.com/li6185377/LKDBHelper-SQLite-ORM)

# Gradle依赖

在总工程依赖插件

```groovy
classpath 'homhomlin.lib:lkdbhelper-compiler:1.0.7'

```

在工程依赖库

```groovy
compile 'homhomlin.lib:lkdbhelper-lib:1.0.7'
```

在主工程使用LKDB插件

```groovy
apply plugin: 'lkdbhelper'
```

# 用法

## Step 1.

初始化LKDBHelper，在应用程序初始化时调用：

```java
LKDBHelper.init(getApplicationContext());
```

## Step 2.

将你的Model实现 ILKDBModel 接口，不需要具体实现其中方法，LKDB-Android会自动实现接口内的方法代码。

* `之所以采用接口是为了避免单继承导致需要调整继承关系的问题，所以LKDB-Android可以在不影响你原有逻辑的基础上有效解耦和拓展。`

```java
public class TestModel  implements ILKDBModel{
	public int id;
	public String name;
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
    public long rowCountWithWhere(Object where) {
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
```

编译工程后，LKDB-Android会自动往方法中织入对应实现代码，无需使用者关心。

## Step 2.

`可直接通过Model操作Model以及数据库。`

```java
TestModel model = new TestModel();
model.id = 2;
model.name = "test";

//保存到数据库
model.insertToDB();

//删除
model.deleteToDB();
```

* 与iOS版LKDBHelper接口一致。

# LKDBHelper自定义数据库


`通过LKDBHelper操作Model以及数据库`

LKDBHelper是LKDB-Android的一个重要类，他能构建数据库的基础数据。

获得一个默认的LKDB并且将model插入数据库。

```java
ILKDBHelper lkdbHelper = LKDBHelper.getHelper();


lkdbHelper.insertToDB(model);//等价于model.insertToDB(model);

```

其他操作类似。

`自定义数据库`

创建一个test.db，并且使用这个DB插入数据

```java

ILKDBHelper lkdbHelper = LKDBHelper.getHelper("test.db");
lkdbHelper.insertToDB(model);

```

# Model设置

通过设置Model能达到自定义表和指定数据库的能力。

重写Model的getDBName()以及getTableName()方法。

```java
	@Override
    public String getTableName() {
    	//将该Model在名为“test”的表中操作。
        return "test";
    }

    @Override
    public String getDBName() {
    	//将该Model在名为“test.db”的数据库中操作。
        return "test.db";
    }

```
然后操作该Model

```java
TestModel model = new TestModel();
model.id = 2;
model.name = "test";

//插入到“test.db”数据库的“test”表中
//该操作等价于获得一个test.db的LKDBHelper，然后insertToDB("test");
model.insertToDB();

//从“test.db”数据库中的“test”表中删除该model
model.deleteToDB();
```

通过上述操作，改变方法返回值，可以完成动态的数据库和表变更操作。


## select 、 delete 、 update 、 isExists 、 rowCount ...

```java
    
      ILKDBHelper lkdbHelper = LKDBHelper.getHelper("test.db");
	  lkdbHelper.search(...);  

	  lkdbHelper.delete(...);

	  etc...
	  //api与iOS版相同
     
```
## "where"

在LKDBHelper中，你可以看到很多where参数，这个参数仅支持String和Map<String,Object>。

## 排除Model的参数

如果Model中有的字段不想插入数据库，则在该字段上注解@No4LKDB

```java
public class TestModel  implements ILKDBModel{	

	@No4LKDB
	public int id;//id字段不插入数据库

	public String name;
	}
```

## Developed By

 * Linhonghong - <linhh90@163.com>

## License
Copyright 2017 LinHongHong

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
