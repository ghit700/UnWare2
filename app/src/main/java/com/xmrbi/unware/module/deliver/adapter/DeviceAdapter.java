package com.xmrbi.unware.module.deliver.adapter;

import android.support.annotation.Nullable;

import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xmrbi.unware.R;
import com.xmrbi.unware.data.entity.deliver.Device;

import java.util.List;

/**
 * Created by wzn on 2018/6/12.
 */
public class DeviceAdapter extends BaseQuickAdapter<Device,BaseViewHolder> {
    public DeviceAdapter(@Nullable List<Device> data) {
        super(R.layout.deliver_item_device,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Device item) {
        helper.setText(R.id.tvDeliverItemName,item.getComponentName());
        helper.setText(R.id.tvDeliverItemBrandName,item.getBrandName());
        helper.setText(R.id.tvDeliverItemModelName,item.getModelName());
        helper.setText(R.id.tvDeliverItemAmount,item.getAmount().toString());
        helper.setText(R.id.tvDeliverItemAssetCode,item.getAssetCode());
        if(!StringUtils.isEmpty(item.getDrawerNames())){
            //已上架
            helper.setVisible(R.id.btnDeliverItemChooseDrawer,false);
            helper.setVisible(R.id.tvDeliverItemDrawerText,true);
            helper.setVisible(R.id.tvDeliverItemDrawer,true);
            helper.setText(R.id.tvDeliverItemDrawer,item.getDrawerNames());
        }else if(!StringUtils.isEmpty(item.getCode())){
            //已贴卡，未上架
            helper.setVisible(R.id.btnDeliverItemChooseDrawer,true);
            helper.addOnClickListener(R.id.btnDeliverItemChooseDrawer);
            helper.setVisible(R.id.tvDeliverItemDrawerText,false);
            helper.setVisible(R.id.tvDeliverItemDrawer,false);
            helper.setText(R.id.tvDeliverItemDrawer,item.getDrawerNames());
        }else{
            //未贴卡
            helper.setVisible(R.id.btnDeliverItemChooseDrawer,false);
            helper.setVisible(R.id.tvDeliverItemDrawerText,true);
            helper.setVisible(R.id.tvDeliverItemDrawer,true);
            helper.setText(R.id.tvDeliverItemDrawer, Utils.getApp().getResources().getText(R.string.deliver_no_post_card_hint).toString());
        }
    }
}
