package com.xmrbi.unware.module.check.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xmrbi.unware.R;
import com.xmrbi.unware.base.BaseActivity;
import com.xmrbi.unware.component.http.ResponseObserver;
import com.xmrbi.unware.data.entity.check.CheckList;
import com.xmrbi.unware.data.entity.main.User;
import com.xmrbi.unware.data.local.MainLocalSource;
import com.xmrbi.unware.data.repository.CheckRepository;
import com.xmrbi.unware.module.check.adapter.CheckListAdapter;
import com.xmrbi.unware.utils.ActivityStackUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by wzn on 2018/6/11.
 */
public class CheckListActivity extends BaseActivity {
    @BindView(R.id.listCheckList)
    RecyclerView listCheckList;
    @BindView(R.id.btnCheckListExit)
    Button btnCheckListExit;


    private CheckListAdapter mAdapter;
    private List<CheckList> mLstCheckLists;
    private MainLocalSource mMainLocalSource;
    private CheckRepository mCheckRepository;

    @Override
    protected int getLayout() {
        return R.layout.check_activity_check_list;
    }

    @Override
    protected void onViewCreated() {
        mLstCheckLists = new ArrayList<>();
        mAdapter = new CheckListAdapter(mLstCheckLists);
        initRecycleView(listCheckList, mAdapter, LinearLayoutManager.VERTICAL);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                CheckDetailActivity.lauch(mContext,mLstCheckLists.get(position).getId());
            }
        });
    }

    @Override
    protected void initEventAndData() {
        mMainLocalSource = new MainLocalSource();
        mCheckRepository = new CheckRepository(this);
        mobileCheckStoreDevicesForFive();
    }

    private void mobileCheckStoreDevicesForFive() {
        mCheckRepository.mobileCheckStoreDevicesForFive(mMainLocalSource.getStoreHouse().getId())
                .subscribe(new ResponseObserver<List<CheckList>>(this) {
                    @Override
                    public void handleData(List<CheckList> data) {
                        mLstCheckLists.addAll(data);
                        mAdapter.notifyDataSetChanged();
                    }
                });
    }

    @OnClick(R.id.btnCheckListExit)
    public void exit() {
        onBackPressed();
    }

}
