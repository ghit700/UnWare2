package com.xmrbi.unware.data.remote;

import com.xmrbi.unware.component.http.Response;
import com.xmrbi.unware.data.entity.deliver.Device;
import com.xmrbi.unware.data.entity.pick.PickListDetail;
import com.xmrbi.unware.data.entity.pick.PickOrder;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by wzn on 2018/5/17.
 */
public interface PickRemoteSource {
    /**
     * 下载订购单列表
     *
     * @param userId
     * @param storeHouseId
     * @return
     */
    @GET("storehouse/delivery/requisition/mobileList")
    Observable<Response<List<PickOrder>>> downloadPickOrder(@Query("userId") long userId, @Query("storeHouseId") long storeHouseId);

    /**
     * 下载订购单明细
     *
     * @param userId
     * @param storeHouseId
     * @return
     */
    @GET("storehouse/delivery/requisition/mobileStocksRfid")
    Observable<Response<List<PickListDetail>>> downloadPickListDetail(@Query("userId") long userId, @Query("storeHouseId") long storeHouseId, @Query("pickListId") long pickListId);

    /**
     * 结束领料
     *
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST("storehouse/delivery/requisition/mobileSaveStocks")
    Observable<Response<String>> endPick(@FieldMap Map<String, String> params);

    /**
     * 更新设备的rifd码扫描情况（也就是领料情况）
     *
     * @param rfid
     * @param deviceId
     * @return
     */
    @POST("gmms/modules/device/device!updatePickListRfid.action")
    Observable<String> updatePickListRfid(@Query("rfid") String rfid, @Query("deviceId") long deviceId, @Query("pickListId") long pickListId);

    /**
     * 出库
     *
     * @param inOutOrderId
     * @param userId
     * @param inOutOrderListIds
     * @return
     */
    @FormUrlEncoded
    @POST("gmms/modules/mobile/device/in-out-order!endPick.action")
    Observable<Response<String>> endPick(@Field("inOutOrderId") long inOutOrderId, @Field("userId") long userId, @Field("inOutOrderListIds") String inOutOrderListIds);

    /**
     * 通过设备编码获取设备信息
     *
     * @param sequenceCode
     * @return
     */
    @GET("gmms/modules/mobile/device/store-house!queryDeviceBySequenceCode.action")
    Observable<Response<Device>> queryDeviceBySequenceCode(@Query("sequenceCode") String sequenceCode,@Query("lesseeId")long lesseeId);
}
