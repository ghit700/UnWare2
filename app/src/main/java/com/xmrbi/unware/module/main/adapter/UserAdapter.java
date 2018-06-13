package com.xmrbi.unware.module.main.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xmrbi.unware.R;
import com.xmrbi.unware.data.entity.main.User;

import java.util.List;

/**
 * Created by wzn on 2018/5/16.
 */
public class UserAdapter extends BaseQuickAdapter<User, BaseViewHolder> {


    public UserAdapter(@Nullable List<User> data) {
        super(R.layout.main_item_user, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, User item) {
        helper.setText(R.id.tvMainItemUserName, item.getName());
        helper.addOnClickListener(R.id.ivMainItemUserPhoto);
    }
}
