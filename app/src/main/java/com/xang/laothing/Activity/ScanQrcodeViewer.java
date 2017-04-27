package com.xang.laothing.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.util.ArrayList;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by xang on 25/04/2017.
 */

public class ScanQrcodeViewer extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    public static final String QR_CODE_VALUE = "qrcode";
    private ZXingScannerView mScannerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScannerView = new ZXingScannerView(this);  // Programmatically initialize the scanner view
        setContentView(mScannerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.setAutoFocus(true);
        List<BarcodeFormat> formate = new ArrayList<>();
        formate.add(BarcodeFormat.QR_CODE);
        mScannerView.setFormats(formate);
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }


    @Override
    public void handleResult(Result result) {
        Intent data = new Intent();
        data.putExtra(QR_CODE_VALUE,result.getText());
        setResult(RESULT_OK,data);
        finish();
    }


}
