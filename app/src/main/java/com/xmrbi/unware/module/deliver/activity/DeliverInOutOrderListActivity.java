package com.xmrbi.unware.module.deliver.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xmrbi.unware.R;
import com.xmrbi.unware.base.BaseActivity;
import com.xmrbi.unware.component.http.BaseObserver;
import com.xmrbi.unware.component.http.ResponseObserver;
import com.xmrbi.unware.data.entity.deliver.InOutOrder;
import com.xmrbi.unware.data.entity.main.StoreHouse;
import com.xmrbi.unware.data.entity.main.User;
import com.xmrbi.unware.data.local.MainLocalSource;
import com.xmrbi.unware.data.repository.DeliverRepository;
import com.xmrbi.unware.data.repository.MainRepository;
import com.xmrbi.unware.module.deliver.adapter.InOutOrderListAdapter;
import com.xmrbi.unware.utils.ActivityStackUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by wzn on 2018/8/4.
 */
public class DeliverInOutOrderListActivity extends BaseActivity {

    public static void lauch(Context context, User user) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("user", user);
        ActivityStackUtils.lauch(context, DeliverInOutOrderListActivity.class, bundle);
    }

    @BindView(R.id.ivDeliverBack)
    ImageView ivDeliverBack;
    @BindView(R.id.btnDeliverExit)
    Button btnDeliverExit;
    @BindView(R.id.tvDeliverWelcome)
    TextView tvDeliverWelcome;
    @BindView(R.id.listDeliverOrder)
    RecyclerView listDeliverOrder;

    private DeliverRepository mDeliverRepository;
    private MainRepository mMainRepository;
    private MainLocalSource mMainLocalSource;
    private StoreHouse mStoreHouse;
    private User mUser;
    private InOutOrderListAdapter mAdapter;
    private List<InOutOrder> mLstInOutOrders;

    @Override
    protected int getLayout() {
        return R.layout.deliver_activity_in_out_order_list;
    }

    @Override
    protected void onViewCreated() {
        mLstInOutOrders = new ArrayList<>();
        mAdapter = new InOutOrderListAdapter(mLstInOutOrders);
        initRecycleView(listDeliverOrder, mAdapter, LinearLayoutManager.VERTICAL);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                InOutOrder order = mLstInOutOrders.get(position);
                DeliverInOutOrderPlaceActivity.lauch(mContext, order.getCode(), mUser, order.getId());
            }
        });
    }

    @Override
    protected void initEventAndData() {
        mDeliverRepository = new DeliverRepository(this);
        mMainLocalSource = new MainLocalSource();
        mMainRepository = new MainRepository(this);
        mStoreHouse = mMainLocalSource.getStoreHouse();
        mUser = (User) mBundle.getSerializable("user");
        tvDeliverWelcome.setText(mUser.getName() + ",欢迎光临" + mStoreHouse.getName() + "无人值守仓库");
    }

    @Override
    protected void onResume() {
        super.onResume();
        queryInOutOrderList();
    }

    /**
     * 获取出入库单列表
     */
    private void queryInOutOrderList() {
        mDeliverRepository.queryInOutOrderList(mUser.getId(), mStoreHouse.getId(), true)
                .subscribe(new ResponseObserver<List<InOutOrder>>(this) {
                    @Override
                    public void handleData(List<InOutOrder> data) {
                        mLstInOutOrders.clear();
                        mLstInOutOrders.addAll(data);
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    protected void handleErrorData() {
                        super.handleErrorData();
                        mLstInOutOrders.clear();
                        mAdapter.notifyDataSetChanged();
                    }
                });
    }


    @OnClick({R.id.ivDeliverBack, R.id.btnDeliverExit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivDeliverBack:
                back();
                break;
            case R.id.btnDeliverExit:
                exitStoreHouse();
                break;
        }
    }

    /**
     * 退出仓库
     */
    private void exitStoreHouse() {
        final MaterialDialog dialog = new MaterialDialog.Builder(mContext)
                .content("正在打开仓库大门")
                .progress(true, 0)
                .progressIndeterminateStyle(false)
                .build();
        dialog.show();


        mMainRepository.controlDoor(mStoreHouse.getId(), true)
                .subscribe(new BaseObserver<String>() {
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

                    @Override
                    public void onComplete() {
                        super.onComplete();
                        dialog.dismiss();
                    }
                });
    }


    /**
     * 返回上一个界面
     */
    private void back() {
        onBackPressed();
    }


}
