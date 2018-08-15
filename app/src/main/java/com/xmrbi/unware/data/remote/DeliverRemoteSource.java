package com.xmrbi.unware.data.remote;

import com.xmrbi.unware.component.http.Response;
import com.xmrbi.unware.data.entity.deliver.Device;
import com.xmrbi.unware.data.entity.deliver.Drawer;
import com.xmrbi.unware.data.entity.deliver.InOutOrder;
import com.xmrbi.unware.data.entity.deliver.InOutOrderList;

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
     *
     * @param assetCode    资产编号
     * @param storeHouseId 仓库id
     * @return
     */
    @GET("/storehouse/delivery/delivery/mobileDeliveryDevices")
    Observable<Response<List<Device>>> downloadDeliverGoods(@Query("assetCode") String assetCode, @Query("storeHouseId") long storeHouseId);

    /**
     * 获取设备的可放的所有货架
     *
     * @param deviceId
     * @param storeHouseId
     * @return
     */
    @GET("/storehouse/delivery/delivery/mobileQueryDrawers")
    Observable<Response<List<Drawer>>> queryDrawers(@Query("deviceId") long deviceId, @Query("storeHouseId") long storeHouseId);

    /**
     * 获取设备可放的所有货架
     *
     * @param deviceId
     * @param storeHouseId
     * @return
     */
    @GET("gmms/modules/mobile/device/store-house!queryDrawers.action")
    Observable<Response<List<Drawer>>> queryDrawersByGmms(@Query("deviceId") long deviceId, @Query("storeHouseId") long storeHouseId);

    /**
     * 上架
     *
     * @param deviceId
     * @param drawerNames
     * @return
     */
    @FormUrlEncoded
    @POST("/storehouse/delivery/delivery/mobileUpdateDeviceDrawer")
    Observable<Response<String>> updateDeviceDrawer(@Field("deviceId") long deviceId, @Field("drawerNames") String drawerNames, @Query("storeHouseId") long storeHouseId);

    /**
     * 获取出入库单列表
     *
     * @param userId       用户id
     * @param storeHouseId 仓库id
     * @param inOrOut      出入库类型
     * @return
     */
    @GET("gmms/modules/mobile/device/in-out-order!queryInOutOrderList.action")
    Observable<Response<List<InOutOrder>>> queryInOutOrderList(@Query("userId") Long userId, @Query("storeHouseId") long storeHouseId, @Query("inOrOut") boolean inOrOut);

    /**
     * 查询入库单明细
     *
     * @param inOutOrderId
     * @return
     */
    @GET("gmms/modules/mobile/device/in-out-order!queryInOutOrderDetail.action")
    Observable<Response<List<InOutOrderList>>> queryInOutOrderDetail(@Query("inOutOrderId") long inOutOrderId);

    /**
     * 入账
     *
     * @param inOutOrderId
     * @param userId
     * @param inOutOrderListIds 需要入账的入库单明细
     * @return
     */
    @FormUrlEncoded
    @POST("gmms/modules/mobile/device/in-out-order!endDeliver.action")
    Observable<Response<String>> endDeliver(@Field("inOutOrderId") long inOutOrderId, @Field("userId") long userId, @Field("inOutOrderListIds") String inOutOrderListIds);
}
