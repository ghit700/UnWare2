package com.xmrbi.unware.data.repository;


import com.google.zxing.common.StringUtils;
import com.xmrbi.unware.base.BaseActivity;
import com.xmrbi.unware.base.Config;
import com.xmrbi.unware.component.http.IOTransformer;
import com.xmrbi.unware.component.http.Response;
import com.xmrbi.unware.component.http.RetrofitHelper;
import com.xmrbi.unware.data.entity.main.Rfid;
import com.xmrbi.unware.data.entity.main.StoreHouseAioConfig;
import com.xmrbi.unware.data.entity.main.StoreHouseDevice;
import com.xmrbi.unware.data.entity.main.User;
import com.xmrbi.unware.data.entity.main.Useunit;
import com.xmrbi.unware.data.remote.MainRemoteSource;
import com.xmrbi.unware.utils.EioUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Scheduler;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
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

//    public Observable<String> remoteCtrlIO(long storeHouseId, String allDrawerIds, String operDrawerIds, String crtlType, boolean onOrOff) {
//        return mGmmsMainRemoteSource.remoteCtrlLight(storeHouseId, allDrawerIds, operDrawerIds, crtlType, onOrOff)
//                .compose(new IOTransformer<String>(mBaseActivity));
//    }
    public Observable<String> remoteCtrlIO(long storeHouseId, String allDrawerIds, final String operDrawerIds, final String crtlType, final boolean onOrOff) {
        return mainRemoteSource.queryStoreHouseDevice(storeHouseId, crtlType.equals("door")?6L:9L, allDrawerIds)
                .subscribeOn(Schedulers.io())
                .flatMap(new Function<Response<List<StoreHouseDevice>>, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(Response<List<StoreHouseDevice>> data) throws Exception {
                        if(crtlType.equals("door")){
                            //控门
                            if(data.getData()!=null&&data.getData().size()>0){
                                StoreHouseDevice device=data.getData().get(0);
                                String[] controls=device.getCtrlNumber().split("-");
                                EioUtils.ctrlDoor(device.getIpIn(),device.getPortIn(),onOrOff?Integer.parseInt(controls[0]):Integer.parseInt(controls[1]),controls.length>2?Integer.parseInt(controls[2]):0);
                                return     Observable.just("success");
                            }
                            return  Observable.just("fail");

                        }else{
                            if(data.getData()!=null&&data.getData().size()>0) {
                                List<StoreHouseDevice> lstDevices=data.getData();
                                if(onOrOff){
                                    //亮灯
                                    String[] dark=operDrawerIds.split(",");
                                    List<Integer> darkChannels=new ArrayList<>();
                                    List<Integer> lightChannels=new ArrayList<>();
                                    for(StoreHouseDevice device:lstDevices){
                                        boolean isDark=true;
                                        for (String id:dark) {
                                            if(device.getDrawerIds().contains(id)){
                                                lightChannels.add(Integer.parseInt(device.getCtrlNumber()));
                                                isDark=false;
                                                break;
                                            }
                                        }
                                        if(isDark){
                                            darkChannels.add(Integer.parseInt(device.getCtrlNumber()));
                                        }
                                    }
                                    EioUtils.ctrlMultipleLight(lstDevices.get(0).getIpIn(),lstDevices.get(0).getPortIn(),lightChannels,darkChannels);
                                }else{
                                    //灭灯
                                    String[] dark=operDrawerIds.split(",");
                                    List<Integer> darkChannels=new ArrayList<>();
                                    for(StoreHouseDevice device:lstDevices){
                                        for (String id:dark) {
                                            if(device.getDrawerIds().contains(id)){
                                                darkChannels.add(Integer.parseInt(device.getCtrlNumber()));
                                                break;
                                            }
                                        }
                                    }
                                    EioUtils.ctrlMultipleLight(lstDevices.get(0).getIpIn(),lstDevices.get(0).getPortIn(),new ArrayList<Integer>(),darkChannels);
                                }
                                return Observable.just("success");
                            }
                            return  Observable.just("fail");

                        }

                    }
                })
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

