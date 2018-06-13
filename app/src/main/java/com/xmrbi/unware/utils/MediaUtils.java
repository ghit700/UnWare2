package com.xmrbi.unware.utils;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.MediaPlayer;

import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.xmrbi.unware.base.BaseActivity;
import com.xmrbi.unware.component.http.IOTransformer;

import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Consumer;


/**
 * Created by wzn on 2018/5/16.
 */
public class MediaUtils {
    private static MediaUtils instance;
    private int mMaxVolume;
    private MediaPlayer mPlayer;
    private AssetManager mAssetManager;

    public static MediaUtils getInstance() {
        if (instance == null) {
            instance = new MediaUtils();
        }
        return instance;
    }


    public MediaUtils() {
        AudioManager audioMgr = (AudioManager) Utils.getApp().getSystemService(Context.AUDIO_SERVICE);
        mMaxVolume = audioMgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mAssetManager = Utils.getApp().getAssets();
    }

    public void play(int resId) {
        play(Utils.getApp().getString(resId));
    }

    /**
     * 播放语音
     *
     * @param fileName
     */
    public void play(final String fileName) {
//        //在oi线程中播放音乐
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> event) {
                try {
                    mPlayer = new MediaPlayer();
                    AssetFileDescriptor fileDescriptor = mAssetManager.openFd(fileName);
                    mPlayer.setDataSource(fileDescriptor.getFileDescriptor(),
                            fileDescriptor.getStartOffset(),
                            fileDescriptor.getStartOffset());
                    mPlayer.setVolume(mMaxVolume, mMaxVolume);
                    mPlayer.prepare();
                    mPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                event.onComplete();
            }
        })
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {

                    }
                });
    }

    public void stop() {
        mPlayer.stop();
    }


}
