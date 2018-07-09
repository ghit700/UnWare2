package com.xmrbi.unware.module.check.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.xmrbi.unware.R;
import com.xmrbi.unware.base.BaseActivity;
import com.xmrbi.unware.component.http.Response;
import com.xmrbi.unware.component.http.ResponseObserver;
import com.xmrbi.unware.data.entity.check.RfidNewInventoryEntity;
import com.xmrbi.unware.data.repository.CheckRepository;
import com.xmrbi.unware.module.check.adapter.CheckDetailAdapter;
import com.xmrbi.unware.utils.ActivityStackUtils;
import com.xmrbi.unware.utils.QcCodeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by wzn on 2018/6/11.
 */
public class CheckDetailActivity extends BaseActivity {

    public static void lauch(Context context, Long checkId) {
        Bundle bundle = new Bundle();
        bundle.putLong("checkId", checkId);
        ActivityStackUtils.lauch(context, CheckDetailActivity.class, bundle);
    }

    @BindView(R.id.ivCheckDetailQCCode)
    ImageView ivCheckDetailQCCode;
    @BindView(R.id.listCheckDetail)
    RecyclerView listCheckDetail;
    @BindView(R.id.btnCheckDetailExit)
    Button btnCheckDetailExit;
    @BindView(R.id.tvCheckDetailFinishCount)
    TextView tvCheckDetailFinishCount;
    @BindView(R.id.tvCheckDetailNonFinishCount)
    TextView tvCheckDetailNonFinishCount;

    /**
     * 盘点单id
     */
    private Long mCheckId;
    private CheckDetailAdapter mAdapter;
    private List<RfidNewInventoryEntity> mlstRfidNewInventoryEntities;
    private CheckRepository mCheckRepository;

    @Override
    protected int getLayout() {
        return R.layout.check_activity_check_detail;
    }

    @Override
    protected void onViewCreated() {
        mCheckId = mBundle.getLong("checkId");
        ivCheckDetailQCCode.setImageBitmap(QcCodeUtils.encodeAsBitmap("'pd" + mCheckId + "'"));
        mlstRfidNewInventoryEntities = new ArrayList<>();
        mAdapter = new CheckDetailAdapter(mlstRfidNewInventoryEntities);
        initRecycleView(listCheckDetail, mAdapter, LinearLayoutManager.VERTICAL);
    }

    @Override
    protected void initEventAndData() {
        mCheckRepository = new CheckRepository(this);
        intervalQueryLastCheckData();

    }

    /**
     * 定时获取最新的盘点情况(5s)
     */
    private void intervalQueryLastCheckData() {
        Observable.interval(0, 10, TimeUnit.SECONDS)
                .compose(this.<Long>bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .flatMap(new Function<Long, ObservableSource<Response<List<RfidNewInventoryEntity>>>>() {
                    @Override
                    public ObservableSource<Response<List<RfidNewInventoryEntity>>> apply(Long aLong) throws Exception {
                        return mCheckRepository.mobileCountCheckStoreDeviceItem(mCheckId);
                    }
                })
                .subscribe(new ResponseObserver<List<RfidNewInventoryEntity>>() {
                    @Override
                    public void handleData(List<RfidNewInventoryEntity> data) {
                        mlstRfidNewInventoryEntities.clear();
                        mlstRfidNewInventoryEntities.addAll(data);
                        int checkCount=0;
                        int nonCheckCount=0;
                        for (RfidNewInventoryEntity entity:mlstRfidNewInventoryEntities) {
                            checkCount+=entity.getCheck();
                            nonCheckCount+=entity.getNoCheck();
                        }
                        tvCheckDetailFinishCount.setText(String.valueOf(checkCount));
                        tvCheckDetailNonFinishCount.setText(String.valueOf(nonCheckCount));
                        mAdapter.notifyDataSetChanged();
                    }
                });
    }


    @OnClick(R.id.btnCheckDetailExit)
    public void exit() {
        onBackPressed();
    }
}
