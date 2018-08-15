package com.xmrbi.unware.module.setting.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.annotation.Nullable;

import com.xmrbi.unware.base.BaseFragment;
import com.xmrbi.unware.component.view.FragmentPagerAdapter.FragmentPagerAdapter;
import com.xmrbi.unware.module.setting.fragment.ServerFragment;
import com.xmrbi.unware.module.setting.fragment.ServerFragment2;
import com.xmrbi.unware.module.setting.fragment.TestDeviceFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wzn on 2018/5/4.
 */

public class SettingContentAdapter extends FragmentPagerAdapter {
    private List<BaseFragment> mlstFragments;
    private String[] mlstTabs;

    public SettingContentAdapter(FragmentManager fm) {
        super(fm);
        List<BaseFragment> lstFragments = new ArrayList<>();
        lstFragments.add(ServerFragment2.newInstance());
        lstFragments.add(TestDeviceFragment.newInstance());
        this.mlstFragments = lstFragments;
        this.mlstTabs = new String[]{"仓库设置", "设备自检"};
    }

    @Override
    public Fragment getItem(int position) {
        return mlstFragments.get(position);
    }

    @Override
    public int getCount() {
        return mlstTabs.length;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mlstTabs[position];
    }
}
