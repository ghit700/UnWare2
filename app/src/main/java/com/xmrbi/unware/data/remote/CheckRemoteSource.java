package com.xmrbi.unware.data.remote;

import com.xmrbi.unware.component.http.Response;
import com.xmrbi.unware.data.entity.check.CheckList;
import com.xmrbi.unware.data.entity.check.RfidNewInventoryEntity;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by wzn on 2018/6/11.
 */
public interface CheckRemoteSource {
    /**
     * 获取最新5条盘点单
     *
     * @param storeHouseId
     * @return
     */
    @GET("storehouse/checkstore/checkstore/mobileCheckStoreDevicesForFive")
    Observable<Response<List<CheckList>>> mobileCheckStoreDevicesForFive(@Query("storeHouseId") long storeHouseId);

    /**
     * 获取盘点单数据
     *
     * @param checkId
     * @return
     */
    @GET("storehouse/checkstore/checkstore/mobileCountCheckStoreDeviceItem")
    Observable<Response<List<RfidNewInventoryEntity>>> mobileCountCheckStoreDeviceItem(@Query("checkId") long checkId);


}
