package com.xmrbi.unware.data.repository;


import com.xmrbi.unware.base.BaseActivity;

/**
 * Created by wzn on 2018/4/26.
 */

public class BaseRepository {
    protected BaseActivity mBaseActivity;

    public BaseRepository(BaseActivity mBaseActivity) {
        this.mBaseActivity = mBaseActivity;
    }
}
