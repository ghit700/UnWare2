package com.xmrbi.unware.module.pick.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xmrbi.unware.R;
import com.xmrbi.unware.data.entity.deliver.InOutOrderList;

import java.util.List;

/**
 * Created by wzn on 2018/8/13.
 */
public class InOutOrderDeviceAdatper extends BaseQuickAdapter<InOutOrderList,BaseViewHolder> {
    public InOutOrderDeviceAdatper(@Nullable List<InOutOrderList> data) {
        super(R.layout.pick_item_in_out_order_device,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, InOutOrderList item) {
        helper.setText(R.id.tvPickComponentName,item.getComponentName());
        helper.setText(R.id.tvPickBrandName,item.getModelName()+" "+item.getBrandName());
        helper.setText(R.id.tvPickPlaceName,item.getDrawerName());
        helper.setText(R.id.tvPickAmount,item.getAmount().toString());
        helper.setText(R.id.tvPickSequenceCode,item.getSequenceCode());
        helper.setText(R.id.tvPickPrice,item.getPrice().toString());
        helper.setText(R.id.tvPickTotalAmount,String.valueOf(item.getPrice()*item.getAmount()));
        if (item.isScan()) {
            helper.setVisible(R.id.ivPickScan, true);
        } else {
            helper.setVisible(R.id.ivPickScan, false);
        }
    }
}
