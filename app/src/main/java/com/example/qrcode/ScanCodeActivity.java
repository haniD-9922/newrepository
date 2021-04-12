package com.example.qrcode;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import org.w3c.dom.Text;

import java.io.IOException;

public class ScanCodeActivity extends AppCompatActivity {
    SurfaceView surfaceView;
    TextView textView;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    public static final int REQUEST_PERMISSION = 423;
    Button btnaction;
    String intendData = "";
    String permissions[] = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_code);
        btnaction = findViewById(R.id.capture);
        textView = findViewById(R.id.showsite);
        surfaceView = findViewById(R.id.surfaceViewid);
        initialiseDetectorAndSources();
        btnaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (intendData.length() > 0) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(intendData)));
                }
            }
        });

    }

    private void initialiseDetectorAndSources() {
        Toast.makeText(this, "Scanner Started", Toast.LENGTH_SHORT).show();
        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();
        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true)
                .build();
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                try {
                    if(Build.VERSION.SDK_INT>Build.VERSION_CODES.M){
                        if(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                            cameraSource.start(surfaceView.getHolder());
                    }
                    else{
                        requestPermissions(permissions,REQUEST_PERMISSION);
                    }
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
            cameraSource.stop();
            }
        });
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                Toast.makeText(ScanCodeActivity.this, "Scanner Stopped", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void receiveDetections(@NonNull Detector.Detections<Barcode> detections) {
        final SparseArray<Barcode> barcode=detections.getDetectedItems();
        if(barcode.size()!=0){
            textView.post(new Runnable() {
                @Override
                public void run() {
                    btnaction.setText("Launch URL");
                    intendData=barcode.valueAt(0).displayValue;
                    textView.setText(intendData);
                }
            });
        }
        else{
            Toast.makeText(ScanCodeActivity.this, "Empty in Size", Toast.LENGTH_SHORT).show();
        }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraSource.release();
    }
}