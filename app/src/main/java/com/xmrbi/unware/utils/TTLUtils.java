package com.xmrbi.unware.utils;

import android.os.Handler;
import android.os.Message;

import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.szzk.ttl_libs.TTL_Factory;
import com.xmrbi.unware.data.entity.deliver.Device;
import com.xmrbi.unware.data.entity.main.User;
import com.xmrbi.unware.data.entity.pick.PickListDetail;
import com.xmrbi.unware.data.entity.pick.PickOrder;
import com.xmrbi.unware.event.TTLEvent;
import com.xmrbi.unware.module.main.activity.MainActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by wzn on 2018/5/21.
 */

public class TTLUtils {
    private TTL_Factory mTtlFactory;

    /**
     * @param SerialName 串口地址
     * @param BautRate   波特率
     */
    public TTLUtils(String SerialName, String BautRate) {
        mTtlFactory = TTL_Factory.geTtl_Factory(new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.obj != null) {
                    RxBus.getDefault().post(new TTLEvent((int) msg.obj, msg.what));
                }
            }
        });
        mTtlFactory.OpenPort(SerialName, Integer.parseInt(BautRate));
    }

    /**
     * 检查缺纸情况
     */
    public void checkPaper() {
        mTtlFactory.Check_Paper();
    }

    /**********************不存在这个方法，会报错***********************************/
//    /**
//     * 断开串口连接
//     */
//    public void disconnect() {
//        if (mTtlFactory != null && mTtlFactory.isConnection()) {
//            mTtlFactory.Disconnect();
//        }
//    }

    /**********************不存在这个方法，会报错***********************************/


    //打印领料小票
    public void printPickNote(String storeHouseName, User user, PickOrder order, List<PickListDetail> data) {
        if (mTtlFactory.isConnection()) {
            mTtlFactory.PrintText("\r\n", "3", "1", 0);
            mTtlFactory.PrintText("\r\n________________________________", "2", "1", 0);
            mTtlFactory.PrintText("厦门路桥管理无人值守库房", "2", "3", 0);
            mTtlFactory.PrintText("\r\n", "3", "1", 0);
            mTtlFactory.PrintText("现场机客户凭单", "2", "1", 0);
            mTtlFactory.PrintText("---------------", "2", "1", 0);
            mTtlFactory.PrintText("\n", "3", "1", 0);
            Date d = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm:ss");
            String dateNowStr = sdf.format(d);
            mTtlFactory.PrintText(storeHouseName, "1", "1", 0);//仓库名
            mTtlFactory.PrintText("", "2", "1", 0);
            mTtlFactory.PrintText("日期:" + dateNowStr, "1", "1", 0);
            mTtlFactory.PrintText("\n", "3", "1", 0);
            mTtlFactory.PrintText("领料人：" + user.getName(), "1", "1", 0);
            mTtlFactory.PrintText("", "2", "1", 0);
            mTtlFactory.PrintText("事物编号：" + order.getBsnum(), "1", "1", 0);
            mTtlFactory.PrintText("", "2", "1", 0);
            mTtlFactory.PrintText("领料部门：" + order.getZoneName(), "1", "1", 0);
            mTtlFactory.PrintText("", "2", "1", 0);
            mTtlFactory.PrintText("领料用途：" + order.getExpenseItemName(), "1", "1", 0);
            mTtlFactory.PrintText("\r\n", "3", "1", 0);
            mTtlFactory.PrintText(" - - - - - - - - - - - - - - - -", "2", "1", 0);
            mTtlFactory.PrintText("领料清单", "2", "1", 0);
            mTtlFactory.PrintText("\r\n", "3", "1", 0);
            if (data != null && data.size() > 0) {
                for (int i = 0; i < data.size(); i++) {
                    mTtlFactory.PrintText(data.get(i).getComponentName() + "*" + data.get(i).getRequestAmount(), "1", "1", 0);
                    mTtlFactory.PrintText("", "2", "1", 0);

                }
            }
            mTtlFactory.PrintText("\r\n", "3", "1", 0);
            mTtlFactory.PrintBarcode(String.valueOf("pick"+order.getId()), 3,80,9,0);
            mTtlFactory.PrintText("\r\n", "3", "1", 0);
            mTtlFactory.PrintText("\r\n", "3", "1", 0);
            mTtlFactory.PrintText("\r\n_________________________________", "3", "1", 0);
        }
    }

    public void printDeliverNote(String storeHouseName, List<Device> lstDevices,String code) {

        mTtlFactory.PrintText("\r\n", "3", "1", 0);
        mTtlFactory.PrintText("\r\n________________________________", "2", "1", 0);
        mTtlFactory.PrintText("厦门路桥管理无人值守库房", "2", "3", 0);
        mTtlFactory.PrintText("\r\n", "3", "1", 0);
        mTtlFactory.PrintText("现场机客户凭单", "2", "1", 0);
        mTtlFactory.PrintText("---------------", "2", "1", 0);
        mTtlFactory.PrintText("\n", "3", "1", 0);
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm:ss");
        String dateNowStr = sdf.format(d);
        String dateNowStr1 = sdf1.format(d);
        if (storeHouseName != null) {
            mTtlFactory.PrintText(storeHouseName, "1", "1", 0);//仓库名
        }
        mTtlFactory.PrintText("", "2", "1", 0);
        mTtlFactory.PrintText("日期:" + dateNowStr, "1", "1", 0);
        mTtlFactory.PrintText("\n", "3", "1", 0);
//        mTtlFactory.PrintText("送货单位：" + lstDevices.get(0).getBrandName(), "1", "1", 0);
        mTtlFactory.PrintText("", "2", "1", 0);
//        mTtlFactory.PrintText("事物编号："+device.getBsnum(), "1", "1", 0);
        mTtlFactory.PrintText("", "2", "1", 0);
//        mTtlFactory.PrintText("送货单位：养护部", "1", "1", 0);
//        mTtlFactory.PrintText("", "2", "1", 0);
//        mTtlFactory.PrintText("领料用途：养护物资", "1", "1", 0);
//        mTtlFactory.PrintText("\r\n", "3", "1", 0);
        mTtlFactory.PrintText(" - - - - - - - - - - - - - - - -", "2", "1", 0);
        mTtlFactory.PrintText("送货清单", "2", "1", 0);
        mTtlFactory.PrintText("\r\n", "3", "1", 0);
        if (lstDevices.size() > 0) {
            for (int i = 0; i < lstDevices.size(); i++) {
                mTtlFactory.PrintText(lstDevices.get(i).getComponentName() + " * " + lstDevices.get(i).getAmount(), "1", "1", 0);
                mTtlFactory.PrintText("", "2", "1", 0);
            }
        }
        mTtlFactory.PrintText("\r\n", "3", "1", 0);

        mTtlFactory.PrintBarcode(code, 3,80,9,0);
        mTtlFactory.PrintText("\r\n", "3", "1", 0);
        mTtlFactory.PrintText("\r\n", "3", "1", 0);
        mTtlFactory.PrintText("\r\n_________________________________", "3", "1", 0);
    }
}
