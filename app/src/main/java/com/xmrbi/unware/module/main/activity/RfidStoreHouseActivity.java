package com.xmrbi.unware.module.main.activity;

import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.xmrbi.unware.R;
import com.xmrbi.unware.base.BaseActivity;
import com.xmrbi.unware.data.entity.main.User;
import com.xmrbi.unware.module.check.activity.CheckListActivity;
import com.xmrbi.unware.module.deliver.activity.DeliverInputActivity;
import com.xmrbi.unware.module.pick.activity.PickListActivity;
import com.xmrbi.unware.module.search.activity.BarcodeScanActivity;
import com.xmrbi.unware.utils.ActivityStackUtils;
import com.xmrbi.unware.utils.MediaUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * rifd仓库的功能选择页面
 * Created by wzn on 2018/5/16.
 */
public class RfidStoreHouseActivity extends BaseActivity {
    @BindView(R.id.tvMainRfidPick)
    TextView tvMainRfidPick;
    @BindView(R.id.tvMainRfidDeliver)
    TextView tvMainRfidDeliver;
    @BindView(R.id.tvMainRfidSearch)
    TextView tvMainRfidSearch;
    @BindView(R.id.tvMainRfidCheck)
    TextView tvMainRfidCheck;

    public static void lauch(Context context, User user) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("user", user);
        ActivityStackUtils.lauch(context, RfidStoreHouseActivity.class, bundle);
    }

    private User mUser;

    @Override
    protected int getLayout() {
        return R.layout.main_activity_rfid_store_house;
    }

    @Override
    protected void onViewCreated() {
        //播放欢迎光临的语音
        MediaUtils.getInstance().play(R.string.welcome_people);
    }

    @Override
    protected void initEventAndData() {
        mUser = (User) mBundle.getSerializable("user");
    }

    @Override
    protected void onStop() {
        super.onStop();
        //如果跳转别的页面，声音还没播放完，停止播放
        MediaUtils.getInstance().stop();
    }

    @OnClick(R.id.tvMainRfidPick)
    public void lauchPick() {
        PickListActivity.lauch(mContext, mUser);
    }

    @OnClick(R.id.tvMainRfidDeliver)
    public void lauchDeliver() {
        if (mUser.isKeeper()) {
            lauch(DeliverInputActivity.class);
        }
    }

    @OnClick(R.id.tvMainRfidSearch)
    public void lauchSearch() {
        if (mUser.isKeeper()) {
//        lauch(BarcodeScanActivity.class);
        }
    }

    @OnClick(R.id.tvMainRfidCheck)
    public void lauchCheck() {
        if (mUser.isKeeper()) {
            lauch(CheckListActivity.class);
        }
    }
}