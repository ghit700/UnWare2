package com.xmrbi.unware.module.pick.adapter;

import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.blankj.utilcode.util.StringUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xmrbi.unware.R;
import com.xmrbi.unware.data.entity.pick.PickOrder;

import java.util.Arrays;
import java.util.List;


/**
 * 领料单adapter
 * Created by wzn on 2018/5/17.
 */

public class PickOrderAdapter extends BaseQuickAdapter<PickOrder, BaseViewHolder> {
    public PickOrderAdapter(@Nullable List data) {
        super(R.layout.pick_item_order_list, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, PickOrder item) {
        helper.setText(R.id.tvPickItemBsnum, item.getBsnum());
        helper.setText(R.id.tvPickItemMoney, item.getTotalCount().toString());
        helper.setText(R.id.tvPickItemPickTime, item.getCreateTime());
        helper.setText(R.id.tvPickItemBranch, item.getZoneName());
        helper.setText(R.id.tvPickItemUse, item.getExpenseItemName());
        if (!StringUtils.isEmpty(item.getExpenseItemName())) {
            RecyclerView listPickItemDevice = helper.getView(R.id.listPickItemDevice);
            listPickItemDevice.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
            String[] devices = item.getComponentCounts().split(",");
            listPickItemDevice.setAdapter(new DeviceAdapter(Arrays.asList(devices)));
        }
    }

    class DeviceAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

        public DeviceAdapter(@Nullable List<String> data) {
            super(R.layout.pick_item_component_count, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, String item) {
            helper.setText(R.id.tvPickItemComponentCount, item);
        }
    }


}
