package com.dapi.suse.scannercreterlib;

import android.app.Activity;
import android.os.Vibrator;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zbar.ZBarView;

public abstract class AbsScanActivity extends Activity {

    ZBarView zBarView;
    FrameLayout contentFrameLayout;// 展示其他布局的时候使用

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.abs_activity_layout);
        zBarView = (ZBarView) findViewById(R.id.zbv_content);
        zBarView.setDelegate(new QRCodeView.Delegate() {
            @Override
            public void onScanQRCodeSuccess(String result) {
                onSuccess(result);
                try{
                    Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                    vibrator.vibrate(200);
                }catch (Exception e){}
            }

            @Override
            public void onScanQRCodeOpenCameraError() {
                onError();
            }
        });

        contentFrameLayout = (FrameLayout) findViewById(R.id.fl_content);
        View contentView = createContentView();
        if(contentView != null && contentFrameLayout != null){
            contentFrameLayout.addView(contentView);
        }
    } //end m

    @Override
    protected void onStart() {
        super.onStart();
        zBarView.startCamera();
        zBarView.showScanRect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        zBarView.stopCamera();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        zBarView.onDestroy();
    }

    /**
     *
     * 开始扫描
     *
     */
    public void startScan(){
        if(zBarView == null){
            return;
        }
        zBarView.startSpot();
    }


    /**
     *
     * 结束扫描
     *
     */
    public void stopScan(){
        if(zBarView == null){
            return;
        }
        zBarView.stopSpot();
    }

    private boolean flashIsOpen = false;//闪光灯是否开启

    /**
     *
     * 闪光灯是否开启
     *
     * @return
     */
    public boolean isFlashIsOpen() {
        return flashIsOpen;
    }

    /**
     *
     * 切换闪光灯
     *
     */
    public void toggleFlash(){
        try {
            if(flashIsOpen){
                zBarView.openFlashlight();
                flashIsOpen = true;
            }else{
                zBarView.closeFlashlight();
                flashIsOpen = false;
            }
        }catch (Exception e){}
    } // end m


    /**
     *
     * 相机打开失败
     *
     */
    public abstract void onError();

    /**
     *
     * 扫描成功
     *
     * @param message
     */
    public abstract void onSuccess(String message);


    public View createContentView(){
        return null;
    }

}
