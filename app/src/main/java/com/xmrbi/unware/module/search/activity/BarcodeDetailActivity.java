package com.xmrbi.unware.module.search.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.utilcode.util.StringUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xmrbi.unware.R;
import com.xmrbi.unware.base.BaseActivity;
import com.xmrbi.unware.component.http.ResponseObserver;
import com.xmrbi.unware.data.entity.deliver.Device;
import com.xmrbi.unware.data.entity.main.Rfid;
import com.xmrbi.unware.data.entity.main.StoreHouse;
import com.xmrbi.unware.data.entity.pick.PickListDetail;
import com.xmrbi.unware.data.local.MainLocalSource;
import com.xmrbi.unware.data.repository.DeliverRepository;
import com.xmrbi.unware.data.repository.PickRepository;
import com.xmrbi.unware.module.deliver.adapter.DeviceAdapter;
import com.xmrbi.unware.module.pick.adapter.PickDetailAdapter;
import com.xmrbi.unware.utils.ActivityStackUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wzn on 2018/7/6.
 */
public class BarcodeDetailActivity extends BaseActivity {
    @BindView(R.id.listBarcodeDevice)
    RecyclerView listBarcodeDevice;
    @BindView(R.id.btnBarcodeDetailExit)
    Button btnBarcodeDetailExit;

    public static void lauch(Context context, String code, Integer type, Long userId) {
        Bundle bundle = new Bundle();
        bundle.putString("code", code);
        bundle.putLong("userId", userId);
        bundle.putInt("type", type);
        ActivityStackUtils.lauch(context, BarcodeDetailActivity.class, bundle);
    }

    /**
     * 领料单
     */
    public static Integer TYPE_PICK = 0x0001;
    /**
     * 送货单
     */
    public static Integer TYPE_DELIVER = 0x0002;

    private int mType;
    private BaseQuickAdapter mAdapter;
    private List<Device> mlstDevice;
    private List<PickListDetail> mlstPickListDetails;
    private PickRepository mPickRepository;
    private DeliverRepository mDeliverRepository;
    private MainLocalSource mMainLocalSource;
    private Long mUserId;
    private StoreHouse mStoreHouse;
    private String mCode;

    @Override
    protected int getLayout() {
        return R.layout.search_activity_barcode_detail;
    }


    @Override
    protected void onViewCreated() {
        mType = mBundle.getInt("type");
        if (mType == TYPE_PICK) {
            mlstPickListDetails = new ArrayList<>();
            mAdapter = new PickDetailAdapter(mlstPickListDetails, false);
            mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
                @Override
                public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                    String[] rfidCodes = mlstPickListDetails.get(position).getRfidCode().split(",");
                    new MaterialDialog.Builder(mContext)
                            .title(R.string.pick_list_detail_rfid_list_title)
                            .items(rfidCodes)
                            .positiveText(android.R.string.cancel)
                            .show();
                }
            });
        } else if (mType == TYPE_DELIVER) {
            mlstDevice = new ArrayList<>();
            mAdapter = new DeviceAdapter(mlstDevice);
        }
        initRecycleView(listBarcodeDevice, mAdapter, LinearLayoutManager.VERTICAL);
    }

    @Override
    protected void initEventAndData() {
        mUserId = mBundle.getLong("userId");
        mCode = mBundle.getString("code");
        mMainLocalSource = new MainLocalSource();
        mStoreHouse = mMainLocalSource.getStoreHouse();
        if (mType == TYPE_PICK) {
            mPickRepository = new PickRepository(this);
            mPickRepository.downloadPickListDetail(mUserId, mStoreHouse.getId(), Long.parseLong(mCode))
                    .subscribe(new ResponseObserver<List<PickListDetail>>() {
                        @Override
                        public void handleData(List<PickListDetail> data) {
                            for (PickListDetail detail : data) {
                                Rfid rfid = new Rfid(detail.getRfidCode(), detail.getComponentName(), true, detail.getDrawerIds());

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
                            }

                            mAdapter.notifyDataSetChanged();
                        }
                    });
        } else if (mType == TYPE_DELIVER) {
            mDeliverRepository = new DeliverRepository(this);
            mDeliverRepository.downloadDeliverGoods(mCode, mStoreHouse.getId())
                    .subscribe(new ResponseObserver<List<Device>>() {
                        @Override
                        public void handleData(List<Device> data) {
                            mlstDevice.addAll(data);
                            mAdapter.notifyDataSetChanged();
                        }
                    });
        }
    }


    @OnClick(R.id.btnBarcodeDetailExit)
    public void exit() {
        onBackPressed();
    }
}
