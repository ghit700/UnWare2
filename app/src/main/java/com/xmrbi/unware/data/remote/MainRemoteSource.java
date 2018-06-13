package com.xmrbi.unware.data.remote;

import com.xmrbi.unware.component.http.Response;
import com.xmrbi.unware.data.entity.main.Rfid;
import com.xmrbi.unware.data.entity.main.StoreHouseAioConfig;
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
     * @param drawerIds    货架id
     * @param ctrlType     io类型(door,light)
     * @param onOrOff      true亮，false暗
     * @return
     */
    @GET("gmms/modules/device/store-house!remoteCtrlIO.action")
    Observable<String> remoteCtrlLight(@Query("storeHouseId") long storeHouseId, @Query("allDrawerIds") String allDrawerIds, @Query("operDrawerIds") String operDrawerIds, @Query("ctrlType") String ctrlType, @Query("onOrOff") boolean onOrOff);

    /**
     * 上传图片
     */
    @Multipart
    @POST("gmms/modules/disease/disease!imgUploadAnnex.action")
    Observable<String> imgUploadAnnex(@Part MultipartBody.Part imgFile,@Query("userId")long userId);
}
