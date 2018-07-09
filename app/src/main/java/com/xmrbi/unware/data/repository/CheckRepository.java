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

/**
 * Created by wzn on 2018/6/11.
 */
public class CheckRepository extends BaseRepository {
    CheckRemoteSource mCheckRemoteSource;

    public CheckRepository(BaseActivity mBaseActivity) {
        super(mBaseActivity);
        mCheckRemoteSource= RetrofitHelper.getInstance(CheckRemoteSource.class);
    }
    public Observable<Response<List<CheckList>>> mobileCheckStoreDevicesForFive( long storeHouseId){
        return mCheckRemoteSource.mobileCheckStoreDevicesForFive(storeHouseId)
                .compose(new IOTransformer<Response<List<CheckList>>>(mBaseActivity));
    }


    public Observable<Response<List<RfidNewInventoryEntity>>> mobileCountCheckStoreDeviceItem(long checkId) {
        return mCheckRemoteSource.mobileCountCheckStoreDeviceItem(checkId)
                .compose(new IOTransformer<Response<List<RfidNewInventoryEntity>>>(mBaseActivity));
    }

}
