package com.xmrbi.unware.data.repository;

import com.xmrbi.unware.base.BaseActivity;
import com.xmrbi.unware.base.Config;
import com.xmrbi.unware.component.http.IOTransformer;
import com.xmrbi.unware.component.http.Response;
import com.xmrbi.unware.component.http.RetrofitHelper;
import com.xmrbi.unware.data.entity.check.CheckList;
import com.xmrbi.unware.data.entity.check.RfidNewInventoryEntity;
import com.xmrbi.unware.data.remote.CheckRemoteSource;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Query;

/**
 * Created by wzn on 2018/6/11.
 */
public class CheckRepository extends BaseRepository {
    CheckRemoteSource mCheckRemoteSource;
    CheckRemoteSource mGmmsCheckRemoteSource;

    public CheckRepository(BaseActivity mBaseActivity) {
        super(mBaseActivity);
        mCheckRemoteSource= RetrofitHelper.getInstance(CheckRemoteSource.class);
        mGmmsCheckRemoteSource= RetrofitHelper.getInstance(CheckRemoteSource.class, Config.Http.SERVER_GMMS);
    }
    public Observable<Response<List<CheckList>>> mobileCheckStoreDevicesForFive( long storeHouseId){
        return mCheckRemoteSource.mobileCheckStoreDevicesForFive(storeHouseId)
                .compose(new IOTransformer<Response<List<CheckList>>>(mBaseActivity));
    }


    public Observable<Response<List<RfidNewInventoryEntity>>> countCheckStoreDeviceItemOrRfid(long checkId) {
        return mGmmsCheckRemoteSource.countCheckStoreDeviceItemOrRfid(checkId)
                .compose(new IOTransformer<Response<List<RfidNewInventoryEntity>>>(mBaseActivity));
    }

}
