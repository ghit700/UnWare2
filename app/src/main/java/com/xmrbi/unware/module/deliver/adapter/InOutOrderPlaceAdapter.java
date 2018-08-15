package com.xmrbi.unware.module.deliver.adapter;

import android.support.annotation.Nullable;

import com.blankj.utilcode.util.StringUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xmrbi.unware.R;
import com.xmrbi.unware.data.entity.deliver.InOutOrderList;

import java.util.List;

/**
 * Created by wzn on 2018/8/7.
 */
public class InOutOrderPlaceAdapter extends BaseQuickAdapter<InOutOrderList, BaseViewHolder> {
    public InOutOrderPlaceAdapter(@Nullable List<InOutOrderList> data) {
        super(R.layout.deliver_item_in_out_order_list_place, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, InOutOrderList item) {
        helper.setText(R.id.tvDeliverComponentName, item.getComponentName());
        helper.setText(R.id.tvDeliverModelName, item.getModelName());
        helper.setText(R.id.tvDeliverBrandName, item.getBrandName());
        helper.setText(R.id.tvDeliverAmount, String.valueOf(item.getAmount()));
        helper.setText(R.id.tvDeliverSequenceCode, item.getSequenceCode());
        if (StringUtils.isEmpty(item.getDrawerIds())) {
            helper.setVisible(R.id.tvDeliverDrawers, false);
            helper.setVisible(R.id.tvDeliverDrawersText, false);
        } else {
            helper.setVisible(R.id.tvDeliverDrawers, true);
            helper.setVisible(R.id.tvDeliverDrawersText, true);
            helper.setText(R.id.tvDeliverDrawers, item.getDrawerName());
        }
        if (item.isScan()) {
            helper.setVisible(R.id.ivDeliverScan, true);
        } else {
            helper.setVisible(R.id.ivDeliverScan, false);
        }
    }
}
