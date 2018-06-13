package com.xmrbi.unware.module.search.activity;

import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.blankj.utilcode.util.ToastUtils;
import com.xmrbi.unware.R;
import com.xmrbi.unware.base.BaseActivity;
import com.xmrbi.unware.utils.MediaUtils;

import butterknife.BindView;

/**
 * 条形码搜索界面
 * Created by wzn on 2018/6/11.
 */
public class BarcodeScanActivity extends BaseActivity {
    @BindView(R.id.etSearchBarcodeContent)
    EditText etSearchBarcodeContent;

    @Override
    protected int getLayout() {
        return R.layout.search_activity_barcode_scan;
    }

    @Override
    protected void onViewCreated() {
        etSearchBarcodeContent.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    ToastUtils.showLong(etSearchBarcodeContent.getText().toString());
                }
                return false;
            }
        });
    }

    @Override
    protected void initEventAndData() {
        MediaUtils.getInstance().play(R.string.query_barcode_scan);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //如果跳转别的页面，声音还没播放完，停止播放
        MediaUtils.getInstance().stop();
    }

}
