package com.example.agoracard;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanCodeActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    ZXingScannerView ScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScannerView = new ZXingScannerView(this);
        setContentView(ScannerView);

    }

    @Override
    public void handleResult(Result result) {
        //Uebergibt den String aus dem QR-Code in die MainActivity
        MainActivity.resultQR = result.toString();
        onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ScannerView.stopCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ScannerView.setResultHandler(this);
        ScannerView.startCamera();
    }
}
