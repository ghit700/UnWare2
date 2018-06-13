package com.xmrbi.unware.component.http;

import android.content.Context;

import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.xmrbi.unware.R;

import org.greenrobot.greendao.annotation.NotNull;


import io.reactivex.annotations.NonNull;

/**
 * 统一返回格式的统一判断（{"success":false,"data":"毫无数据"}）
 * Created by wzn on 2018/4/17.
 */

public abstract class ResponseObserver<T> extends BaseObserver<Response> {
    public ResponseObserver() {
    }

    public ResponseObserver(Context context) {
        super(context);
    }

    public ResponseObserver(Context mContext, boolean isShowErrorToast, boolean isShowDialog) {
        super(mContext, isShowErrorToast, isShowDialog);
    }

    @Override
    public void onNext(@NonNull Response response) {
        super.onNext(response);
        if (!response.isSuccess()) {
            if(isShowErrorToast){
                if (StringUtils.isEmpty(response.getErrorMsg())) {
                    ToastUtils.showLong(R.string.default_error_request);
                } else {
                    ToastUtils.showLong(response.getErrorMsg());
                }
            }
            handleErrorData();
        } else {
            handleData((T) response.getData());
        }
    }

    @Override
    protected void onError(ExceptionHandle.ResponeThrowable e) {
        super.onError(e);
        handleErrorData();
    }

    protected void handleErrorData() {

    }

    public abstract void handleData(@NotNull T data);
}
