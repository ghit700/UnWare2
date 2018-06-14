package com.xmrbi.unware.data.repository;

import com.xmrbi.unware.base.BaseActivity;
import com.xmrbi.unware.base.Config;
import com.xmrbi.unware.component.http.IOTransformer;
import com.xmrbi.unware.component.http.Response;
import com.xmrbi.unware.component.http.RetrofitHelper;
import com.xmrbi.unware.data.entity.main.Useunit;
import com.xmrbi.unware.data.entity.pick.PickListDetail;
import com.xmrbi.unware.data.entity.pick.PickOrder;
import com.xmrbi.unware.data.remote.PickRemoteSource;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;

/**
 * Created by wzn on 2018/5/17.
 */
public class PickRepository extends BaseRepository {
    PickRemoteSource mPickRemoteSource;
    PickRemoteSource mGmmsPickRemoteSource;

    public PickRepository(BaseActivity mBaseActivity) {
        super(mBaseActivity);
        mPickRemoteSource = RetrofitHelper.getInstance(PickRemoteSource.class);
        mGmmsPickRemoteSource = RetrofitHelper.getInstance(PickRemoteSource.class, Config.Http.SERVER_GMMS);
    }

    public Observable<Response<List<PickOrder>>> downloadPickOrder(long userId, long storeHouseId) {
        return mPickRemoteSource.downloadPickOrder(userId, storeHouseId)
                .compose(new IOTransformer<Response<List<PickOrder>>>(mBaseActivity));
    }

    public Observable<Response<List<PickListDetail>>> downloadPickListDetail(long userId, long storeHouseId, long pickListId) {
        return mPickRemoteSource.downloadPickListDetail(userId, storeHouseId, pickListId)
                .compose(new IOTransformer<Response<List<PickListDetail>>>(mBaseActivity));
    }

    public Observable<Response<String>> endPick(Map<String, String> params) {
        return mPickRemoteSource.endPick(params)
                .compose(new IOTransformer<Response<String>>(mBaseActivity));
    }

    public Observable<String> updatePickListRfid(String rfid, long deviceId, long pickListId) {
        return mGmmsPickRemoteSource.updatePickListRfid(rfid, deviceId, pickListId)
                .compose(new IOTransformer<String>(mBaseActivity));
    }
}
