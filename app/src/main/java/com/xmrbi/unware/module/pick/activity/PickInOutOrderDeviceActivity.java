package com.xmrbi.unware.module.pick.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.xmrbi.unware.R;
import com.xmrbi.unware.base.BaseActivity;
import com.xmrbi.unware.component.http.Response;
import com.xmrbi.unware.component.http.ResponseObserver;
import com.xmrbi.unware.data.entity.deliver.Device;
import com.xmrbi.unware.data.entity.deliver.InOutOrderList;
import com.xmrbi.unware.data.entity.main.StoreHouse;
import com.xmrbi.unware.data.entity.main.StoreHouseAioConfig;
import com.xmrbi.unware.data.entity.main.User;
import com.xmrbi.unware.data.local.MainLocalSource;
import com.xmrbi.unware.data.repository.DeliverRepository;
import com.xmrbi.unware.data.repository.MainRepository;
import com.xmrbi.unware.data.repository.PickRepository;
import com.xmrbi.unware.module.pick.adapter.InOutOrderDeviceAdatper;
import com.xmrbi.unware.utils.ActivityStackUtils;
import com.xmrbi.unware.utils.DialogUtils;
import com.xmrbi.unware.utils.RTUUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * Created by wzn on 2018/8/7.
 */
public class PickInOutOrderDeviceActivity extends BaseActivity {

    public static void lauch(Context context, String code, User user, long inOutOrderId, String time) {
        Bundle bundle = new Bundle();
        bundle.putString("code", code);
        bundle.putString("time", time);
        bundle.putLong("inOutOrderId", inOutOrderId);
        bundle.putSerializable("user", user);
        ActivityStackUtils.lauch(context, PickInOutOrderDeviceActivity.class, bundle);
    }


    @BindView(R.id.ivPickBack)
    ImageView ivPickBack;
    @BindView(R.id.btnPickEndPlace)
    Button btnPickEndPlace;
    @BindView(R.id.tvPickCode)
    TextView tvPickCode;
    @BindView(R.id.tvPickPlaceSituation)
    TextView tvPickPlaceSituation;
    @BindView(R.id.listPick)
    RecyclerView listPick;
    @BindView(R.id.etPickScan)
    EditText etPickScan;
    @BindView(R.id.tvPickTime)
    TextView tvPickTime;


    private String mCode;
    private String mTime;
    private long mInOutOrderId;
    private User mUser;
    private List<InOutOrderList> mLstIools;
    private InOutOrderDeviceAdatper mAdapter;
    private DeliverRepository mDeliverRepository;
    private MainRepository mMainRepository;
    private MainLocalSource mMainLocalSource;
    private PickRepository mPickRepository;
    private StoreHouse mStoreHouse;
    private RTUUtils mRtuUtils;


    @Override
    protected int getLayout() {
        return R.layout.pick_activity_in_out_order_pick;
    }

    @Override
    protected void onViewCreated() {
        mCode = mBundle.getString("code");
        mTime = mBundle.getString("time");
        mInOutOrderId = mBundle.getLong("inOutOrderId");
        mUser = (User) mBundle.getSerializable("user");
        mLstIools = new ArrayList<>();
        mCode = mBundle.getString("code");
        tvPickCode.setText(mCode);
        tvPickTime.setText(mTime);
        etPickScan.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    String text = etPickScan.getText().toString().trim();
                    LogUtils.d("scan:" + text);
                    for (InOutOrderList iool : mLstIools) {
                        if (iool.getSequenceCode().equals(text)) {
                            iool.setScan(true);
                            mAdapter.notifyDataSetChanged();
                            etPickScan.setText("");
                            setPickSituation();
                            controlLightOn(true, null);
                            return false;
                        }
                    }
                    //扫描错误的码
                    if (!StringUtils.isEmpty(text)) {
                        showErrorResult(text);
                        etPickScan.setText("");
                    }

                }
                return false;
            }
        });
        mAdapter = new InOutOrderDeviceAdatper(mLstIools);
        initRecycleView(listPick, mAdapter, LinearLayoutManager.VERTICAL);
        etPickScan.requestFocus();
        etPickScan.setFocusable(true);
    }

    @Override
    protected void initEventAndData() {
        mDeliverRepository = new DeliverRepository(this);
        mMainRepository = new MainRepository(this);
        mMainLocalSource = new MainLocalSource();
        mStoreHouse = mMainLocalSource.getStoreHouse();
        mPickRepository = new PickRepository(this);
        mRtuUtils = new RTUUtils();
        queryInOutOrderDetail();
    }

    @Override
    protected void onDestroy() {
        controlLightOn(false, null);
        super.onDestroy();
    }

    /**
     * 显示扫描到的错误的货物
     */
    private void showErrorResult(final String code) {
        mPickRepository.queryDeviceBySequenceCode(code, mStoreHouse.getLesseeId())
                .subscribe(new ResponseObserver<Device>(this) {
                    @Override
                    public void handleData(Device data) {
                        String name = data.getComponentName() == null ? "" : data.getComponentName();
                        DialogUtils.alert(mContext, "扫描错误：" + code + "-" + name).show();
                    }
                });
    }

    /**
     * 控制灯光亮起
     */
    private void controlLightOn(boolean onOrOff, String ope) {
        List<String> drawerIds = new ArrayList<>();
        for (InOutOrderList iool : mLstIools) {
            if (!iool.isScan() || (!StringUtils.isEmpty(ope) && ope.equals("all"))) {
                if (!StringUtils.isEmpty(iool.getDrawerIds())) {
                    String[] ids = iool.getDrawerIds().split(",");
                    for (String drawerId : ids) {
                        if (!drawerIds.contains(drawerId)) {
                            drawerIds.add(drawerId);
                        }
                    }
                }
            }
        }
        mMainRepository.controlLightByRTU(mRtuUtils, mStoreHouse.getId(), drawerIds, onOrOff)
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        LogUtils.d("light:" + s);
                    }
                });
    }

    /**
     * 获取入库单明细
     */
    private void queryInOutOrderDetail() {
        //获取灯光配置器
        mMainRepository.queryStoreHouseConfig(mStoreHouse.getId())
                .flatMap(new Function<Response<List<StoreHouseAioConfig>>, ObservableSource<Response<List<InOutOrderList>>>>() {
                    @Override
                    public ObservableSource<Response<List<InOutOrderList>>> apply(Response<List<StoreHouseAioConfig>> data) throws Exception {
                        if (data.isSuccess() && data.getData() != null && !data.getData().isEmpty()) {
                            StoreHouseAioConfig config = data.getData().get(0);
                            mRtuUtils.init(config.getLightSerialName(), config.getLightBautRate());
                        }
                        //获取入单库明细
                        return mDeliverRepository.queryInOutOrderDetail(mInOutOrderId);
                    }
                })
                .subscribe(new ResponseObserver<List<InOutOrderList>>(this) {
                    @Override
                    public void handleData(List<InOutOrderList> data) {
                        mLstIools.clear();
                        mLstIools.addAll(data);
                        mAdapter.notifyDataSetChanged();
                        controlLightOn(true, "all");
                        setPickSituation();
                    }
                });
    }

    /**
     * 出库情况
     */
    private void setPickSituation() {
        int pickCount = 0;
        for (InOutOrderList iool : mLstIools) {
            if (iool.isScan()) {
                pickCount++;
            }
        }
        tvPickPlaceSituation.setText(pickCount + "/" + mLstIools.size());
    }


    @OnClick({R.id.ivPickBack, R.id.btnPickEndPlace})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivPickBack:
                onBackPressed();
                break;
            case R.id.btnPickEndPlace:
                endPick();
                break;
        }
    }

    /**
     * 结束出库
     */
    private void endPick() {
        final StringBuffer inOutOrderListIds = new StringBuffer();
        int count=0;
        for (InOutOrderList iool : mLstIools) {
            if (iool.isScan()) {
                count++;
                inOutOrderListIds.append(",").append(iool.getId());
            }
        }
        if (count == 0) {
            DialogUtils.alert(mContext, "请扫描设备上的条形码进行出库").show();
        } else if (count!= mLstIools.size()) {
            new MaterialDialog.Builder(mContext)
                    .title("提示")
                    .content("还未全部扫描，是否出账？")
                    .positiveText("确定")
                    .negativeText("取消")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            endPick(inOutOrderListIds.substring(1));
                        }
                    })
                    .show();
        } else {
            new MaterialDialog.Builder(mContext)
                    .title("提示")
                    .content("是否出账？")
                    .positiveText("确定")
                    .negativeText("取消")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            endPick(inOutOrderListIds.substring(1));
                        }
                    })
                    .show();
        }
    }


    /**
     * 结束出库，进行网络操作
     */
    private void endPick(String inOutOrderListIds) {

        mPickRepository.endPick(mInOutOrderId, mUser.getId(), inOutOrderListIds)
                .subscribe(new ResponseObserver<String>(this) {
                    @Override
                    public void handleData(String data) {
                        ToastUtils.showLong("出账成功");
                        finish();
                    }

                    @Override
                    protected MaterialDialog setDialog() {
                        return new MaterialDialog.Builder(mContext)
                                .title("出账中...")
                                .content(R.string.main_progress_content)
                                .progress(true, 0)
                                .progressIndeterminateStyle(false)
                                .show();
                    }
                });

    }
}
