package com.xmrbi.unware.data.remote;

import com.xmrbi.unware.component.http.Response;
import com.xmrbi.unware.data.entity.deliver.Device;
import com.xmrbi.unware.data.entity.deliver.Drawer;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * Created by wzn on 2018/6/12.
 */
public interface DeliverRemoteSource {
    /**
     * 通过设备的资产编号获取设备列表
     * @param assetCode    资产编号
     * @param storeHouseId 仓库id
     * @return
     */
    @GET("/storehouse/delivery/delivery/mobileDeliveryDevices")
    Observable<Response<List<Device>>> downloadDeliverGoods(@Query("assetCode") String assetCode, @Query("storeHouseId")long storeHouseId);

    /**
     * 获取设备的可放的所有货架
     * @param deviceId
     * @param storeHouseId
     * @return
     */
    @GET("/storehouse/delivery/delivery/mobileQueryDrawers")
    Observable<Response<List<Drawer>>> queryDrawers(@Query("deviceId") long deviceId, @Query("storeHouseId")long storeHouseId);

    /**
     * 上架
     * @param deviceId
     * @param drawerNames
     * @return
     */
    @FormUrlEncoded
    @POST("/storehouse/delivery/delivery/mobileUpdateDeviceDrawer")
    Observable<Response<String>> updateDeviceDrawer(@Field("deviceId") long deviceId, @Field("drawerNames")String drawerNames);
}
