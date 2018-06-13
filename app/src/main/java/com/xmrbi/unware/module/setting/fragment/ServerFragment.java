package com.xmrbi.unware.module.setting.fragment;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.xmrbi.unware.R;
import com.xmrbi.unware.base.BaseActivity;
import com.xmrbi.unware.base.BaseFragment;
import com.xmrbi.unware.base.Config;
import com.xmrbi.unware.component.http.BaseObserver;
import com.xmrbi.unware.component.http.HttpUtils;
import com.xmrbi.unware.component.http.IOTransformer;
import com.xmrbi.unware.component.http.Response;
import com.xmrbi.unware.component.http.ResponseObserver;
import com.xmrbi.unware.data.entity.main.StoreHouse;
import com.xmrbi.unware.data.entity.main.StoreHouseAioConfig;
import com.xmrbi.unware.data.entity.main.Useunit;
import com.xmrbi.unware.data.local.MainLocalSource;
import com.xmrbi.unware.data.repository.MainRepository;
import com.xmrbi.unware.event.SettingEvent;
import com.xmrbi.unware.module.main.activity.MainActivity;
import com.xmrbi.unware.utils.RxBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;

/**
 * Created by wzn on 2018/5/4.
 */

public class ServerFragment extends BaseFragment {

    @BindView(R.id.etSettingWareHouseIp)
    EditText etSettingWareHouseIp;
    @BindView(R.id.etSettingGmmsIp)
    EditText etSettingGmmsIp;
    @BindView(R.id.tvSettingLessess)
    TextView tvSettingLessess;
    @BindView(R.id.tvSettingStoreHouse)
    TextView tvSettingStoreHouse;
    @BindView(R.id.btnSettingConnectService)
    Button btnSettingConnectService;
    @BindView(R.id.btnSettingExit)
    ImageView btnSettingExit;
    @BindView(R.id.btnSettingSave)
    Button btnSettingSave;
    @BindView(R.id.etSettingRfidIp)
    EditText etSettingRfidIp;
    @BindView(R.id.etSettingRfidPort)
    EditText etSettingRfidPort;
    @BindView(R.id.etSettingNotePrintSerialAddress)
    EditText etSettingNotePrintSerialAddress;
    @BindView(R.id.etSettingNotePrintBaudRate)
    EditText etSettingNotePrintBaudRate;
    @BindView(R.id.etSettingChannel)
    EditText etSettingChannel;
    @BindView(R.id.etSettingCodePrintBaudRate)
    EditText etSettingCodePrintBaudRate;
    @BindView(R.id.etSettingCodePrintSerialAddress)
    EditText etSettingCodePrintSerialAddress;

    private MainRepository mMainRepository;
    private MainLocalSource mMainLocalSource;
    private MaterialDialog mlstLessessDialog;
    private MaterialDialog mlstStoreHouseDialog;
    private List<Useunit> mLstUseunits;
    private List<StoreHouse> mLstStoreHouses;
    private Useunit mUseunit;
    private StoreHouse mStoreHouse;


    public static ServerFragment newInstance() {
        ServerFragment fragment = new ServerFragment();
        return fragment;
    }

    @Override
    protected int getLayout() {
        return R.layout.setting_fragment_server;
    }

    @Override
    protected void onViewCreated() {
        etSettingGmmsIp.setText(Config.Http.SERVER_GMMS);
        etSettingWareHouseIp.setText(Config.Http.SERVER_IP);
    }

    @Override
    protected void initData() {
        mLstUseunits = new ArrayList<>();
        mLstStoreHouses = new ArrayList<>();
        mMainLocalSource = new MainLocalSource();
        if (!SPUtils.getInstance(Config.SP.SP_NAME).getBoolean(Config.SP.SP_IS_SETTING)) {
            mobileLesseeIdStoreHouse();
        } else {
            mStoreHouse = mMainLocalSource.getStoreHouse();
            mUseunit = mMainLocalSource.getUseunit(mStoreHouse.getUseunitId());
            tvSettingStoreHouse.setText(mStoreHouse.getName());
            tvSettingLessess.setText(mUseunit.getName());
            mobileLesseeIdStoreHouse();
            mobileQueryAioConfig();
        }
    }

    /**
     * 连接服务器获取仓库和租户数据
     */
    @OnClick(R.id.btnSettingConnectService)
    public void mobileLesseeIdStoreHouse() {
        Config.Http.SERVER_IP = etSettingWareHouseIp.getText().toString();
        mMainRepository = new MainRepository((BaseActivity) getActivity());
        mMainRepository.mobileLesseeIdStoreHouse()
                .subscribe(new ResponseObserver<List<Useunit>>(getBaseActivity()) {
                    @Override
                    public void handleData(List<Useunit> data) {
                        mLstUseunits.clear();
                        if (mUseunit != null) {
                            for (Useunit u : data) {
                                if (u.getId().equals(mUseunit.getId())) {
                                    mUseunit = u;
                                    mLstStoreHouses.addAll(mUseunit.getStoreHouses());
                                    break;
                                }
                            }
                        }
                        mLstUseunits.addAll(data);
                    }
                });
    }

    /**
     * 保存仓库设置
     */
    @OnClick(R.id.btnSettingSave)
    public void saveStoreHouseSetting() {
        Map<String, String> params = new HashMap<>();
        params.put("lesseeId", mUseunit.getLesseeId().toString());
        params.put("storeHouseId", mStoreHouse.getId().toString());
        params.put("rfidIp", etSettingRfidIp.getText().toString().trim());
        params.put("rfidPort", etSettingRfidPort.getText().toString().trim());
        params.put("printSerialName", etSettingNotePrintSerialAddress.getText().toString().trim());
        params.put("printBautRate", etSettingNotePrintBaudRate.getText().toString().trim());
        params.put("codeSerialName", etSettingCodePrintSerialAddress.getText().toString().trim());
        params.put("codeBautRate", etSettingCodePrintBaudRate.getText().toString());

        mMainRepository.mobileSaveAioConfig(params)
                .subscribe(new BaseObserver<String>(getBaseActivity(), true) {
                    @Override
                    public void onNext(String s) {
                        if (s.contains("true")) {
                            SPUtils.getInstance(Config.SP.SP_NAME).put(Config.SP.SP_SERVER_IP, etSettingWareHouseIp.getText().toString().trim());
                            SPUtils.getInstance(Config.SP.SP_NAME).put(Config.SP.SP_SERVER_GMMS_IP, etSettingGmmsIp.getText().toString().trim());
                            SPUtils.getInstance(Config.SP.SP_NAME).put(Config.SP.SP_CHANNEL, etSettingChannel.getText().toString().trim());
                            HttpUtils.resetServerAddress();
                            mMainLocalSource.saveStoreHouse(mStoreHouse);
                            mMainLocalSource.saveUseunit(mUseunit);
                            SPUtils.getInstance(Config.SP.SP_NAME).put(Config.SP.SP_IS_SETTING, true);
                            RxBus.getDefault().post(new SettingEvent());
                            getBaseActivity().finish();
                            getBaseActivity().lauch(MainActivity.class);
                        } else {
                            ToastUtils.showLong("上传仓库设备配置失败！");
                        }
                    }
                });

    }


    /**
     * 退出设置界面
     */
    @OnClick(R.id.btnSettingExit)
    public void exit() {
        if (!SPUtils.getInstance(Config.SP.SP_NAME).getBoolean(Config.SP.SP_IS_SETTING)) {
            new MaterialDialog.Builder(getBaseActivity())
                    .title(R.string.setting_dailog_title)
                    .content(R.string.setting_warning, true)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            getBaseActivity().finish();
                        }
                    })
                    .positiveText(R.string.setting_agree)
                    .negativeText(R.string.main_cancel)
                    .show();
        } else {
            getBaseActivity().finish();
        }
    }


    /**
     * 显示租户选择列表对话框
     */
    @OnClick(R.id.tvSettingLessess)
    public void showLessessDialog() {
        if (mlstLessessDialog == null) {
            mlstLessessDialog = new MaterialDialog.Builder(getActivity())
                    .title("租户")
                    .itemsCallback(new MaterialDialog.ListCallback() {
                        @Override
                        public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                            mUseunit = mLstUseunits.get(position);
                            tvSettingLessess.setText(text);
                            mLstStoreHouses.clear();
                            if (mUseunit.getStoreHouses() != null && !mUseunit.getStoreHouses().isEmpty()) {
                                mLstStoreHouses.addAll(mUseunit.getStoreHouses());
                            }
                        }
                    })
                    .positiveText(R.string.main_cancel)
                    .items(mLstUseunits)
                    .build();
        } else {
            mlstLessessDialog.getBuilder().items(mLstUseunits);
        }

        mlstLessessDialog.show();
    }

    /**
     * 显示仓库选择列表对话框
     */
    @OnClick(R.id.tvSettingStoreHouse)
    public void showStoreHouseDialog() {
        if (mlstStoreHouseDialog == null) {
            mlstStoreHouseDialog = new MaterialDialog.Builder(getActivity())
                    .title("仓库")
                    .itemsCallback(new MaterialDialog.ListCallback() {
                        @Override
                        public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                            mStoreHouse = mLstStoreHouses.get(position);
                            tvSettingStoreHouse.setText(text);
                            mobileQueryAioConfig();
                        }
                    })
                    .positiveText(R.string.main_cancel)
                    .items(mLstStoreHouses)
                    .build();
        } else {
            mlstStoreHouseDialog.getBuilder().items(mLstStoreHouses);
        }

        mlstStoreHouseDialog.show();
    }

    /**
     * 获取仓库配置
     */
    private void mobileQueryAioConfig() {
        mMainRepository.mobileQueryAioConfig(mUseunit.getLesseeId(), mStoreHouse.getId())
                .compose(new IOTransformer<Response<List<StoreHouseAioConfig>>>((BaseActivity) getActivity()))
                .subscribe(new ResponseObserver<List<StoreHouseAioConfig>>(getBaseActivity()) {
                    @Override
                    public void handleData(List<StoreHouseAioConfig> data) {
                        if (!data.isEmpty()) {
                            StoreHouseAioConfig config = data.get(0);
                            etSettingRfidIp.setText(config.getRfidIp());
                            etSettingRfidPort.setText(config.getRfidPort().toString());
                            etSettingNotePrintSerialAddress.setText(config.getPrintSerialName());
                            etSettingNotePrintBaudRate.setText(config.getPrintBautRate());
                            etSettingCodePrintBaudRate.setText(config.getCodeBautRate());
                            etSettingCodePrintSerialAddress.setText(config.getCodeSerialName());
                        }
                    }
                });

    }

}
