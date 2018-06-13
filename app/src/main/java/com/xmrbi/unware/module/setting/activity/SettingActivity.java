package com.xmrbi.unware.module.setting.activity;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.xmrbi.unware.R;
import com.xmrbi.unware.base.BaseActivity;
import com.xmrbi.unware.module.setting.adapter.SettingContentAdapter;

import butterknife.BindView;

/**
 * Created by wzn on 2018/5/3.
 */

public class SettingActivity extends BaseActivity {
    @BindView(R.id.tlSettingTab)
    TabLayout tlSettingTab;
    @BindView(R.id.vpSettingContent)
    ViewPager vpSettingContent;

    private SettingContentAdapter mAdapter;

    @Override
    protected int getLayout() {
        return R.layout.setting_activity;
    }

    @Override
    protected void onViewCreated() {
        mAdapter = new SettingContentAdapter(getFragmentManager());
        vpSettingContent.setAdapter(mAdapter);
        tlSettingTab.setupWithViewPager(vpSettingContent, false);
    }

    @Override
    protected void initEventAndData() {

    }

}
