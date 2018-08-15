package com.xmrbi.unware.utils;

import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.utilcode.util.StringUtils;
import com.xmrbi.unware.R;

/**
 * Created by wzn on 2018/5/24.
 */
public class DialogUtils {

    public static MaterialDialog alert(Context context, String content, MaterialDialog.SingleButtonCallback callback) {
        return alert(context, null, content, null,null,callback);
    }
    public static MaterialDialog alert(Context context, String content) {
        return alert(context, null, content, null,null,null);
    }
    public static MaterialDialog alert(Context context,String title ,String content, MaterialDialog.SingleButtonCallback callback) {
        return alert(context, title, content, null,null,callback);
    }



    /**
     * 提示框（确认，取消）
     *
     * @param context
     * @param title
     * @param content
     * @param callback
     * @return
     */
    public static MaterialDialog alert(Context context, String title, String content, String positiveText, String negativeText, MaterialDialog.SingleButtonCallback callback) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
        if (callback != null) {
            builder.onPositive(callback);
        }
        if (!StringUtils.isEmpty(title)) {
            builder.title(title);
        }
        if (!StringUtils.isEmpty(positiveText)) {
            builder.positiveText(positiveText);
        } else {
            builder.positiveText(R.string.setting_agree);
        }
        if (!StringUtils.isEmpty(negativeText)) {
            builder.negativeText(negativeText);
        } else {
            builder.negativeText(R.string.main_cancel);
        }

        if (!StringUtils.isEmpty(content)) {
            builder.title(content);
        }
        return builder.build();
    }
    public static MaterialDialog alert(Context context, String title, String content, String positiveText, String negativeText, MaterialDialog.SingleButtonCallback callback,MaterialDialog.SingleButtonCallback callback1) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
        if (callback != null) {
            builder.onPositive(callback);
        }
        if (callback != null) {
            builder.onNegative(callback1);
        }
        if (!StringUtils.isEmpty(title)) {
            builder.title(title);
        }
        if (!StringUtils.isEmpty(positiveText)) {
            builder.positiveText(positiveText);
        } else {
            builder.positiveText(R.string.setting_agree);
        }
        if (!StringUtils.isEmpty(negativeText)) {
            builder.negativeText(negativeText);
        } else {
            builder.negativeText(R.string.main_cancel);
        }

        if (!StringUtils.isEmpty(content)) {
            builder.title(content);
        }
        return builder.build();
    }


    public static MaterialDialog progress(Context context, String content) {
        return progress(context, null, content);
    }


    /**
     * 显示圆圈的载入中
     */
    public static MaterialDialog progress(Context context, String title, String content) {
        MaterialDialog.Builder builder =
                new MaterialDialog.Builder(context)
                        .progress(true, 0)
                        .progressIndeterminateStyle(false);
        if (!StringUtils.isEmpty(title)) {
            builder.title(title);
        }
        if (!StringUtils.isEmpty(content)) {
            builder.title(content);
        }
        return builder.build();
    }
}
