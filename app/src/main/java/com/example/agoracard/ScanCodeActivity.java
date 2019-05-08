package com.example.agoracard;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanCodeActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    ZXingScannerView ScannerView;

    private final String TAG = "Debug_MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScannerView = new ZXingScannerView(this);
        setContentView(ScannerView);
    }

    @Override
    protected void onResume(){
        super.onResume();

        checkCameraPermission();
    }


    private final int MY_PERMISSIONS_REQUEST_USE_CAMERA = 0x00AF;
    private void checkCameraPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA ) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG,"Permission not available requesting permission");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_USE_CAMERA);
        } else {
            Log.d(TAG,"Permission has already granted");
            ScannerView.setResultHandler(this);
            ScannerView.startCamera();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_USE_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG,"permission was granted! Do your stuff");
                    ScannerView.setResultHandler(this);
                    ScannerView.startCamera();
                } else {
                    Log.d(TAG,"permission denied! Disable the function related with permission.");
                }
                return;
            }
        }
    }

    @Override
    public void handleResult(Result result) {
        //Uebergibt den String aus dem QR-Code in die MainActivity
        MainActivity.resultQR = result.toString();
        onBackPressed();
    }


}
