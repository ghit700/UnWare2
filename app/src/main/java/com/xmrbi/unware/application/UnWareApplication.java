package com.xmrbi.unware.application;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDex;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;
import com.xmrbi.unware.base.Config;
import com.xmrbi.unware.component.greendao.DaoMaster;
import com.xmrbi.unware.component.greendao.DaoSession;
import com.xmrbi.unware.utils.ImageLoader;


/**
 * Created by wzn on 2018/5/3.
 */

public class UnWareApplication extends Application {

    private static UnWareApplication instances;
    private DaoSession mDaoSession;

    @Override
    public void onCreate() {
        super.onCreate();
        instances = this;
        //初始化utils
        initUtils();
        initDb();
        //讯飞语音在线合成

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //分包
        MultiDex.install(this);
    }

    private void initUtils() {
        Utils.init(this);
        //log
        LogUtils.getConfig().setLogSwitch(Config.IS_OPEN_LOG);
    }

    /**
     * 初始化数据库
     */
    private void initDb() {
        // 通过 DaoMaster 的内部类 DevOpenHelper，你可以得到一个便利的 SQLiteOpenHelper 对象。
        // 可能你已经注意到了，你并不需要去编写「CREATE TABLE」这样的 SQL 语句，因为 greenDAO 已经帮你做了。
        // 注意：默认的 DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。
        // 所以，在正式的项目中，你还应该做一层封装，来实现数据库的安全升级。
        DaoMaster.DevOpenHelper mHelper = new DaoMaster.DevOpenHelper(this, Config.DB.DB_NAME, null);
        SQLiteDatabase db = mHelper.getWritableDatabase();
        // 注意：该数据库连接属于 DaoMaster，所以多个 Session 指的是相同的数据库连接。
        DaoMaster mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();
    }

    /**
     * 获取数据库的session
     *
     * @return
     */
    public DaoSession getDaoSession() {
        return mDaoSession;
    }

    public static UnWareApplication getInstances() {
        return instances;
    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();
        ImageLoader.GuideClearMemory(this);
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        ImageLoader.GuideClearMemory(this);
    }


}
