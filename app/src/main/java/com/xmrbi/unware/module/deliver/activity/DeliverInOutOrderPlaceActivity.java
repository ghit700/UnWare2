package com.xmrbi.unware.module.deliver.activity;

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
import com.xmrbi.unware.data.remote.PickRemoteSource;
import com.xmrbi.unware.data.repository.DeliverRepository;
import com.xmrbi.unware.data.repository.MainRepository;
import com.xmrbi.unware.data.repository.PickRepository;
import com.xmrbi.unware.module.deliver.adapter.InOutOrderPlaceAdapter;
import com.xmrbi.unware.utils.ActivityStackUtils;
import com.xmrbi.unware.utils.DialogUtils;
import com.xmrbi.unware.utils.RTUUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * Created by wzn on 2018/8/7.
 */
public class DeliverInOutOrderPlaceActivity extends BaseActivity {
    public static void lauch(Context context, String code, User user, long inOutOrderId) {
        Bundle bundle = new Bundle();
        bundle.putString("code", code);
        bundle.putLong("inOutOrderId", inOutOrderId);
        bundle.putSerializable("user", user);
        ActivityStackUtils.lauch(context, DeliverInOutOrderPlaceActivity.class, bundle);
    }


    @BindView(R.id.btnDeliverEndPlace)
    Button btnDeliverEndPlace;
    @BindView(R.id.ivDeliverBack)
    ImageView ivDeliverBack;
    @BindView(R.id.tvDeliverCode)
    TextView tvDeliverCode;
    @BindView(R.id.tvDeliverPlaceSituation)
    TextView tvDeliverPlaceSituation;
    @BindView(R.id.etDeliverScan)
    EditText etDeliverScan;
    @BindView(R.id.listDeliver)
    RecyclerView listDeliver;


    private String mCode;
    private User mUser;
    private DeliverRepository mDeliverRepository;
    private PickRepository mPickRepository;
    private List<InOutOrderList> mLstIools;
    private InOutOrderPlaceAdapter mAdapter;
    private MainRepository mMainRepository;
    private MainLocalSource mMainLocalSource;
    private StoreHouse mStoreHouse;
    private long mInOutOrderId;

    @Override
    protected int getLayout() {
        return R.layout.deliver_activity_in_out_order_place;
    }

    @Override
    protected void onViewCreated() {
        mLstIools = new ArrayList<>();
        mCode = mBundle.getString("code");
        tvDeliverCode.setText(mCode);
        etDeliverScan.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    String text = etDeliverScan.getText().toString().trim();
                    LogUtils.d("scan:" + text);
                    for (InOutOrderList iool : mLstIools) {
                        if (iool.getSequenceCode().equals(text)) {
                            iool.setScan(true);
                            mAdapter.notifyDataSetChanged();
                            setPlaceSituation();
                            showDrawerSelect(iool.getDrawerIds(), true);
                            etDeliverScan.setText("");
                            return false;
                        }
                    }
                    if (!StringUtils.isEmpty(text)) {
                        showErrorResult(text);
                        etDeliverScan.setText("");
                    }
                }
                return false;
            }
        });
        mAdapter = new InOutOrderPlaceAdapter(mLstIools);
        initRecycleView(listDeliver, mAdapter, LinearLayoutManager.VERTICAL);
        etDeliverScan.requestFocus();
        etDeliverScan.setFocusable(true);
    }

    @Override
    protected void initEventAndData() {
        mUser = (User) mBundle.getSerializable("user");
        mDeliverRepository = new DeliverRepository(this);
        mMainRepository = new MainRepository(this);
        mPickRepository = new PickRepository(this);
        mMainLocalSource = new MainLocalSource();
        mStoreHouse = mMainLocalSource.getStoreHouse();
        mInOutOrderId = mBundle.getLong("inOutOrderId");
        queryInOutOrderDetail();
    }

    @Override
    protected void onDestroy() {
        showDrawerSelect(null, false);
        super.onDestroy();
    }

    /**
     * 显示扫描到的错误的货物
     *
     * @param code
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
     * 灭掉仓库所有的灯，亮起货架的灯
     */
    private void showDrawerSelect(String drawerIds, boolean onOrOff) {
        List<String> lstDrawers = null;
        if (!StringUtils.isEmpty(drawerIds)) {
            lstDrawers = Arrays.asList(drawerIds.split(","));
        }
        mMainRepository.controlLightByEio(mStoreHouse.getId(), lstDrawers, onOrOff)
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        LogUtils.d("light result:" + s);
                    }
                });
    }

    /**
     * 获取入库单明细
     */
    private void queryInOutOrderDetail() {
        mDeliverRepository.queryInOutOrderDetail(mInOutOrderId)
                .subscribe(new ResponseObserver<List<InOutOrderList>>(this) {
                    @Override
                    public void handleData(List<InOutOrderList> data) {
                        mLstIools.clear();
                        mLstIools.addAll(data);
                        mAdapter.notifyDataSetChanged();
                        setPlaceSituation();
                    }
                });
    }

    private void setPlaceSituation() {
        int placeCount = 0;
        for (InOutOrderList iool : mLstIools) {
            if (iool.isScan()) {
                placeCount++;
            }
        }
        tvDeliverPlaceSituation.setText(placeCount + "/" + mLstIools.size());
    }

    @OnClick(R.id.ivDeliverBack)
    public void deliverBack() {
        onBackPressed();
    }


    /**
     * 结束入库
     */
    @OnClick(R.id.btnDeliverEndPlace)
    public void endPlace() {
        int placeCount = 0;
        for (InOutOrderList iool : mLstIools) {
            if (iool.isScan()) {
                placeCount++;
            }
        }
        if (placeCount == 0) {
            DialogUtils.alert(mContext, "请扫描设备上的条形码进行上架和入库").show();
        } else if (placeCount != mLstIools.size()) {
            new MaterialDialog.Builder(mContext)
                    .title("提示")
                    .content("还未全部上架，是否入账？")
                    .positiveText("确定")
                    .negativeText("取消")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            endDeliver();
                        }
                    })
                    .show();
        } else {
            new MaterialDialog.Builder(mContext)
                    .title("提示")
                    .content("是否入账？")
                    .positiveText("确定")
                    .negativeText("取消")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            endDeliver();
                        }
                    })
                    .show();
        }
    }

    public void endDeliver() {
        StringBuffer inOutOrderListIds = new StringBuffer();
        for (InOutOrderList iool : mLstIools) {
            if (iool.isScan()) {
                inOutOrderListIds.append(",").append(iool.getId());
            }
        }
        mDeliverRepository.endDeliver(mInOutOrderId, mUser.getId(), inOutOrderListIds.substring(1))
                .subscribe(new ResponseObserver<String>(this) {
                    @Override
                    public void handleData(String data) {
                        ToastUtils.showLong("入账成功");
                        finish();
                    }

                    @Override
                    protected MaterialDialog setDialog() {
                        return new MaterialDialog.Builder(mContext)
                                .title("入账中...")
                                .content(R.string.main_progress_content)
                                .progress(true, 0)
                                .progressIndeterminateStyle(false)
                                .show();
                    }
                });

    }
}
