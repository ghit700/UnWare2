package com.xmrbi.unware.module.main.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.CrashUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.xmrbi.unware.R;
import com.xmrbi.unware.base.BaseActivity;
import com.xmrbi.unware.base.Config;
import com.xmrbi.unware.component.http.ExceptionHandle;
import com.xmrbi.unware.component.http.HttpUtils;
import com.xmrbi.unware.component.http.ResponseObserver;
import com.xmrbi.unware.data.entity.main.StoreHouse;
import com.xmrbi.unware.data.entity.main.User;
import com.xmrbi.unware.data.local.MainLocalSource;
import com.xmrbi.unware.data.repository.MainRepository;
import com.xmrbi.unware.event.SettingEvent;
import com.xmrbi.unware.event.UserEvent;
import com.xmrbi.unware.module.main.adapter.UserAdapter;
import com.xmrbi.unware.module.setting.activity.SettingActivity;
import com.xmrbi.unware.utils.ActivityStackUtils;
import com.xmrbi.unware.utils.RxBus;
import com.xmrbi.unware.utils.UpdateUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;


public class MainActivity extends BaseActivity {


    @BindView(R.id.listMainLstUsers)
    RecyclerView listMainLstUsers;
    @BindView(R.id.tvMainSetting)
    TextView tvMainSetting;


    private UserAdapter mAdapter;

    private List<User> mLstUsers;
    private MainLocalSource mMainLocalSource;
    private MainRepository mMainRepository;
    private StoreHouse mStoreHouse;
    /**
     * 请求在库人员的订阅，在设置页面的时候停止
     */
    private Disposable mDisposable;
    private UpdateUtils mUpdateUtils;

    @Override
    protected int getLayout() {
        return R.layout.main_activity;
    }

    @Override
    protected void onViewCreated() {
        listMainLstUsers.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        mLstUsers = new ArrayList<>();
        mAdapter = new UserAdapter(mLstUsers);
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                User user = mLstUsers.get(position);
                RfidStoreHouseActivity.lauch(mContext, user);
            }
        });
        listMainLstUsers.setAdapter(mAdapter);


    }

    @Override
    protected void initEventAndData() {
        //应用全屏显示
        closeAllScreen(false);
        //判断是否进入设置页面
        if (!SPUtils.getInstance(Config.SP.SP_NAME).getBoolean(Config.SP.SP_IS_SETTING)) {
            ActivityUtils.startActivity(MainActivity.this, SettingActivity.class);
        } else {
            HttpUtils.resetServerAddress();
        }
        //文件崩溃收集
        initCrash();
        //初始化
        mMainLocalSource = new MainLocalSource();
        mMainRepository = new MainRepository(this);
        mUpdateUtils=new UpdateUtils(mContext);
        //定时查询在库人员
        queryStoreHouseUser();
        //接收设置修改事件
        RxBus.getDefault()
                .toObservable(SettingEvent.class)
                .subscribe(new Consumer<SettingEvent>() {
                    @Override
                    public void accept(SettingEvent settingEvent) throws Exception {
                        queryStoreHouseUser();
                    }
                });

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeAllScreen(false);
    }

    /**
     * 启动设置页面
     */
    @OnClick(R.id.tvMainSetting)
    public void lauchSetting() {
        //取消查询用户
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
        lauch(SettingActivity.class);
    }

    /**
     * 定时查询在库人员
     */
    private void queryStoreHouseUser() {
        mMainLocalSource = new MainLocalSource();
        mMainRepository = new MainRepository(this);
        mStoreHouse = mMainLocalSource.getStoreHouse();
        mDisposable = Observable.interval(0, 10, TimeUnit.SECONDS)
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long time) throws Exception {
                        if (mStoreHouse != null) {
                            mMainRepository.queryStoreHouseUser(mStoreHouse.getId())
                                    .subscribe(new ResponseObserver<List<User>>(MainActivity.this, false, false) {
                                        @Override
                                        public void handleData(List<User> data) {
                                            RxBus.getDefault().post(new UserEvent(data));
                                            mLstUsers.clear();
                                            mLstUsers.addAll(data);
                                            mAdapter.notifyDataSetChanged();
                                        }

                                        //没有查询到用户
                                        @Override
                                        protected void handleErrorData() {
                                            super.handleErrorData();
                                            findNonUserOrError();
                                        }

                                        //报错
                                        @Override
                                        protected void onError(ExceptionHandle.ResponeThrowable e) {
                                            super.onError(e);
                                            findNonUserOrError();
                                        }

                                        @Override
                                        public void onComplete() {
                                            if (ActivityStackUtils.getAllActivities().size() == 1) {
                                                //更新apk
                                                mUpdateUtils.updateAPK();
                                            }
                                            super.onComplete();
                                        }
                                    });
                        }
                    }
                });


    }

    /**
     * 没有查询到用户或是接口报错
     */
    private void findNonUserOrError() {
        mLstUsers.clear();
        mAdapter.notifyDataSetChanged();
        ActivityStackUtils.finishAllActivity(this);
    }

    /**
     * 设置全屏
     *
     * @param isClose false全屏true关闭全屏
     */
    private void closeAllScreen(boolean isClose) {
        //应用全屏
        Intent intent = new Intent();
        intent.setAction("android.intent.action.hidenavigation");
        intent.putExtra("enable", isClose);
        sendBroadcast(intent);
    }

    /**
     * 初始化崩溃文件日志以便上传回溯崩溃
     */
    private void initCrash() {
        if (Build.VERSION.SDK_INT >= 23) {
            new RxPermissions(this).request(Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(new Consumer<Boolean>() {
                @Override
                public void accept(@NonNull Boolean granted) throws Exception {
                    if (granted) {
                        //崩溃日志
                        CrashUtils.init(Config.Crash.CRASH_DIR);
                    } else {
                        ToastUtils.showLong(R.string.permissions_fail);
                    }
                }
            });
        } else {
            CrashUtils.init(Config.Crash.CRASH_DIR);
        }
    }

}
