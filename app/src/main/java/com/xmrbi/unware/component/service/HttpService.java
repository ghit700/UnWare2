package com.xmrbi.unware.component.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.StringUtils;
import com.xmrbi.unware.base.BaseActivity;
import com.xmrbi.unware.component.http.IOTransformer;
import com.xmrbi.unware.component.http.Response;
import com.xmrbi.unware.component.service.ZkTeco.ZkTecoAttLog;
import com.xmrbi.unware.data.entity.main.User;
import com.xmrbi.unware.data.local.MainLocalSource;
import com.xmrbi.unware.data.repository.MainRepository;

import java.io.IOException;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.TimeUnit;

import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 服务请求处理
 * Created by wzn on 2018/8/9.
 */
public class HttpService extends Service {

    private MainRepository mMainRepository;
    private MainLocalSource mMainLocalSource;
    private HttpBinder mBind;
    /**
     * 进入仓库
     */
    private int IN_STOREHOUSE = 0x001;
    /**
     * 出仓库
     */
    private int OUT_STOREHOUSE = 0x002;
    /**
     * 当前有人在库，只是打开仓库的门
     */
    private int OPEN_STOREHOUSE = 0x003;
    /**
     * 当前在库人员
     */
    private User mUser;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new HttpBinder();
    }

    public class HttpBinder extends Binder {

        public void startupService(final BaseActivity activity) {
            WebHttp webHttp = new WebHttp(new Observer() {
                @Override
                public void update(Observable o, Object arg) {
                    mMainRepository = new MainRepository(activity);
                    ObserversRespose respose = (ObserversRespose) arg;
                    LogUtils.d("http respose type:" + respose.getType());
                    LogUtils.d("http respose data:" + respose.getData());
                    switch (respose.getType()) {
                        case 1:
                            //中控指纹机刷指纹
                            inOrOutStoreHouse((ZkTecoAttLog) respose.getData());
                            break;
                    }
                }
            });
            try {
                webHttp.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mMainLocalSource = new MainLocalSource();
        }
    }


    /**
     * 控制本次指纹刷入仓库是进还是出
     *
     * @param zkTecoAttLog 考勤记录
     */
    private void inOrOutStoreHouse(final ZkTecoAttLog zkTecoAttLog) {
        mUser = null;
        Disposable d = mMainRepository.queryStoreHouseUserByGmms(mMainLocalSource.getStoreHouse().getId())
                .flatMap(new Function<Response<List<User>>, ObservableSource<Integer>>() {
                    @Override
                    public ObservableSource<Integer> apply(Response<List<User>> data) throws Exception {
                        //获取当前在库人员
                        if (StringUtils.isEmpty(data.getErrorMsg()) && data.getData().size() > 0) {
                            mUser = data.getData().get(0);
                            if (mUser.getId().toString().equals(zkTecoAttLog.getPin())) {
                                //出库
                                return io.reactivex.Observable.just(OUT_STOREHOUSE);
                            } else {
                                //当前有在库人员，只是打开大门
                                return io.reactivex.Observable.just(OPEN_STOREHOUSE);
                            }
                        } else {
                            mUser = new User();
                            mUser.setId(Long.parseLong(zkTecoAttLog.getPin()));
                            //入库
                            return io.reactivex.Observable.just(IN_STOREHOUSE);

                        }
                    }
                })
                .flatMap(new Function<Integer, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(Integer result) throws Exception {
                        if (result == IN_STOREHOUSE || result == OUT_STOREHOUSE) {
                            //出入库记录操作
                            Disposable d = mMainRepository.triggerSend(mMainLocalSource.getStoreHouse().getId(), mUser.getId(), result == IN_STOREHOUSE ? "1" : "0")
                                    .subscribe(new Consumer<Response<String>>() {
                                        @Override
                                        public void accept(Response<String> result) throws Exception {
                                            if (StringUtils.isEmpty(result.getErrorMsg())) {
                                                LogUtils.d("zkTecoAttLog:" + result.getData());
                                            } else {
                                                LogUtils.d("zkTecoAttLog:" + result.getErrorMsg());
                                            }
                                        }
                                    });
                        }
                        if (result == IN_STOREHOUSE || result == OPEN_STOREHOUSE) {
                            //如果是入库，打开仓库，30s后自动关门
                           Disposable d= io.reactivex.Observable.timer(30, TimeUnit.SECONDS)

                                    .flatMap(new Function<Long, ObservableSource<String>>() {
                                        @Override
                                        public ObservableSource<String> apply(Long aLong) throws Exception {
                                            return mMainRepository.controlDoor(mMainLocalSource.getStoreHouse().getId(), false);
                                        }
                                    })
                                   .subscribeOn(Schedulers.io())
                                   .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Consumer<String>() {
                                        @Override
                                        public void accept(String s) throws Exception {
                                            LogUtils.d("auto close:" + s);
                                        }
                                    });
                        }
                        return mMainRepository.controlDoor(mMainLocalSource.getStoreHouse().getId(), !(result == OUT_STOREHOUSE));
                    }
                })
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        LogUtils.d(s);
                    }
                });
    }


}
