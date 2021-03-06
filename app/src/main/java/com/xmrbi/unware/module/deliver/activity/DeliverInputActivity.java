package com.xmrbi.unware.module.deliver.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.xmrbi.unware.R;
import com.xmrbi.unware.base.BaseActivity;
import com.xmrbi.unware.component.http.ResponseObserver;
import com.xmrbi.unware.data.entity.deliver.Device;
import com.xmrbi.unware.data.entity.main.StoreHouse;
import com.xmrbi.unware.data.local.MainLocalSource;
import com.xmrbi.unware.data.repository.DeliverRepository;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 订购单编号输入
 * Created by wzn on 2018/6/12.
 */
public class DeliverInputActivity extends BaseActivity {
    @BindView(R.id.tvDeliverOrderId)
    TextView tvDeliverOrderId;
    @BindView(R.id.btnDeliverOne)
    Button btnDeliverOne;
    @BindView(R.id.btnDeliverTow)
    Button btnDeliverTow;
    @BindView(R.id.btnDeliverThree)
    Button btnDeliverThree;
    @BindView(R.id.btnDeliverExit)
    Button btnDeliverExit;
    @BindView(R.id.btnDeliverFour)
    Button btnDeliverFour;
    @BindView(R.id.btnDeliverFive)
    Button btnDeliverFive;
    @BindView(R.id.btnDeliverSix)
    Button btnDeliverSix;
    @BindView(R.id.btnDeliverHeng)
    Button btnDeliverHeng;
    @BindView(R.id.btnDeliverSeven)
    Button btnDeliverSeven;
    @BindView(R.id.btnDeliverEight)
    Button btnDeliverEight;
    @BindView(R.id.btnDeliverNine)
    Button btnDeliverNine;
    @BindView(R.id.btnDeliverClear)
    Button btnDeliverClear;
    @BindView(R.id.btnDeliverZero)
    Button btnDeliverZero;
    @BindView(R.id.btnDeliverSubmit)
    Button btnDeliverSubmit;

    private DeliverRepository mDeliverRepository;
    private MainLocalSource mMainLocalSource;
    private StoreHouse mStoreHouse;

    @Override
    protected int getLayout() {
        return R.layout.deliver_activity_input;
    }

    @Override
    protected void onViewCreated() {
        mDeliverRepository=new DeliverRepository(this);
        mMainLocalSource=new MainLocalSource();
        mStoreHouse=mMainLocalSource.getStoreHouse();
    }

    @Override
    protected void initEventAndData() {

    }

    @OnClick(R.id.btnDeliverExit)
    public void exit() {
        onBackPressed();
    }

    @OnClick(R.id.btnDeliverClear)
    public void clear() {
        tvDeliverOrderId.setText(getString(R.string.deliver_order_id_hint));
    }


    /**
     * 确定
     */
    @OnClick(R.id.btnDeliverSubmit)
    public void submit() {
        if ((tvDeliverOrderId.getText().toString().trim().contains("-") && tvDeliverOrderId.getText().toString().length() == 8) ||
                (!tvDeliverOrderId.getText().toString().trim().contains("-") && tvDeliverOrderId.getText().toString().length() == 5)) {
            mDeliverRepository.downloadDeliverGoods(tvDeliverOrderId.getText().toString().trim(),mStoreHouse.getId())
                    .subscribe(new ResponseObserver<List<Device>>(this,false,false) {
                        @Override
                        public void handleData(List<Device> data) {
                            if(data.size()>0){
                                DeliverDeviceActivity.lauch(mContext, tvDeliverOrderId.getText().toString().trim());
                                finish();
                            }
                        }

                        @Override
                        protected void handleErrorData() {
                            super.handleErrorData();
                            ToastUtils.showLong("查无数据");
                        }
                    });

        }else{
            ToastUtils.showLong(getString(R.string.deliver_order_id_hint));
        }

    }


    @OnClick({R.id.btnDeliverHeng, R.id.btnDeliverOne, R.id.btnDeliverTow, R.id.btnDeliverThree, R.id.btnDeliverFour, R.id.btnDeliverFive, R.id.btnDeliverSix, R.id.btnDeliverSeven, R.id.btnDeliverEight, R.id.btnDeliverNine, R.id.btnDeliverZero})
    public void onViewClicked(View view) {
        String number = ((Button) view).getText().toString().trim();
        if (tvDeliverOrderId.getText().toString().trim().equals(getString(R.string.deliver_order_id_hint))) {
            tvDeliverOrderId.setText(number);
        } else {
            tvDeliverOrderId.setText(tvDeliverOrderId.getText().toString().trim() + number);
        }
    }
}
