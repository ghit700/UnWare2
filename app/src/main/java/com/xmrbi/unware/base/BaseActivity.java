package com.xmrbi.unware.base;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.trello.rxlifecycle2.components.RxActivity;
import com.xmrbi.unware.R;
import com.xmrbi.unware.utils.ActivityStackUtils;


import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;


/**
 * Created by wzn on 2018/3/29.
 */

public abstract class BaseActivity extends RxActivity {
    protected Activity mContext;
    protected Unbinder mUnbinder;
    protected Bundle mBundle;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayout());
        mUnbinder = ButterKnife.bind(this);
        mContext = this;
        mBundle = getIntent().getExtras();
        ActivityStackUtils.addActivity(this);
        onViewCreated();
        initEventAndData();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityStackUtils.removeActivity(this);
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
    }

    protected abstract int getLayout();

    protected abstract void onViewCreated();

    protected abstract void initEventAndData();
    /******utils******/
    /**
     * 初始化获取权限的类
     *
     * @return
     */
    protected RxPermissions getRxPermissionsInstance() {
        return new RxPermissions(mContext);
    }

    public void lauch(Class clazz) {
        ActivityUtils.startActivity(mContext, clazz);
    }

    protected void lauch(Intent intent) {
        ActivityUtils.startActivity(intent);
    }

    protected void lauch(Class clazz, String tille) {
        Intent intent = new Intent(mContext, clazz);
        Bundle bundle = new Bundle();
        bundle.putString("title", tille);
        intent.putExtras(bundle);
        ActivityUtils.startActivity(intent);
    }

    protected void lauchPermission(String[] manifests, final Class clazz, final String title) {
        if (Build.VERSION.SDK_INT >= 23) {
            getRxPermissionsInstance().request(manifests).subscribe(new Consumer<Boolean>() {
                @Override
                public void accept(@NonNull Boolean granted) throws Exception {
                    if (granted) {
                        lauch(clazz, title);
                    } else {
                        ToastUtils.showLong(R.string.permissions_fail);
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        startActivity(intent);
                    }
                }
            });
        } else {
            lauch(clazz, title);
        }

    }

    protected void initRecycleView(RecyclerView list, BaseQuickAdapter adapter, int duration) {
        list.setLayoutManager(new LinearLayoutManager(this, duration, false));
        list.setAdapter(adapter);
    }


}
