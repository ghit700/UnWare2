package com.xmrbi.unware.module.search.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.blankj.utilcode.util.ToastUtils;
import com.xmrbi.unware.R;
import com.xmrbi.unware.base.BaseActivity;
import com.xmrbi.unware.component.http.ResponseObserver;
import com.xmrbi.unware.data.entity.deliver.Device;
import com.xmrbi.unware.data.entity.main.StoreHouse;
import com.xmrbi.unware.data.entity.pick.PickListDetail;
import com.xmrbi.unware.data.local.MainLocalSource;
import com.xmrbi.unware.data.repository.DeliverRepository;
import com.xmrbi.unware.data.repository.PickRepository;
import com.xmrbi.unware.utils.ActivityStackUtils;
import com.xmrbi.unware.utils.MediaUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * 条形码搜索界面
 * Created by wzn on 2018/6/11.
 */
public class BarcodeScanActivity extends BaseActivity {
    public static void lauch(Context context,Long userId){
        Bundle bundle=new Bundle();
        bundle.putLong("userId",userId);
        ActivityStackUtils.lauch(context,BarcodeScanActivity.class,bundle);
    }

    @BindView(R.id.etSearchBarcodeContent)
    EditText etSearchBarcodeContent;
    @BindView(R.id.btnBarcodeDetailExit)
    Button btnBarcodeDetailExit;

    private PickRepository mPickRepository;
    private DeliverRepository mDeliverRepository;
    private MainLocalSource mMainLocalSource;
    private Long mUserId;
    private StoreHouse mStoreHouse;
    private boolean isLauch=false;
    @Override
    protected int getLayout() {
        return R.layout.search_activity_barcode_scan;
    }

    @Override
    protected void onViewCreated() {
        etSearchBarcodeContent.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if(isLauch){
                    return true;
                }
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    String text=etSearchBarcodeContent.getText().toString().trim();
                    if(text.contains("pick")){
                        isLauch=true;
                        text=text.replace("pick","");
                        final String finalText = text;
                        mPickRepository.downloadPickListDetail(mUserId,mStoreHouse.getId(),Long.parseLong(text))
                                .subscribe(new ResponseObserver<List<PickListDetail>>() {
                                    @Override
                                    public void handleData(List<PickListDetail> data) {
                                        BarcodeDetailActivity.lauch(mContext, finalText,BarcodeDetailActivity.TYPE_PICK,mUserId);
                                        finish();
                                    }
                                });
                    }else{
                        isLauch=true;
                        final String finalText1 = text;
                        mDeliverRepository.downloadDeliverGoods(text,mStoreHouse.getId())
                        .subscribe(new ResponseObserver<List<Device>>() {
                            @Override
                            public void handleData(List<Device> data) {
                                BarcodeDetailActivity.lauch(mContext, finalText1,BarcodeDetailActivity.TYPE_DELIVER,mUserId);
                                finish();
                            }
                        });
                    }
                    text="";
                }
                return false;
            }
        });
    }

    @Override
    protected void initEventAndData() {
        mUserId=mBundle.getLong("userId");
        mMainLocalSource=new MainLocalSource();
        mPickRepository=new PickRepository(this);
        mDeliverRepository=new DeliverRepository(this);
        mStoreHouse=mMainLocalSource.getStoreHouse();
        Disposable d=Observable.interval(0,10, TimeUnit.SECONDS)
                .compose(this.<Long>bindToLifecycle())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        MediaUtils.getInstance().play(R.string.query_barcode_scan);
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        //如果跳转别的页面，声音还没播放完，停止播放
        MediaUtils.getInstance().stop();
    }

    @OnClick(R.id.btnBarcodeDetailExit)
    public void exit(){
        onBackPressed();
    }

}
