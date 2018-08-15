package com.xmrbi.unware.data.remote;

import com.xmrbi.unware.component.http.Response;
import com.xmrbi.unware.data.entity.main.Rfid;
import com.xmrbi.unware.data.entity.main.StoreHouseAioConfig;
import com.xmrbi.unware.data.entity.main.StoreHouseDevice;
import com.xmrbi.unware.data.entity.main.User;
import com.xmrbi.unware.data.entity.main.Useunit;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;

/**
 * Created by wzn on 2018/4/17.
 */

public interface MainRemoteSource {
    /**
     * 查询租户及现场位置
     *
     * @return
     */
    @GET("storehouse/main/mobileLesseeIdStoreHouse")
    Observable<Response<List<Useunit>>> mobileLesseeIdStoreHouse();

    /**
     * 获取仓库设备配置
     *
     * @param lesseeId
     * @param storeHouseId
     * @return
     */
    @GET("storehouse/main/mobileQueryAioConfig")
    Observable<Response<List<StoreHouseAioConfig>>> mobileQueryAioConfig(@Query("lesseeId") long lesseeId, @Query("storeHouseId") long storeHouseId);

    /**
     * 保存仓库设备配置
     *
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST("storehouse/main/mobileSaveAioConfig")
    Observable<String> mobileSaveAioConfig(@FieldMap Map<String, String> params);

    /**
     * 获取当前在仓库的人员信息
     *
     * @return
     */
    @GET("storehouse/main/queryStoreHouseUser")
    Observable<Response<List<User>>> queryStoreHouseUser(@Query("storeHouseId") long storeHouseId);

    /**
     * 查询当前在库人员
     *
     * @param storeHouseId
     * @return
     */
    @GET("gmms/modules/mobile/device/store-house!queryStoreHouseUser.action")
    Observable<Response<List<User>>> queryStoreHouseUserByGmms(@Query("storeHouseId") long storeHouseId);

    /**
     * 获取rfid码对应的设备
     *
     * @param rfid
     * @return
     */
    @GET("gmms/modules/device/device!showRfidDetail.action")
    Observable<Response<List<Rfid>>> showRfidDetail(@Query("rfid") String rfid);

    /**
     * io控制接口（door门禁，light灯架）
     *
     * @param storeHouseId 仓库id
     * @param ctrlType     io类型(door,light)
     * @param onOrOff      true亮，false暗
     * @return
     */
    @GET("gmms/modules/device/store-house!remoteCtrlIO.action")
    Observable<String> remoteCtrlLight(@Query("storeHouseId") long storeHouseId, @Query("allDrawerIds") String allDrawerIds, @Query("operDrawerIds") String operDrawerIds, @Query("ctrlType") String ctrlType, @Query("onOrOff") boolean onOrOff);

    /**
     * 获取仓库所有的灯光控制器
     *
     * @param storeHouseId
     * @return
     */
    @GET("gmms/modules/mobile/device/store-house!queryDrawersLightControl.action")
    Observable<Response<List<StoreHouseDevice>>> queryDrawersLightControl(@Query("storeHouseId") long storeHouseId);

    /**
     * 上传图片
     */
    @Multipart
    @POST("gmms/modules/disease/disease!imgUploadAnnex.action")
    Observable<String> imgUploadAnnex(@Part MultipartBody.Part imgFile, @Query("userId") long userId);

    /**
     * 获取仓库设备配置
     *
     * @param storeHouseId
     * @param deviceType
     * @param drawerIds
     * @return
     */
    @GET("storehouse/device/mobileQueryStoreHouseDevice")
    Observable<Response<List<StoreHouseDevice>>> queryStoreHouseDevice(@Query("storeHouseId") long storeHouseId, @Query("deviceType") long deviceType, @Query("drawerIds") String drawerIds);

    /**
     * 获取仓库门禁设备的配置
     *
     * @param storeHouseId
     * @return
     */
    @GET("gmms/modules/mobile/device/store-house!queryDoorConfig.action")
    Observable<Response<StoreHouseDevice>> queryDoorConfig(@Query("storeHouseId") long storeHouseId);


    /**
     * 获取仓库配置
     *
     * @param storeHouseId
     * @return
     */
    @GET("gmms/modules/mobile/device/store-house!queryStoreHouseConfig.action")
    Observable<Response<List<StoreHouseAioConfig>>> queryStoreHouseConfig(@Query("storeHouseId") long storeHouseId);

    /**
     * 获取租户
     *
     * @return
     */
    @GET("gmms/modules/mobile/device/store-house!queryUseunitList.action")
    Observable<Response<List<Useunit>>> queryUseunitList();

    /**
     * 保存仓库配置
     *
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST("gmms/modules/mobile/device/store-house!saveStoreHouseConfig.action")
    Observable<String> saveStoreHouseConfig(@FieldMap Map<String, String> params);

    /**
     * 指纹打卡进入仓库触发上位机向gmms发送仓库状态
     *
     * @param storeHouseId
     * @param userId
     * @param oper         入库还是出库
     * @return
     */
    @GET("gmms/modules/mobile/device/store-house!triggerSend.action")
    Observable<Response<String>> triggerSend(@Query("storeHouseId") long storeHouseId, @Query("userId") long userId, @Query("oper") String oper);
}

