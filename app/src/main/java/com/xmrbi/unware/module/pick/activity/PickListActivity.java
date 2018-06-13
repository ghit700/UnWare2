package com.xmrbi.unware.module.pick.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xmrbi.unware.R;
import com.xmrbi.unware.base.BaseActivity;
import com.xmrbi.unware.base.Config;
import com.xmrbi.unware.component.http.BaseObserver;
import com.xmrbi.unware.component.http.IOTransformer;
import com.xmrbi.unware.component.http.Response;
import com.xmrbi.unware.component.http.ResponseObserver;
import com.xmrbi.unware.component.rfid.RfidClient;
import com.xmrbi.unware.data.entity.main.Rfid;
import com.xmrbi.unware.data.entity.main.StoreHouse;
import com.xmrbi.unware.data.entity.main.StoreHouseAioConfig;
import com.xmrbi.unware.data.entity.main.User;
import com.xmrbi.unware.data.entity.pick.PickListDetail;
import com.xmrbi.unware.data.entity.pick.PickOrder;
import com.xmrbi.unware.data.local.MainLocalSource;
import com.xmrbi.unware.data.repository.MainRepository;
import com.xmrbi.unware.data.repository.PickRepository;
import com.xmrbi.unware.event.PickEvent;
import com.xmrbi.unware.module.pick.adapter.PickOrderAdapter;
import com.xmrbi.unware.utils.ActivityStackUtils;
import com.xmrbi.unware.utils.CameraUtils;
import com.xmrbi.unware.utils.DialogUtils;
import com.xmrbi.unware.utils.MediaUtils;
import com.xmrbi.unware.utils.RfidUtils;
import com.xmrbi.unware.utils.RxBus;

import org.greenrobot.greendao.annotation.NotNull;
import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static android.R.attr.order;
import static android.R.attr.readPermission;
import static android.R.attr.track;

/**
 * Created by wzn on 2018/5/17.
 */
public class PickListActivity extends BaseActivity {
    @BindView(R.id.ivPickBack)
    ImageView ivPickBack;
    @BindView(R.id.tvPickWelcome)
    TextView tvPickWelcome;
    @BindView(R.id.btnPickExit)
    Button btnPickExit;
    @BindView(R.id.listPickOrder)
    RecyclerView listPickOrder;
    @BindView(R.id.svPickTakePhoto)
    SurfaceView svPickTakePhoto;

    public static void lauch(Context context, User user) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("user", user);
        ActivityStackUtils.lauch(context, PickListActivity.class, bundle);
    }

    private MainLocalSource mMainLocalSource;
    private PickRepository mPickRepository;
    private User mUser;
    private StoreHouse mStoreHouse;
    private List<PickOrder> mlstPickOrders;
    private PickOrderAdapter mAdapter;
    private CameraUtils mCameraUtils;
    /**
     * 本次领料所有的rfid
     */
    private String mRfidCodes;
    /**
     * 提示正在识别用户的dailog
     */
    private MaterialDialog mAlertDialog;
    private MainRepository mMainRepository;
    private RfidClient mRfidClient;

    @Override
    protected int getLayout() {
        return R.layout.pick_activity_pick_list;
    }

    @Override
    protected void onViewCreated() {
        listPickOrder.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mlstPickOrders = new ArrayList<>();
        mAdapter = new PickOrderAdapter(mlstPickOrders);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                lauchPickDetail(mlstPickOrders.get(position).getBsnum(), mlstPickOrders.get(position));
            }
        });
        listPickOrder.setAdapter(mAdapter);

    }

    @Override
    protected void initEventAndData() {
        mMainLocalSource = new MainLocalSource();
        mStoreHouse = mMainLocalSource.getStoreHouse();
        mPickRepository = new PickRepository(this);
        mMainRepository = new MainRepository(this);
        mUser = (User) mBundle.getSerializable("user");
        tvPickWelcome.setText(mUser.getName() + ",欢迎光临" + mStoreHouse.getName() + "无人值守仓库");
        //后台拍照
        mCameraUtils = new CameraUtils(svPickTakePhoto);
        //下载订购单
        downloadPickOrder();
        //获取领料成功的rfid列表
        getPickSuccessListRfidCodes();
        //若是领料成功，重新获取rfid列表
        Disposable d = RxBus.getDefault()
                .toObservable(PickEvent.class)
                .compose(this.<PickEvent>bindToLifecycle())
                .subscribe(new Consumer<PickEvent>() {
                    @Override
                    public void accept(PickEvent pickEvent) throws Exception {
                        getPickSuccessListRfidCodes();
                    }
                });

    }

    /**
     * 获取领料成功的rfid列表
     */
    private void getPickSuccessListRfidCodes() {
        mRfidCodes = SPUtils.getInstance(Config.SP.SP_NAME).getString(Config.SP.SP_RFID_CODES);
    }

    /**
     * 进入订购单详情
     */
    private void lauchPickDetail(final String bsnum, final PickOrder order) {
        new MaterialDialog.Builder(this)
                .title(R.string.pick_list_dailog_title)
                .content(R.string.pick_list_warning, true)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        //提示框
                        if (mAlertDialog == null) {
                            mAlertDialog = new MaterialDialog.Builder(mContext)
                                    .title(R.string.pick_list_dailog_title)
                                    .content(R.string.pick_list_lauch_content)
                                    .progress(true, 0)
                                    .progressIndeterminateStyle(false)
                                    .canceledOnTouchOutside(false)
                                    .show();
                        } else {
                            mAlertDialog.show();
                        }
                        //创建目录
                        FileUtils.createOrExistsDir(Config.FileConfig.PICK_PHOTO_DIR);
                        //拍摄图片
                        mCameraUtils.takePicture(Config.FileConfig.PICK_PHOTO_DIR + bsnum + "-start.jpg")
                                .compose(new IOTransformer<Boolean>(PickListActivity.this))
                                .subscribe(new Consumer<Boolean>() {
                                    @Override
                                    public void accept(Boolean success) throws Exception {
                                        mAlertDialog.dismiss();
//                                        if (success) {
                                        PickDetailActivity.lauch(mContext, mUser, order);
//                                        } else {
//                                            ToastUtils.showLong(R.string.pick_list_take_photo_error);
//                                        }

                                    }
                                });
                    }
                })
                .positiveText(R.string.setting_agree)
                .negativeText(R.string.main_cancel)
                .show();
    }

    /**
     * 下载订购单信息
     */
    private void downloadPickOrder() {
        mPickRepository.downloadPickOrder(mUser.getId(), mStoreHouse.getId())
                .subscribe(new ResponseObserver<List<PickOrder>>(this) {
                    @Override
                    public void handleData(List<PickOrder> data) {
                        mlstPickOrders.addAll(data);
                        mAdapter.notifyDataSetChanged();
                    }
                });
    }


    /**
     * 回退
     */
    @OnClick(R.id.ivPickBack)
    public void back() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    /**
     * 退出仓库
     */
    @OnClick(R.id.btnPickExit)
    public void exitStoreHouse() {
        final MaterialDialog scanDialog = new MaterialDialog.Builder(mContext)
                .content(R.string.pick_list_detail_scan)
                .progress(true, 0)
                .progressIndeterminateStyle(false)
                .canceledOnTouchOutside(false)
                .show();
        //错误编码
        final StringBuffer errorCodes = new StringBuffer();
        Disposable d = mMainRepository.mobileQueryAioConfig(mStoreHouse.getLesseeId(), mStoreHouse.getId())
                .subscribeOn(Schedulers.io())
                .flatMap(new Function<Response<List<StoreHouseAioConfig>>, ObservableSource<Integer>>() {
                    @Override
                    public ObservableSource<Integer> apply(Response<List<StoreHouseAioConfig>> data) {
                        //连接rfid
                        StoreHouseAioConfig config = data.getData().get(0);
                        String rfidIp = config.getRfidIp();
                        Integer rfidPort = config.getRfidPort();
                        mRfidClient = new RfidClient(rfidIp, rfidPort);
                        try {
                            if (mRfidClient.connect()) {
                                //50次单次扫描
                                return Observable.range(1, 50);
                            } else {
                                return Observable.just(-1);

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            return Observable.just(-1);
                        }

                    }
                })
                .doOnNext(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        if (integer > 0) {
                            //判断是否有错签
                            List<String> rfidList = null;
                            try {
                                if (mRfidClient != null) {
                                    rfidList = mRfidClient.startRead();
                                    if (rfidList != null && !rfidList.isEmpty()) {
                                        for (String code : rfidList) {
                                            if (!mRfidCodes.contains(code) && RfidUtils.isAccord(code) && !errorCodes.toString().contains(code)) {
                                                errorCodes.append(",").append(code);
                                            }
                                        }

                                    }
                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                })
                .compose(this.<Integer>bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer count) throws Exception {
                        if (count < 0) {
                            //rfid连接失败
                            ToastUtils.showLong(R.string.pick_rfid_connection_fail);
                        } else if (count == 50) {
                            scanDialog.dismiss();
                            //含有错签
                            if (errorCodes != null && !StringUtils.isEmpty(errorCodes.toString()) && errorCodes.length() > 0) {
                                showRfidDetail(errorCodes.substring(1), true);
                                return;
                            } else {
                                //正常领料
                                openDoor();
                            }
                        }
                    }
                });


    }

    /**
     * 开门
     */
    private void openDoor() {
        mMainRepository.remoteCtrlIO(mStoreHouse.getId(), null, null, "door", true)
                .subscribe(new BaseObserver<String>(this) {
                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull String result) {
                        if (!StringUtils.isEmpty(result)) {
                            if (result.equals("success")) {
                                finish();
                            } else {
                                ToastUtils.showLong(result);
                            }
                        }
                    }
                });
    }

    /**
     * 查找错签的具体设备信息
     *
     * @param rfids
     */
    private void showRfidDetail(String rfids, boolean isShowProgress) {
        mMainRepository.showRfidDetail(rfids)
                .subscribe(new ResponseObserver<List<Rfid>>(mContext, true, isShowProgress) {
                    @Override
                    public void handleData(@NotNull List<Rfid> data) {
                        //错签加待领的rfid签
                        showScanResultDialog(data);
                    }

                    @Override
                    protected MaterialDialog setDialog() {
                        return new MaterialDialog.Builder(mContext)
                                .content(R.string.pick_detail_rfid_match)
                                .progress(true, 0)
                                .progressIndeterminateStyle(false)
                                .build();
                    }

                });

    }

    /**
     * 显示扫描错签结果信息对话框
     */
    private void showScanResultDialog(List<Rfid> data) {
        MediaUtils.getInstance().stop();
        MediaUtils.getInstance().play(R.string.take_rfid_error);
        new MaterialDialog.Builder(mContext)
                .title(R.string.pick_list_detail_rfid_list_title)
                .items(data)
                .positiveText(android.R.string.cancel)
                .show();
    }

}
