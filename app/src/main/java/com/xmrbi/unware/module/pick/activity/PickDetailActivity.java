package com.xmrbi.unware.module.pick.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.szzk.ttl_libs.TTL_Factory;
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
import com.xmrbi.unware.event.TTLEvent;
import com.xmrbi.unware.module.pick.adapter.PickDetailAdapter;
import com.xmrbi.unware.utils.ActivityStackUtils;
import com.xmrbi.unware.utils.CameraUtils;
import com.xmrbi.unware.utils.DialogUtils;
import com.xmrbi.unware.utils.MediaUtils;
import com.xmrbi.unware.utils.QcCodeUtils;
import com.xmrbi.unware.utils.RfidUtils;
import com.xmrbi.unware.utils.RxBus;
import com.xmrbi.unware.utils.TTLUtils;

import org.greenrobot.greendao.annotation.NotNull;

import java.io.File;
import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by wzn on 2018/5/18.
 */

public class PickDetailActivity extends BaseActivity {

    public static void lauch(Context context, User user, PickOrder order) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("user", user);
        bundle.putSerializable("order", order);
        ActivityStackUtils.lauch(context, PickDetailActivity.class, bundle);
    }

    @BindView(R.id.btnPickDetailExit)
    Button btnPickDetailExit;
    @BindView(R.id.ivPickDetailQCCode)
    ImageView ivPickDetailQCCode;
    @BindView(R.id.tvPickDetailBsnum)
    TextView tvPickDetailBsnum;
    @BindView(R.id.tvPickDetailTime)
    TextView tvPickDetailTime;
    @BindView(R.id.tvPickDetailPickCount)
    TextView tvPickDetailPickCount;
    @BindView(R.id.listPickDetailDeviceDetail)
    RecyclerView listPickDetailDeviceDetail;
    @BindView(R.id.svPickDetailCamera)
    SurfaceView svPickDetailCamera;

    private User mUser;
    private PickOrder mOrder;
    private MainLocalSource mMainLocalSource;
    private MainRepository mMainRepository;
    private PickRepository mPickRepository;
    private StoreHouse mStoreHouse;
    private List<PickListDetail> mlstPickListDetails;
    private PickDetailAdapter mAdapter;
    /**
     * rfidip
     */
    private String mRfidIp;
    /**
     * rfid端口
     */
    private Integer mRfidPort;
    /**
     * 回执打印机串口号
     */
    private String mPrintSerialName;
    /**
     * 回执打印机波特率
     */
    private String mPrintBautRate;
    /**
     * rfid扫描器
     */
    private RfidClient mRfidClient;
    /**
     * 当前领料单的所有rfid码
     */
    private String mlstRfids;
    /**
     * 当前还需查找的Rfid码
     */
    private List<Rfid> mlstNeedRfids;
    /**
     * 错误的rfid列表（每一轮扫描都是新的）
     */
    private StringBuffer mErrorCodes;
    /**
     * 全部扫描过的rfid错签
     */
    private StringBuffer mAllErrorCodes;
    /**
     * 显示待领设备所有的rfid的列表对话框或是扫描结果的rfid列表对话框
     */
    private MaterialDialog mRfidListDialog;
    private int mTotal;
    private int mScanTotal;
    /**
     * 领料回执单打印工具类
     */
    private TTLUtils mTtlUtils;
    /**
     * 领料开始时间
     */
    private Long mPickStartTime;
    /**
     * 该识别单上的所有没取完rfid的货架id
     */
    private String mDrawerIds;
    /**
     * 扫描线程
     */
    private Disposable mScanDisposable;
    /**
     * 获取领料单明细扫描得到的rfid
     */
    private Disposable mRfidDisposable;
    private String mLightDrawer;
    /**
     * 有rfid标签的设备（明细id集合）
     */
    private String mRfidDetailIds;
    /**
     * 每个rfid对应的数量
     */
    private String mRfidDetailCounts;
    /**
     * 没有rfid标签的设备（明细id集合）
     */
    private String mDetailIds;
    /**
     * 没有rfid标签的设备对应的数量
     */
    private String mDetailCounts;
    private CameraUtils mCameraUtils;


    @Override
    protected int getLayout() {
        return R.layout.pick_activity_pick_detail;
    }

    @Override
    protected void onViewCreated() {
        mOrder = (PickOrder) mBundle.getSerializable("order");
        //生成二维码图片
        ivPickDetailQCCode.setImageBitmap(QcCodeUtils.encodeAsBitmap("'rfid" + mOrder.getId() + "'"));
        tvPickDetailBsnum.setText(mOrder.getBsnum());
        tvPickDetailTime.setText(mOrder.getCreateTime());

        listPickDetailDeviceDetail.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mlstPickListDetails = new ArrayList<>();
        mAdapter = new PickDetailAdapter(mlstPickListDetails);
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                String[] rfidCodes = mlstPickListDetails.get(position).getRfidCode().split(",");
                //查看rfid编码
                mRfidListDialog.setItems(rfidCodes);
                mRfidListDialog.show();
            }
        });
        listPickDetailDeviceDetail.setAdapter(mAdapter);

    }

    @Override
    protected void initEventAndData() {
        mUser = (User) mBundle.getSerializable("user");
        mMainLocalSource = new MainLocalSource();
        mMainRepository = new MainRepository(this);
        mPickRepository = new PickRepository(this);
        mStoreHouse = mMainLocalSource.getStoreHouse();
        mlstRfids = "";
        mRfidDetailIds = "";
        mDetailIds = "";
        mRfidDetailCounts = "";
        mDetailCounts = "";
        mlstNeedRfids = new ArrayList<>();
        mAllErrorCodes = new StringBuffer();
        mPickStartTime = new Date().getTime();
        mRfidListDialog = new MaterialDialog.Builder(mContext)
                .title(R.string.pick_list_detail_rfid_list_title)
                .items(new ArrayList())
                .positiveText(android.R.string.cancel)
                .build();
        //后台拍照
        mCameraUtils = new CameraUtils(svPickDetailCamera);
        //下载领料单明细
        downloadPickListDetail();
        //打印机回调方法
        RxBus.getDefault().toObservable(TTLEvent.class)
                .compose(this.<TTLEvent>bindToLifecycle())
                .subscribe(new Consumer<TTLEvent>() {
                    @Override
                    public void accept(TTLEvent ttlEvent) throws Exception {
                        if (ttlEvent.getType() == TTL_Factory.CONNECTION_STATE) {
                            if (ttlEvent.getStatus() != 1) {
                                ToastUtils.showLong("回执打印机串口打开失败");
                            }
                        } else if (TTL_Factory.CHECKPAGE_RESULT == ttlEvent.getType()) {
                            if (ttlEvent.getStatus() != 1) {
                                ToastUtils.showLong("回执打印机缺纸");
                            }
                        }
                    }
                });
    }

    /**
     * 下载领料单详情
     */
    private void downloadPickListDetail() {
        //先下载仓库的配置（rfid设备）
        mMainRepository.mobileQueryAioConfig(mStoreHouse.getLesseeId(), mStoreHouse.getId())
                .compose(new IOTransformer<Response<List<StoreHouseAioConfig>>>(this))
                .flatMap(new Function<Response<List<StoreHouseAioConfig>>, ObservableSource<Response<List<PickListDetail>>>>() {
                    @Override
                    public ObservableSource<Response<List<PickListDetail>>> apply(@NonNull Response<List<StoreHouseAioConfig>> data) throws Exception {
//                        List<StoreHouseAioConfig> lstConfig= JSON.parseArray(data.getData(),StoreHouseAioConfig.class);
                        StoreHouseAioConfig config = data.getData().get(0);
                        mRfidIp = config.getRfidIp();
                        mRfidPort = config.getRfidPort();
                        mPrintSerialName = config.getPrintSerialName();
                        mPrintBautRate = config.getPrintBautRate();
                        //初始化回执打印机
                        mTtlUtils = new TTLUtils(mPrintSerialName, mPrintBautRate);
                        mTtlUtils.checkPaper();
                        return mPickRepository.downloadPickListDetail(mUser.getId(), mStoreHouse.getId(), mOrder.getId());

                    }
                })
                .subscribe(new ResponseObserver<List<PickListDetail>>(mContext) {
                    @Override
                    public void handleData(@NotNull List<PickListDetail> data) {
                        mDrawerIds = "";
                        for (PickListDetail detail : data) {
                            Rfid rfid = new Rfid(detail.getRfidCode(), detail.getComponentName(), true, detail.getDrawerIds());
                            if (!StringUtils.isEmpty(detail.getRfidCode())) {
                                //有rfid标签
                                mTotal++;
                                mlstNeedRfids.add(rfid);
                                if (StringUtils.isEmpty(mlstRfids)) {
                                    mlstRfids = detail.getRfidCode();
                                } else {
                                    mlstRfids += "," + detail.getRfidCode();
                                }
                                //如果已经扫描到的rfid包含rfid明细的code，那代表这个码已经扫到了
                                if (!StringUtils.isEmpty(detail.getRfid()) && detail.getRfid().contains(detail.getRfidCode())) {
                                    mScanTotal++;
                                }
                                mRfidDetailIds += "," + detail.getId();
                                mRfidDetailCounts += "," + detail.getRequestAmount();
                            } else {
                                //无rfid
                                mDetailIds += "," + detail.getId();
                                mDetailCounts += "," + detail.getRequestAmount();

                            }

                            if (mlstPickListDetails.contains(detail)) {
                                //合并相同的设备id的物品
                                PickListDetail listDetail = mlstPickListDetails.get(mlstPickListDetails.indexOf(detail));
                                //合并rfid
                                if (!StringUtils.isEmpty(listDetail.getRfidCode())) {
                                    listDetail.setRfidCode(listDetail.getRfidCode() + "," + detail.getRfidCode());
                                    listDetail.getLstRfids().add(rfid);
                                }
                                //合并请领数量
                                listDetail.setRequestAmount(listDetail.getRequestAmount() + detail.getRequestAmount());
                                //合并货架
                                if (!StringUtils.isEmpty(detail.getDrawerIds())) {
                                    String[] drawerIds = detail.getDrawerIds().split(",");
                                    for (String drawer : drawerIds) {
                                        if (StringUtils.isEmpty(listDetail.getDrawerIds())) {
                                            listDetail.setDrawerIds(drawer);
                                        } else if (!listDetail.getDrawerIds().contains(drawer)) {
                                            listDetail.setDrawerIds(listDetail.getDrawerIds() + "," + drawer);
                                        }
                                    }
                                }
                            } else {
                                List<Rfid> lstRfids = new ArrayList<>();
                                lstRfids.add(rfid);
                                detail.setLstRfids(lstRfids);
                                mlstPickListDetails.add(detail);
                            }
                            //获取所有的货架id
                            if (!StringUtils.isEmpty(detail.getDrawerIds())) {
                                String[] drawers = detail.getDrawerIds().split(",");
                                for (String drawerId : drawers) {
                                    if (!mDrawerIds.contains(drawerId)) {
                                        if (mDrawerIds.length() > 1) {
                                            mDrawerIds += "," + drawerId;
                                        } else {
                                            mDrawerIds = drawerId;
                                        }
                                    }
                                }
                            }
                        }
                        mAdapter.notifyDataSetChanged();

                        setScanResult();
                        //亮灯
                        lightControl(mDrawerIds, true);
                        //开始rfid扫描
                        startRfidScan();
                        //循环获取
                        intervalQueryPickListDetailCodes();
                    }
                });
    }

    /**
     * 货架的灯控
     */
    private void lightControl(String drawerIds, boolean onOrOff) {
        mMainRepository.remoteCtrlIO(mStoreHouse.getId(), mDrawerIds, drawerIds, "light", onOrOff)
                .subscribe(new BaseObserver<String>(this, false, false) {
                    @Override
                    public void onNext(@NonNull String s) {
                    }
                });
    }

    /**
     * 开始rfid扫描
     */
    private void startRfidScan() {
        Disposable disposable = Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Boolean> e) throws Exception {
                //连接rfid客户端
                try {
                    if (mRfidClient != null) {
                        e.onNext(true);
                    } else {
                        //连接rfidsocket
                        mRfidClient = new RfidClient(mRfidIp, mRfidPort);
                        e.onNext(mRfidClient.connect());
                    }

                } catch (IOException Exception) {
                    Exception.printStackTrace();
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            //5秒扫描一轮
                            mScanDisposable = Observable.interval(0, 5, TimeUnit.SECONDS)
                                    .compose(PickDetailActivity.this.<Long>bindToLifecycle())
                                    .flatMap(new Function<Long, Observable<Boolean>>() {
                                        @Override
                                        public Observable<Boolean> apply(final @NonNull Long index) throws Exception {
                                            mErrorCodes = new StringBuffer();
                                            //读取rfid 10次
                                            for (int i = 0; i < 10; i++) {
                                                try {
                                                    List<String> rfidList = mRfidClient.startRead();
                                                     matchRfids(rfidList);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                            return Observable.just(true);
                                        }
                                    })
                                    .compose(new IOTransformer<Boolean>(PickDetailActivity.this))
                                    .subscribe(new Consumer<Boolean>() {
                                        @Override
                                        public void accept(Boolean aBoolean) throws Exception {
                                            //显示识别结果
                                            setScanResult();
                                            //控制货架的灯亮
                                            controlLightOn();
                                            //更新ui
                                            mAdapter.notifyDataSetChanged();
                                            //判断显示错签
                                            if (mErrorCodes != null && mRfidListDialog != null && !mRfidListDialog.isShowing()) {
                                                //含有错签显示错签的提示框
                                                if (mErrorCodes.length() > 1) {
                                                    showRfidDetail(mErrorCodes.substring(1), false);
                                                }

                                            }
                                        }
                                    });
                        }
                    }
                });


    }

    /**
     * 轮询领料单明细，获取已经扫描到的rfid
     */
    private void intervalQueryPickListDetailCodes() {
        Observable.interval(0, 10, TimeUnit.SECONDS)
                .flatMap(new Function<Long, ObservableSource<Response<List<PickListDetail>>>>() {
                    @Override
                    public ObservableSource<Response<List<PickListDetail>>> apply(Long aLong) throws Exception {
                        return mPickRepository.downloadPickListDetail(mUser.getId(), mStoreHouse.getId(), mOrder.getId());
                    }
                })
                .subscribe(new ResponseObserver<List<PickListDetail>>(mContext, false, false) {
                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                        mRfidDisposable = d;
                    }

                    @Override
                    public void handleData(List<PickListDetail> data) {
                        List<String> lstRfids = new ArrayList<>();
                        for (PickListDetail pick : data) {
                            if (!StringUtils.isEmpty(pick.getRfid())) {
                                String[] rfids = pick.getRfid().split(",");
                                for (String code : rfids) {
                                    if (!lstRfids.contains(code)) {
                                        lstRfids.add(code);
                                    }

                                }
                            }
                        }
                        matchRfids(lstRfids);
                    }
                });
    }

    /**
     * 匹配搜索得到的rfid码
     *
     * @param rfidList
     */
    private void matchRfids(List<String> rfidList) {
        if (rfidList != null && !rfidList.isEmpty()) {
            for (String code : rfidList) {
                //判断是否符合标准
                if (RfidUtils.isAccord(code)) {
                    if (mlstRfids.contains(code)) {
                        //判断是否已经插入到rfid中
                        for (PickListDetail detail : mlstPickListDetails) {
                            //输入rfid码中是否之前已经扫描到，如果没有加入到已经扫描到列表中
                            if (!StringUtils.isEmpty(detail.getRfidCode()) && detail.getRfidCode().contains(code)) {
                                if (StringUtils.isEmpty(detail.getRfid())) {
                                    mScanTotal++;
                                    detail.setRfid(code);
                                } else if (!detail.getRfid().contains(code)) {
                                    mScanTotal++;
                                    detail.setRfid(detail.getRfid() + "," + code);
                                }
                            }
                            //删除需要扫描的rfid列表中的rfid
                            mlstNeedRfids.remove(new Rfid(code));
                        }
                    } else {
                        if (!mErrorCodes.toString().contains(code)) {
                            //加入到错签列表中
                            mErrorCodes.append(",").append(code);
                        }
                        if (!mAllErrorCodes.toString().contains(code)) {
                            //加入到全部错签列表中
                            mAllErrorCodes.append(",").append(code);
                        }
                    }
                }
            }
        }
    }

    /**
     * 查找错签的具体设备信息
     *
     * @param rfids
     */
    private void showRfidDetail(final String rfids, boolean isShowProgress) {
        mMainRepository.showRfidDetail(rfids)
                .subscribe(new ResponseObserver<List<Rfid>>(mContext, false, isShowProgress) {
                    @Override
                    public void handleData(@NotNull List<Rfid> data) {
                        //错签的rfid签
                        showScanResultDialog(data);
                    }

                    @Override
                    protected void handleErrorData() {
                        super.handleErrorData();
                        String[] lstCodes=rfids.split(",");
                        List<Rfid> lstRfids=new ArrayList<>();
                        for (String code:lstCodes) {
                            if(!StringUtils.isEmpty(code)){
                                Rfid rfid = new Rfid(code,"", false, "");
                                lstRfids.add(rfid);
                            }
                        }
                        showScanResultDialog(lstRfids);
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
     * 控制货架的灯亮（会灭掉没有亮的灯，接口处理）
     */
    private void controlLightOn() {
        List<String> drawerIds = new ArrayList<>();
        for (PickListDetail detail : mlstPickListDetails) {
            //物品还没领完
            if (!detail.getRelation() && !StringUtils.isEmpty(detail.getDrawerIds())) {
                //仍然需要亮灯的货架
                String[] lstDrawerIds = detail.getDrawerIds().split(",");
                for (String drawerId : lstDrawerIds) {
                    if (!drawerIds.contains(drawerId)) {
                        drawerIds.add(drawerId);
                    }
                }
            }
        }
        //当前正在亮灯的货架
        String lightDrawer = "";
        for (String drawerId : drawerIds) {
            if (!lightDrawer.contains(drawerId)) {
                lightDrawer += "," + drawerId;
            }
        }

        if (lightDrawer.length() > 0) {
            if (StringUtils.isEmpty(mLightDrawer)) {
                mLightDrawer = lightDrawer;
            } else if (mLightDrawer.equals(lightDrawer)) {
                //如果亮灯情况和上次亮灯情况一样，则不做操作
                return;
            }
            lightControl(lightDrawer.substring(1), true);
        } else {
            lightControl(mDrawerIds, false);
        }
    }

    /**
     * 显示扫描错签结果信息对话框
     */
    private void showScanResultDialog(List<Rfid> data) {
        Collections.sort(data, new Comparator<Rfid>() {
            @Override
            public int compare(Rfid o1, Rfid o2) {
                return o1.getCode().compareTo(o2.getCode());
            }
        });
        MediaUtils.getInstance().stop();
        MediaUtils.getInstance().play(R.string.take_rfid_error);
        mRfidListDialog.getBuilder().items(data);
        mRfidListDialog.notifyItemsChanged();
        mRfidListDialog.show();
    }


    /**
     * 识别情况
     */
    private void setScanResult() {
        tvPickDetailPickCount.setText(mScanTotal + "/" + mTotal);
    }

    @Override
    protected void onDestroy() {
        if (mScanDisposable != null && !mScanDisposable.isDisposed()) {
            mScanDisposable.dispose();
        }
        //熄灭所有的灯
        lightControl(mDrawerIds, false);
        try {
            if (mRfidClient != null) {
                // 结束读卡
                if (mRfidClient.getStatus() != 0) {
                    // 结束读取rfid标签
                    mRfidClient.disconnect();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();

    }


    /**
     * 结束领料
     */
    @OnClick(R.id.btnPickDetailExit)
    public void finishPick() {
        //取消轮询搜索rifd
        if (mScanDisposable != null && !mScanDisposable.isDisposed()) {
            mScanDisposable.dispose();
        }
        mErrorCodes = new StringBuffer();
        //识别中
        final MaterialDialog scanDialog = new MaterialDialog.Builder(mContext)
                .content(R.string.pick_list_detail_scan)
                .progress(true, 0)
                .progressIndeterminateStyle(false)
                .canceledOnTouchOutside(false)
                .show();
        //扫描10次获取结果(延迟5s执行)
        Disposable d = Observable.timer(5, TimeUnit.SECONDS)
                .flatMap(new Function<Long, ObservableSource<Integer>>() {
                    @Override
                    public ObservableSource<Integer> apply(Long aLong) {
                        return Observable.range(1, 10);
                    }
                })
                .doOnNext(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) {
                        List<String> rfidList = null;
                        try {
                            if (mRfidClient != null) {
                                rfidList = mRfidClient.startRead();
                                if (rfidList != null && !rfidList.isEmpty()) {
                                    for (String code : rfidList) {
                                        if (!mlstRfids.contains(code) && RfidUtils.isAccord(code) && !mErrorCodes.toString().contains(code)) {
                                            mErrorCodes.append(",").append(code);
                                        }
                                        if (!mlstRfids.contains(code) && RfidUtils.isAccord(code) && !mAllErrorCodes.toString().contains(code)) {
                                            mAllErrorCodes.append(",").append(code);
                                        }
                                    }

                                }
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                })
                .compose(new IOTransformer<Integer>(this))
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer count) throws Exception {
                        //循环10次，显示最终的识别结果
                        if (count == 10) {
                            scanDialog.dismiss();
                            MaterialDialog dialog = null;
                            //含有错签
                            if (mErrorCodes != null && !StringUtils.isEmpty(mErrorCodes.toString()) && mErrorCodes.length() > 0) {
                                showRfidDetail(mErrorCodes.substring(1), true);
                                //重新开始搜索
                                startRfidScan();
                                return;
                            } else if (!mlstNeedRfids.isEmpty()) {
                                //还有待领的物品
                                dialog = DialogUtils.alert(mContext,
                                        getString(R.string.pick_detail_finish_title),
                                        getString(R.string.pick_detail_no_all), getString(R.string.pick_detail_no_all_btn_positive_text),
                                        getString(R.string.pick_detail_no_all_btn_negative_text),
                                        new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@android.support.annotation.NonNull MaterialDialog dialog, @android.support.annotation.NonNull DialogAction which) {
                                                uploadPickDetailData();
                                            }
                                        }, new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@android.support.annotation.NonNull MaterialDialog dialog, @android.support.annotation.NonNull DialogAction which) {
                                                startRfidScan();
                                            }
                                        });

                            } else {
                                //正常领料
                                dialog = DialogUtils.alert(mContext,
                                        getString(R.string.pick_detail_finish_title),
                                        getString(R.string.pick_detail_finish),
                                        new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@android.support.annotation.NonNull MaterialDialog dialog, @android.support.annotation.NonNull DialogAction which) {
                                                uploadPickDetailData();
                                            }
                                        });
                            }
//                            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//                                @Override
//                                public void onDismiss(DialogInterface dialog) {
//                                    //重新开始搜索
//                                    startRfidScan();
//                                }
//                            });
                            dialog.setCanceledOnTouchOutside(false);
                            dialog.show();
                        }
                    }
                });


    }

    /**
     * 上传领料单数据
     */
    private void uploadPickDetailData() {
        final StringBuffer startAnnexId = new StringBuffer();
        final StringBuffer endAnnexId = new StringBuffer();
        //拍摄结束领料的图片
        mCameraUtils.takePicture(Config.FileConfig.PICK_PHOTO_DIR + mOrder.getBsnum() + "-end.jpg")
                .flatMap(new Function<Boolean, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(Boolean success) throws Exception {
                        //上传开始领料图片
                        return mMainRepository.imgUploadAnnex(new File(Config.FileConfig.PICK_PHOTO_DIR + mOrder.getBsnum() + "-start.jpg"), mUser.getId());
                    }
                })
                .flatMap(new Function<String, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(String uploadResult) throws Exception {
                        if (uploadResult.indexOf("id=") == 0) {
                            startAnnexId.append(uploadResult.substring(3, uploadResult.length() - 1));
                        }
                        try{
                            File file =new File(Config.FileConfig.PICK_PHOTO_DIR + mOrder.getBsnum() + "-start.jpg");
                            if(file.exists()){
                                file.delete();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        //上传结束领料图片
                        return mMainRepository.imgUploadAnnex(new File(Config.FileConfig.PICK_PHOTO_DIR + mOrder.getBsnum() + "-end.jpg"), mUser.getId());
                    }
                })
                .flatMap(new Function<String, ObservableSource<Response<String>>>() {
                    @Override
                    public ObservableSource<Response<String>> apply(String uploadResult) throws Exception {
                        if (uploadResult.indexOf("id=") == 0) {
                            endAnnexId.append(uploadResult.substring(3, uploadResult.length() - 1));
                        }
                        try{
                            File file =new File(Config.FileConfig.PICK_PHOTO_DIR + mOrder.getBsnum() + "-end.jpg");
                            if(file.exists()){
                                file.delete();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        //上传数据
                        Map<String, String> params = new HashMap<>();
                        params.put("pickListId", Integer.toString(mOrder.getId()));//领料单id
                        if (mlstRfids.length() > 0) {
                            params.put("rfidCodes", mlstRfids.substring(1));//rfid的编码
                        }
                        if (mRfidDetailCounts.length() > 0) {
                            params.put("rfidCounts", mRfidDetailCounts.substring(1));//rfid对应的数量
                        }
                        if (mRfidDetailIds.length() > 0) {
                            params.put("rfidDetailIds", mRfidDetailIds.substring(1));//rfid对应的领料明细id
                        }
                        if (mDetailIds.length() > 0) {
                            params.put("detailIds", mDetailIds.substring(1));//领料明细id
                        }
                        if (mDetailCounts.length() > 0) {
                            params.put("detailCounts", mDetailCounts.substring(1));//设备对应的数量
                        }
                        params.put("startTimestamp", Long.toString(mPickStartTime));//领料开始时间
                        params.put("overAnnexIds", endAnnexId.toString());//结束领料的图片
                        params.put("annexIds", startAnnexId.toString());//开始领料的图片
                        params.put("userId", mUser.getId().toString());
                        params.put("channel", SPUtils.getInstance(Config.SP.SP_NAME).getString(Config.SP.SP_CHANNEL));//摄像头通道号
                        params.put("mistakeCodes", mAllErrorCodes.toString());//错签
                        if (mlstNeedRfids.size() > 0) {
                            StringBuffer lessCodes = new StringBuffer();
                            for (Rfid rfid : mlstNeedRfids) {
                                lessCodes.append(",").append(rfid.getCode());
                            }
                            params.put("lessCodes", lessCodes.substring(1));//少签
                        }
                        return mPickRepository.endPick(params);
                    }
                })
                .compose(new IOTransformer<Response<String>>(this))
                .subscribe(new ResponseObserver<String>(this, true, true) {
                    @Override
                    public void handleData(String data) {
                        //熄灭所有的灯
                        lightControl(mDrawerIds, false);
                        //打印回执单
                        mTtlUtils.printPickNote(mStoreHouse.getName(), mUser, mOrder, mlstPickListDetails);
                        //保存领取的rfid列表
                        if (mlstRfids.length() > 0) {
                            SPUtils.getInstance(Config.SP.SP_NAME).put(Config.SP.SP_RFID_CODES,
                                    SPUtils.getInstance(Config.SP.SP_NAME).getString(Config.SP.SP_RFID_CODES) + "," + mlstRfids.substring(1));
                        }
                        RxBus.getDefault().post(new PickEvent());
                        finish();
                    }

                    @Override
                    protected MaterialDialog setDialog() {
                        return new MaterialDialog.Builder(mContext)
                                .content(R.string.pick_detail_upload_data)
                                .progress(true, 0)
                                .progressIndeterminateStyle(false)
                                .build();
                    }

                    @Override
                    protected void handleErrorData() {
                        super.handleErrorData();
                        //重新开始扫描
                        startRfidScan();
                    }
                });


    }
}
