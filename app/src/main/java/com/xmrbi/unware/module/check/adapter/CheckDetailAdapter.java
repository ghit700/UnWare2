package com.xmrbi.unware.module.check.adapter;

import android.support.annotation.Nullable;
import android.widget.ProgressBar;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xmrbi.unware.R;
import com.xmrbi.unware.data.entity.check.RfidNewInventoryEntity;

import java.util.List;

/**
 * Created by wzn on 2018/6/11.
 */
public class CheckDetailAdapter extends BaseQuickAdapter<RfidNewInventoryEntity, BaseViewHolder> {
    public CheckDetailAdapter(@Nullable List<RfidNewInventoryEntity> data) {
        super(R.layout.check_item_check_detail, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, RfidNewInventoryEntity item) {
        helper.setText(R.id.tvCheckItemDrawer, item.getDrawerName());
        int count = item.getNoRfidCheck() + item.getNoRfidUncheck() + item.getRfidCheck() + item.getRfidUncheck();
        int checkCount = item.getNoRfidCheck() + item.getRfidCheck();
        helper.setMax(R.id.pbCheckItemProgress, count);
        helper.setProgress(R.id.pbCheckItemProgress, checkCount);
        if(count==checkCount){
            helper.setVisible(R.id.ivCheckItemCheck,true);
        }else{
            helper.setVisible(R.id.ivCheckItemCheck,false);
        }
    }
}
