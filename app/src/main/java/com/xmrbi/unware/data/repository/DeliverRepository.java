package com.xmrbi.unware.data.repository;

import com.xmrbi.unware.base.BaseActivity;
import com.xmrbi.unware.base.Config;
import com.xmrbi.unware.component.http.IOTransformer;
import com.xmrbi.unware.component.http.Response;
import com.xmrbi.unware.component.http.RetrofitHelper;
import com.xmrbi.unware.data.entity.deliver.Device;
import com.xmrbi.unware.data.entity.deliver.Drawer;
import com.xmrbi.unware.data.entity.deliver.InOutOrder;
import com.xmrbi.unware.data.entity.deliver.InOutOrderList;
import com.xmrbi.unware.data.remote.DeliverRemoteSource;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by wzn on 2018/6/12.
 */
public class DeliverRepository extends BaseRepository {
    DeliverRemoteSource mDeliverRemoteSource;
    DeliverRemoteSource mGmmsDeliverRemoteSource;

    public DeliverRepository(BaseActivity mBaseActivity) {
        super(mBaseActivity);
        mDeliverRemoteSource = RetrofitHelper.getInstance(DeliverRemoteSource.class);
        mGmmsDeliverRemoteSource = RetrofitHelper.getInstance(DeliverRemoteSource.class, Config.Http.SERVER_GMMS);
    }

    public Observable<Response<List<Device>>> downloadDeliverGoods(String assetCode, long storeHouseId) {
        return mDeliverRemoteSource.downloadDeliverGoods(assetCode, storeHouseId)
                .compose(new IOTransformer<Response<List<Device>>>(mBaseActivity));
    }

    public Observable<Response<List<Drawer>>> queryDrawers(long deviceId, long storeHouseId) {
        return mDeliverRemoteSource.queryDrawers(deviceId, storeHouseId)
                .compose(new IOTransformer<Response<List<Drawer>>>(mBaseActivity));
    }

    public Observable<Response<List<Drawer>>> queryDrawersByGmms(long deviceId, long storeHouseId) {
        return mGmmsDeliverRemoteSource.queryDrawersByGmms(deviceId, storeHouseId)
                .compose(new IOTransformer<Response<List<Drawer>>>(mBaseActivity));
    }

    public Observable<Response<String>> updateDeviceDrawer(long deviceId, String drawerNames, long storeHouseId) {
        return mDeliverRemoteSource.updateDeviceDrawer(deviceId, drawerNames, storeHouseId)
                .compose(new IOTransformer<Response<String>>(mBaseActivity));
    }

    public Observable<Response<List<InOutOrder>>> queryInOutOrderList(long userId, Long storeHouseId, boolean inOrOut) {
        return mGmmsDeliverRemoteSource.queryInOutOrderList(userId, storeHouseId, inOrOut)
                .compose(new IOTransformer<Response<List<InOutOrder>>>(mBaseActivity));
    }

    public Observable<Response<List<InOutOrderList>>> queryInOutOrderDetail(long inOutOrderId) {
        return mGmmsDeliverRemoteSource.queryInOutOrderDetail(inOutOrderId)
                .compose(new IOTransformer<Response<List<InOutOrderList>>>(mBaseActivity));
    }

    public Observable<Response<String>> endDeliver(long inOutOrderId, long userId,String inOutOrderListIds) {
        return mGmmsDeliverRemoteSource.endDeliver(inOutOrderId, userId,inOutOrderListIds)
                .compose(new IOTransformer<Response<String>>(mBaseActivity));
    }


}
