package com.xmrbi.unware.module.pick.adapter;

import android.support.annotation.Nullable;

import com.blankj.utilcode.util.StringUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xmrbi.unware.R;
import com.xmrbi.unware.data.entity.pick.PickListDetail;

import java.util.List;

/**
 * Created by wzn on 2018/5/18.
 */

public class PickDetailAdapter extends BaseQuickAdapter<PickListDetail, BaseViewHolder> {
    private Boolean isShowFinish = true;

    public PickDetailAdapter(@Nullable List<PickListDetail> data) {
        super(R.layout.pick_item_pick_detail, data);
    }

    public PickDetailAdapter(@Nullable List<PickListDetail> data, boolean isShowFinish) {
        super(R.layout.pick_item_pick_detail, data);
        this.isShowFinish = isShowFinish;
    }

    @Override
    protected void convert(BaseViewHolder helper, PickListDetail item) {
        helper.setText(R.id.tvPickItemComponentName, item.getComponentName());
        helper.setText(R.id.tvPickItemBrandName, item.getBrandName());
        helper.setText(R.id.tvPickItemUnit, item.getUnit());
        helper.setText(R.id.tvPickItemDrawerName, item.getDrawerName());
        helper.setText(R.id.tvPickItemDevicePrice, item.getPrice().toString());
        helper.setText(R.id.tvPickItemActualAmount, item.getRequestAmount().toString());
        helper.setText(R.id.tvPickItemRequestAmount, item.getRequestAmount().toString());
        helper.setText(R.id.tvPickItemPrice, String.format("%.2f", item.getPrice() * item.getRequestAmount()));
        helper.setVisible(R.id.ivPickItemCheckRight, false);
        helper.setVisible(R.id.btnPickItemCheckRfid, false);
        helper.setVisible(R.id.tvPickItemRfid, false);
        helper.setVisible(R.id.tvPickItemRfidText, false);

        helper.setVisible(R.id.tvPickItemRfidScanAmount, false);

        if (!StringUtils.isEmpty(item.getRfidCode())) {
            if(!StringUtils.isEmpty(item.getRfid())&&item.getRfid().indexOf(",")==0){
                item.setRfid(item.getRfid().substring(1));
                if(item.getRfid().lastIndexOf(",")==item.getRfid().length()-1){
                    item.setRfid(item.getRfid().substring(0,item.getRfid().length()-1));
                }
            }
            helper.setVisible(R.id.tvPickItemRfidText, true);

            String[] rfids = item.getRfidCode().split(",");
            if (rfids.length > 1) {
                helper.setVisible(R.id.tvPickItemRfidScanAmount, true);
                Integer rfidScanAmount = 0;
                if (!StringUtils.isEmpty(item.getRfid())) {
                    rfidScanAmount = item.getRfid().split(",").length;
                }
                helper.setText(R.id.tvPickItemRfidText, "识别情况：");
                if(isShowFinish){
                    helper.setText(R.id.tvPickItemRfidScanAmount, rfidScanAmount + "/" + rfids.length);
                }else{
                    helper.setText(R.id.tvPickItemRfidScanAmount, rfidScanAmount + "/" + rfidScanAmount);
                }
                helper.setVisible(R.id.btnPickItemCheckRfid, true);
                helper.addOnClickListener(R.id.btnPickItemCheckRfid);
            } else {
                helper.setText(R.id.tvPickItemRfidText, "RFID编号：");
                helper.setVisible(R.id.tvPickItemRfid, true);
                helper.setText(R.id.tvPickItemRfid, item.getRfidCode());
            }

            if (!StringUtils.isEmpty(item.getRfid()) && item.getRfidCode().length() == item.getRfid().length() && isShowFinish) {
                helper.setVisible(R.id.ivPickItemCheckRight, true);
                item.setRelation(true);
            }
        }
    }
}
