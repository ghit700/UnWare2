package com.xmrbi.unware.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.blankj.utilcode.util.LogUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;

/**
 * Created by wzn on 2018/5/18.
 */

public class CameraUtils {
    private Camera mCamera;
    private SurfaceHolder mHolder;
    private boolean isTakePhoto = false;

    public CameraUtils(SurfaceView surfaceView) {
        mHolder = surfaceView.getHolder();
        mHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                initCamera();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                if (mCamera != null) {
                    mCamera.stopPreview();
                    mCamera.release();
                    mCamera = null;
                }
            }
        });
    }

    /**
     * 打开摄像头
     */
    private void initCamera() {
        int cameraCount = 0;
        android.hardware.Camera.CameraInfo cameraInfo = new android.hardware.Camera.CameraInfo();
        cameraCount = android.hardware.Camera.getNumberOfCameras();

        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            android.hardware.Camera.getCameraInfo(camIdx, cameraInfo);
            if (cameraInfo.facing == android.hardware.Camera.CameraInfo.CAMERA_FACING_BACK) {
                try {
                    mCamera = android.hardware.Camera.open(camIdx);
                } catch (RuntimeException e) {
                    LogUtils.e("Camera failed to open: " + e.toString());
                }
            }
        }
        try {
            if (mCamera != null) {
                mCamera.setPreviewDisplay(mHolder);
                //自动对焦
                mCamera.setAutoFocusMoveCallback(new Camera.AutoFocusMoveCallback() {
                    @Override
                    public void onAutoFocusMoving(boolean start, Camera camera) {

                    }
                });
            }

        } catch (IOException e) {
            e.printStackTrace();
            LogUtils.e("Camera failed to open: " + e.toString());

        }

    }

    /**
     * 拍摄图片
     *
     * @param filePath
     */
    public Observable<Boolean> takePicture(final String filePath) {
        if (mCamera != null) {
            //耗时操作
            return Observable.create(new ObservableOnSubscribe<Boolean>() {
                @Override
                public void subscribe(@NonNull final ObservableEmitter<Boolean> e)  {
                    if (!isTakePhoto) {
                        mCamera.startPreview();
                        isTakePhoto = true;
                        mCamera.takePicture(null, null, new Camera.PictureCallback() {
                            @Override
                            public void onPictureTaken(byte[] data, Camera camera) {
                                //获取图片
                                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                //创建并保存图片文件
                                File pictureFile = new File(filePath);
                                try {
                                    FileOutputStream fos = new FileOutputStream(pictureFile);
                                    //压缩50输出图片
                                    bitmap.compress(Bitmap.CompressFormat.PNG, 50, fos);
                                    fos.close();
                                } catch (Exception error) {
                                    error.printStackTrace();
                                    e.onNext(false);
                                }
                                mCamera.stopPreview();
                                isTakePhoto = false;
                                e.onNext(true);

                            }
                        });
                    } else {
                        e.onNext(false);
                    }
                }
            });
        }
        return Observable.just(false);


    }
}
