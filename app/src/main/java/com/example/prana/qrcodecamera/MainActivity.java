package com.example.prana.qrcodecamera;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    SurfaceView sv;
    TextView tv;
    BarcodeDetector bd;
    CameraSource cs;
   final int requst = 1001;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case requst: {

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    try {
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                            return;
                        }
                        cs.start(sv.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
            break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sv = (SurfaceView) findViewById(R.id.camera);
        tv = (TextView) findViewById(R.id.result);

        bd = new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.QR_CODE).build();

        cs = new CameraSource.Builder(this, bd).setRequestedPreviewSize(640, 480).build();

        //Add Event
        sv.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    //Request Permission
                    ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.CAMERA},requst);
                    return;
                }
                try {
                    cs.start(sv.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

                cs.stop();

            }
        });

        bd.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {

                final SparseArray<Barcode> sa = detections.getDetectedItems();
                if (sa.size() != 0)
                {
                    tv.post(new Runnable() {
                        @Override
                        public void run() {

                            //Create Vibrator
                            Vibrator v = (Vibrator)getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                            v.vibrate(1000);
                            tv.setText(sa.valueAt(0).displayValue);
                        }
                    });
                }
            }
        });
    }
}
