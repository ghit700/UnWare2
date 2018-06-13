package com.xmrbi.unware.data.local;

import com.blankj.utilcode.util.ToastUtils;
import com.xmrbi.unware.R;
import com.xmrbi.unware.application.UnWareApplication;
import com.xmrbi.unware.component.greendao.StoreHouseDao;
import com.xmrbi.unware.component.greendao.UseunitDao;
import com.xmrbi.unware.data.entity.main.StoreHouse;
import com.xmrbi.unware.data.entity.main.Useunit;

/**
 * Created by wzn on 2018/4/17.
 */

public class MainLocalSource {
    private StoreHouse mStoreHouse;
    private StoreHouseDao mStoreHouseDao;
    private UseunitDao mUseunitDao;

    public MainLocalSource() {
        mStoreHouseDao = UnWareApplication.getInstances().getDaoSession().getStoreHouseDao();
        mUseunitDao = UnWareApplication.getInstances().getDaoSession().getUseunitDao();
    }

    /**
     * 获取仓库数据
     *
     * @return
     */
    public StoreHouse getStoreHouse() {
        if (UnWareApplication.getInstances().getDaoSession().getStoreHouseDao().count() > 0) {
            //只保存当前仓库的数据
            mStoreHouse = UnWareApplication.getInstances().getDaoSession().getStoreHouseDao().loadAll().get(0);
        } else {
//            ToastUtils.showLong(R.string.no_find_store_house_data);
        }
        return mStoreHouse;
    }

    public void saveStoreHouse(StoreHouse house) {
        mStoreHouse = null;
        mStoreHouseDao.deleteAll();
        mStoreHouseDao.insertOrReplace(house);
    }

    public void saveUseunit(Useunit useunit) {
        mUseunitDao.deleteAll();
        mUseunitDao.insertOrReplace(useunit);
    }

    public Useunit getUseunit(Long useunitId) {
        return mUseunitDao.loadByRowId(useunitId);
    }


}
