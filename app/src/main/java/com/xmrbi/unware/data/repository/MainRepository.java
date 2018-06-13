package com.xmrbi.unware.data.repository;


import com.xmrbi.unware.base.BaseActivity;
import com.xmrbi.unware.base.Config;
import com.xmrbi.unware.component.http.IOTransformer;
import com.xmrbi.unware.component.http.Response;
import com.xmrbi.unware.component.http.RetrofitHelper;
import com.xmrbi.unware.data.entity.main.Rfid;
import com.xmrbi.unware.data.entity.main.StoreHouseAioConfig;
import com.xmrbi.unware.data.entity.main.User;
import com.xmrbi.unware.data.entity.main.Useunit;
import com.xmrbi.unware.data.remote.MainRemoteSource;

import java.io.File;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;


/**
 * Created by wzn on 2018/4/17.
 */

public class MainRepository extends BaseRepository {
    private MainRemoteSource mainRemoteSource;
    private MainRemoteSource mGmmsMainRemoteSource;


    public MainRepository(BaseActivity baseActivity) {
        super(baseActivity);
        mBaseActivity = baseActivity;
        mainRemoteSource = RetrofitHelper.getInstance(MainRemoteSource.class);
        mGmmsMainRemoteSource = RetrofitHelper.getInstance(MainRemoteSource.class, Config.Http.SERVER_GMMS);
    }

    public Observable<Response<List<Useunit>>> mobileLesseeIdStoreHouse() {
        return mainRemoteSource.mobileLesseeIdStoreHouse()
                .compose(new IOTransformer<Response<List<Useunit>>>(mBaseActivity));
    }

    public Observable<Response<List<User>>> queryStoreHouseUser(long storeHouseId) {
        return mainRemoteSource.queryStoreHouseUser(storeHouseId)
                .compose(new IOTransformer<Response<List<User>>>(mBaseActivity));
    }

    public Observable<Response<List<StoreHouseAioConfig>>> mobileQueryAioConfig(long lesessId, long storeHouseId) {
        return mainRemoteSource.mobileQueryAioConfig(lesessId, storeHouseId);
    }

    public Observable<Response<List<Rfid>>> showRfidDetail(String rfids) {
        return mGmmsMainRemoteSource.showRfidDetail(rfids)
                .compose(new IOTransformer<Response<List<Rfid>>>(mBaseActivity));
    }

    public Observable<String> mobileSaveAioConfig(Map<String, String> params) {
        return mainRemoteSource.mobileSaveAioConfig(params)
                .compose(new IOTransformer<String>(mBaseActivity));
    }

    public Observable<String> remoteCtrlIO(long storeHouseId, String allDrawerIds, String operDrawerIds, String crtlType, boolean onOrOff) {
        return mGmmsMainRemoteSource.remoteCtrlLight(storeHouseId, allDrawerIds, operDrawerIds, crtlType, onOrOff)
                .compose(new IOTransformer<String>(mBaseActivity));
    }

    public Observable<String> imgUploadAnnex(File file,Long userId) {
        if (file.exists()) {
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
            return mGmmsMainRemoteSource.imgUploadAnnex(part,userId);
        } else {
            return Observable.just("文件不存在");
        }

    }

}

