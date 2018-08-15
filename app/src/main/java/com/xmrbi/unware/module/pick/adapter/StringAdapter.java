package com.xmrbi.unware.module.pick.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xmrbi.unware.R;

import java.util.List;

/**
 * Created by wzn on 2018/8/4.
 */
public class StringAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public StringAdapter(@Nullable List<String> data) {
        super(R.layout.pick_item_component_count, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.tvPickItemComponentCount, item);
    }
}
