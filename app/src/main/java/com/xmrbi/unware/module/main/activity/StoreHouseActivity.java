package com.xmrbi.unware.module.main.activity;

import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.xmrbi.unware.R;
import com.xmrbi.unware.base.BaseActivity;
import com.xmrbi.unware.data.entity.main.User;
import com.xmrbi.unware.event.UserEvent;
import com.xmrbi.unware.module.check.activity.CheckListActivity;
import com.xmrbi.unware.module.deliver.activity.DeliverInOutOrderListActivity;
import com.xmrbi.unware.module.deliver.activity.DeliverInputActivity;
import com.xmrbi.unware.module.pick.activity.PickInOutOrderListActivity;
import com.xmrbi.unware.module.pick.activity.PickListActivity;
import com.xmrbi.unware.module.search.activity.BarcodeScanActivity;
import com.xmrbi.unware.utils.ActivityStackUtils;
import com.xmrbi.unware.utils.MediaUtils;
import com.xmrbi.unware.utils.RxBus;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;

/**
 * 无rifd仓库的功能选择页面
 * Created by wzn on 2018/5/16.
 */
public class StoreHouseActivity extends BaseActivity {
    @BindView(R.id.tvMainDeliver)
    TextView tvMainDeliver;
    @BindView(R.id.tvMainPick)
    TextView tvMainPick;

    public static void lauch(Context context, User user) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("user", user);
        ActivityStackUtils.lauch(context, StoreHouseActivity.class, bundle);
    }

    private User mUser;

    @Override
    protected int getLayout() {
        return R.layout.main_activity_store_house;
    }

    @Override
    protected void onViewCreated() {
        //播放欢迎光临的语音
//        MediaUtils.getInstance().play(R.string.welcome_people);
    }

    @Override
    protected void initEventAndData() {
        mUser = (User) mBundle.getSerializable("user");
        RxBus.getDefault().toObservable(UserEvent.class)
                .compose(this.<UserEvent>bindToLifecycle())
                .subscribe(new Consumer<UserEvent>() {
                    @Override
                    public void accept(UserEvent userEvent) throws Exception {
                        //如果当前在库人员不包含当前操作人员，直接退出到主界面
                        if (!userEvent.getLstUsers().contains(mUser)) {
                            ActivityStackUtils.finishAllActivity(StoreHouseActivity.this);
                        }
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        //如果跳转别的页面，声音还没播放完，停止播放
//        MediaUtils.getInstance().stop();
    }


    /**
     * 入库
     */
    @OnClick(R.id.tvMainDeliver)
    public void lauchDeliver() {
        DeliverInOutOrderListActivity.lauch(mContext,mUser);

    }

    /**
     * 出库
     */
    @OnClick(R.id.tvMainPick)
    public void lauchPick() {
        PickInOutOrderListActivity.lauch(mContext,mUser);
    }

    private int mCount = 0;

    @OnClick(R.id.imageView5)
    public void exit() {
        if (mCount++ > 10) {
            onBackPressed();
        }
    }
}
