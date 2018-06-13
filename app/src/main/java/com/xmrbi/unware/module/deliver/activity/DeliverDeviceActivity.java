package com.xmrbi.unware.module.deliver.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xmrbi.unware.R;
import com.xmrbi.unware.base.BaseActivity;
import com.xmrbi.unware.component.http.BaseObserver;
import com.xmrbi.unware.component.http.ExceptionHandle;
import com.xmrbi.unware.component.http.IOTransformer;
import com.xmrbi.unware.component.http.Response;
import com.xmrbi.unware.component.http.ResponseObserver;
import com.xmrbi.unware.data.entity.deliver.Device;
import com.xmrbi.unware.data.entity.deliver.Drawer;
import com.xmrbi.unware.data.entity.main.StoreHouse;
import com.xmrbi.unware.data.entity.main.StoreHouseAioConfig;
import com.xmrbi.unware.data.local.MainLocalSource;
import com.xmrbi.unware.data.repository.DeliverRepository;
import com.xmrbi.unware.data.repository.MainRepository;
import com.xmrbi.unware.module.deliver.adapter.DeviceAdapter;
import com.xmrbi.unware.utils.ActivityStackUtils;
import com.xmrbi.unware.utils.QcCodeUtils;
import com.xmrbi.unware.utils.TTLUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

/**
 * Created by wzn on 2018/6/12.
 */
public class DeliverDeviceActivity extends BaseActivity {
    @BindView(R.id.ivDeliverDeviceQCCode)
    ImageView ivDeliverDeviceQCCode;
    @BindView(R.id.listDeliverDevicel)
    RecyclerView listDeliverDevicel;
    @BindView(R.id.btnDeliverDevicelExit)
    Button btnDeliverDevicelExit;
    @BindView(R.id.tvDeliverDeviceAssetCode)
    TextView tvDeliverDeviceAssetCode;
    @BindView(R.id.tvDeliverDevicePlaceCount)
    TextView tvDeliverDevicePlaceCount;
    /**
     * 货架ids
     */
    private String mDrawerIds;

    public static void lauch(Context context, String assetCode) {
        Bundle bundle = new Bundle();
        bundle.putString("assetCode", assetCode);
        ActivityStackUtils.lauch(context, DeliverDeviceActivity.class, bundle);
    }

    private String mAssetCode;
    private DeliverRepository mDeliverRepository;
    private MainLocalSource mMainLocalSource;
    private StoreHouse mStoreHouse;
    private List<Device> mLstDevices;
    private DeviceAdapter mAdapter;
    private MaterialDialog mChooseDrawerDialog;
    private MainRepository mMainRepository;
    /**
     * 操作过的设备
     */
    private List<Device> mlstOperateDevices;

    @Override
    protected int getLayout() {
        return R.layout.deliver_activity_device_list;
    }

    @Override
    protected void onViewCreated() {
        mAssetCode = mBundle.getString("assetCode");
        ivDeliverDeviceQCCode.setImageBitmap(QcCodeUtils.encodeAsBitmap("'jh" + mAssetCode + "'"));
        mLstDevices = new ArrayList<>();
        mAdapter = new DeviceAdapter(mLstDevices);
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                Device device = mLstDevices.get(position);
                queryDrawerNeedClass(device.getId());

            }
        });
        initRecycleView(listDeliverDevicel, mAdapter, LinearLayoutManager.VERTICAL);
    }

    @Override
    protected void initEventAndData() {
        mDeliverRepository = new DeliverRepository(this);
        mMainLocalSource = new MainLocalSource();
        mStoreHouse = mMainLocalSource.getStoreHouse();
        mMainRepository = new MainRepository(this);
        mlstOperateDevices = new ArrayList<>();
        intervalDownloadDeliverGoods();
    }

    /**
     * 下载该资产编号包含的设备(10s刷新)
     */
    private void intervalDownloadDeliverGoods() {
        Observable.interval(0, 10, TimeUnit.SECONDS)
                .flatMap(new Function<Long, ObservableSource<Response<List<Device>>>>() {
                    @Override
                    public ObservableSource<Response<List<Device>>> apply(Long aLong) throws Exception {
                        return mDeliverRepository.downloadDeliverGoods(mAssetCode, mStoreHouse.getId());
                    }
                })
                .subscribe(new ResponseObserver<List<Device>>() {
                    @Override
                    public void handleData(List<Device> data) {
                        mLstDevices.clear();
                        mLstDevices.addAll(data);
                        setPlaceCount();
                        mAdapter.notifyDataSetChanged();
                    }
                });
    }

    /**
     * 设置上架数量
     */
    private void setPlaceCount() {
        StringBuffer assetCode = new StringBuffer();
        int count = mLstDevices.size();
        int placeCount = 0;
        for (Device d : mLstDevices) {
            if (!assetCode.toString().contains(d.getAssetCode())) {
                assetCode.append(",").append(d.getAssetCode());
            }
            if (!StringUtils.isEmpty(d.getDrawerNames())) {
                placeCount++;
            }
        }
        tvDeliverDevicePlaceCount.setText(placeCount + "/" + count);
        if (StringUtils.isEmpty(tvDeliverDeviceAssetCode.getText().toString().trim())) {
            tvDeliverDeviceAssetCode.setText(assetCode.substring(1));
        }
    }

    /**
     * 获取设备的可以选择的所有货架
     */
    private void queryDrawerNeedClass(final long deviceId) {
        mDeliverRepository.queryDrawers(deviceId, mStoreHouse.getId())
                .subscribe(new ResponseObserver<List<Drawer>>() {
                    @Override
                    public void handleData(final List<Drawer> data) {
                        mDrawerIds = "";
                        for (Drawer drawer : data) {
                            mDrawerIds += "," + drawer.getId();
                        }
                        //亮灯
                        lightControl(mDrawerIds.substring(1), true);
                        mChooseDrawerDialog = new MaterialDialog.Builder(mContext)
                                .title("货架选择")
                                .items(data)
                                .itemsCallbackMultiChoice(new Integer[]{}, new MaterialDialog.ListCallbackMultiChoice() {
                                    @Override
                                    public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                                        return false;
                                    }
                                })
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        Integer[] selectIndices = dialog.getSelectedIndices();
                                        if (selectIndices != null && selectIndices.length > 0) {
                                            StringBuffer selectText = new StringBuffer();
                                            for (int index : selectIndices) {
                                                selectText.append(",").append(data.get(index).getName());
                                            }
                                            updateDeviceDrawer(deviceId, selectText.substring(1));
                                        } else {
                                            ToastUtils.showLong("请选择货架");
                                        }
                                        dialog.dismiss();
                                    }
                                })
                                .onNegative(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        dialog.dismiss();
                                    }
                                })
                                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        mChooseDrawerDialog.clearSelectedIndices();
                                    }
                                })
                                .dismissListener(new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialog) {
                                        //暗灯
                                        if (!StringUtils.isEmpty(mDrawerIds)) {
                                            lightControl(mDrawerIds.substring(1), false);
                                        }
                                    }
                                })
                                .positiveText("确定")
                                .negativeText("取消")
                                .autoDismiss(false)
                                .neutralText("清空")
                                .build();

                        mChooseDrawerDialog.show();
                    }
                });
    }

    /**
     * 上架
     *
     * @param deviceId
     * @param drawerNames
     */
    private void updateDeviceDrawer(final long deviceId, String drawerNames) {
        mDeliverRepository.updateDeviceDrawer(deviceId, drawerNames)
                .subscribe(new ResponseObserver<String>() {
                    @Override
                    public void handleData(String data) {
                        for (Device d :
                                mLstDevices) {
                            if (d.getId() == deviceId) {
                                mlstOperateDevices.add(d);
                            }
                        }
                        //暗灯
                        if (!StringUtils.isEmpty(mDrawerIds)) {
                            lightControl(mDrawerIds.substring(1), false);
                        }
                        ToastUtils.showLong(data);
                        //重新获取一下设备列表
                        mDeliverRepository.downloadDeliverGoods(mAssetCode, mStoreHouse.getId())
                                .subscribe(new ResponseObserver<List<Device>>() {
                                    @Override
                                    public void handleData(List<Device> data) {
                                        mLstDevices.clear();
                                        mLstDevices.addAll(data);
                                        setPlaceCount();
                                        mAdapter.notifyDataSetChanged();
                                    }
                                });
                    }
                });
    }

    @OnClick(R.id.btnDeliverDevicelExit)
    public void endDeliverGoods() {
        if(mlstOperateDevices.size()>0){
            mMainRepository.mobileQueryAioConfig(mStoreHouse.getLesseeId(), mStoreHouse.getId())
                    .compose(new IOTransformer<Response<List<StoreHouseAioConfig>>>(this))
                    .subscribe(new ResponseObserver<List<StoreHouseAioConfig>>() {
                        @Override
                        public void handleData(List<StoreHouseAioConfig> data) {
                            StoreHouseAioConfig config = data.get(0);
                            String printSerialName = config.getPrintSerialName();
                            String printBautRate = config.getPrintBautRate();
                            TTLUtils ttlUtils = new TTLUtils(printSerialName, printBautRate);
                            ttlUtils.printDeliverNote(mStoreHouse.getName(), mlstOperateDevices);
                        }

                        @Override
                        public void onComplete() {
                            super.onComplete();
                            finish();
                        }
                    });
        }else{
            finish();
        }

    }

    /**
     * 货架的灯控
     */
    private void lightControl(String drawerIds, boolean onOrOff) {
        //全亮，全暗
        mMainRepository.remoteCtrlIO(mStoreHouse.getId(), drawerIds, drawerIds, "light", onOrOff)
                .subscribe(new BaseObserver<String>(this, false, false) {
                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull String s) {
                    }
                });
    }

}
