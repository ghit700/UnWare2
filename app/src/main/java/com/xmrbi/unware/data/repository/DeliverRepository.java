package com.xmrbi.unware.data.repository;

import com.xmrbi.unware.base.BaseActivity;
import com.xmrbi.unware.component.http.IOTransformer;
import com.xmrbi.unware.component.http.Response;
import com.xmrbi.unware.component.http.RetrofitHelper;
import com.xmrbi.unware.data.entity.deliver.Device;
import com.xmrbi.unware.data.entity.deliver.Drawer;
import com.xmrbi.unware.data.remote.DeliverRemoteSource;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by wzn on 2018/6/12.
 */
public class DeliverRepository extends BaseRepository {
    DeliverRemoteSource mDeliverRemoteSource;

    public DeliverRepository(BaseActivity mBaseActivity) {
        super(mBaseActivity);
        mDeliverRemoteSource = RetrofitHelper.getInstance(DeliverRemoteSource.class);
    }

   public Observable<Response<List<Device>>> downloadDeliverGoods(String assetCode, long storeHouseId) {
        return mDeliverRemoteSource.downloadDeliverGoods(assetCode, storeHouseId)
                .compose(new IOTransformer<Response<List<Device>>>(mBaseActivity));
    }
   public Observable<Response<List<Drawer>>> queryDrawers(long deviceId, long storeHouseId) {
        return mDeliverRemoteSource.queryDrawers(deviceId, storeHouseId)
                .compose(new IOTransformer<Response<List<Drawer>>>(mBaseActivity));
    }

   public Observable<Response<String>> updateDeviceDrawer(long deviceId, String drawerNames) {
        return mDeliverRemoteSource.updateDeviceDrawer(deviceId, drawerNames)
                .compose(new IOTransformer<Response<String>>(mBaseActivity));
    }

}
