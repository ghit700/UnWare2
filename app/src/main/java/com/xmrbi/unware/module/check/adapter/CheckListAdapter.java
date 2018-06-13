package com.xmrbi.unware.module.check.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xmrbi.unware.R;
import com.xmrbi.unware.data.entity.check.CheckList;

import java.util.List;

/**
 * Created by wzn on 2018/6/11.
 */
public class CheckListAdapter extends BaseQuickAdapter<CheckList,BaseViewHolder> {
    public CheckListAdapter(@Nullable List<CheckList> data) {
        super(R.layout.check_item_check_list,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, CheckList item) {
        //去除00:00:00
        helper.setText(R.id.tvCheckItemTime,item.getCheckDate().replaceAll(":","").replaceAll("0",""));
        helper.setText(R.id.tvCheckItemID,item.getBillNo());
        helper.setText(R.id.tvCheckItemCreator,item.getCreator());
    }
}
