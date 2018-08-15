package com.xmrbi.unware.module.deliver.adapter;

import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xmrbi.unware.R;
import com.xmrbi.unware.data.entity.deliver.InOutOrder;
import com.xmrbi.unware.data.entity.deliver.InOutOrderList;
import com.xmrbi.unware.module.pick.adapter.StringAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wzn on 2018/8/4.
 */
public class InOutOrderListAdapter extends BaseQuickAdapter<InOutOrder,BaseViewHolder> {
    public InOutOrderListAdapter(@Nullable List<InOutOrder> data) {
        super(R.layout.deliver_item_in_out_order_list,data );
    }

    @Override
    protected void convert(BaseViewHolder helper, InOutOrder item) {
        helper.setText(R.id.tvDeliverItemCode,item.getCode());
        helper.setText(R.id.tvDeliverItemTime,item.getInOutDate());
        helper.setText(R.id.tvDeliverItemType,item.getInOutType());
        helper.setText(R.id.tvDeliverItemPrice,item.getTotalPrice().toString());
        if(item.getInOutOrderListList()!=null&&!item.getInOutOrderListList().isEmpty()){
            RecyclerView list=helper.getView(R.id.listDeliverItemInOutOrderList);
            List<String> lstStrings=new ArrayList<>();
            for (InOutOrderList iool:item.getInOutOrderListList()) {
                lstStrings.add(iool.getComponentName()+" * "+iool.getAmount());
            }
            list.setLayoutManager(new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false));
            list.setAdapter(new StringAdapter(lstStrings));
        }

    }

}
