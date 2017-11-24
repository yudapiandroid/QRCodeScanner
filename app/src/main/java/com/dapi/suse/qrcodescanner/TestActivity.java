package com.dapi.suse.qrcodescanner;

import android.widget.Toast;

import com.dapi.suse.scannercreterlib.AbsScanActivity;

/**
 * Created by Administrator on 2017/11/24.
 */

public class TestActivity extends AbsScanActivity{
    @Override
    public void onError() {

    }

    @Override
    public void onSuccess(String message) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        startScan();
    }
}
