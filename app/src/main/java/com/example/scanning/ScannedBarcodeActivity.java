package com.example.scanning;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Bundle;
//import android.support.v4.app.ActivityCompat;
//import android.support.v7.app.AppCompatActivity;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.material.snackbar.Snackbar;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import DTO.DeviceInfo;
import DTO.DeviceInfoUtils;

public class ScannedBarcodeActivity extends AppCompatActivity {

    Set<String> scannedList = new HashSet<>();
    Set<DeviceInfo> matchScannedList = new HashSet<>();
    int matchScannedCount = 0;

    GmsBarcodeScanner scanner;
    private static final String TAG = "";
    SurfaceView surfaceView;
    TextView txtBarcodeValue;
    TextView txtBarcodeCountValue;
    DeviceInfoUtils dbUtil;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    Button btnAction;
    String intentData = "";
    boolean isEmail = false;


    ToneGenerator tGen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanned_barcode);
        dbUtil = new DeviceInfoUtils(this);
        dbUtil.openDB();
        tGen = new ToneGenerator(AudioManager.STREAM_SYSTEM, 100);
//        Intent intent = getIntent();
//        Toast.makeText(getApplicationContext(), String.valueOf(getDeviceList(intent.getStringExtra("path")).size()), Toast.LENGTH_SHORT).show();
        initViews();
    }

    private void initViews() {
        txtBarcodeValue = findViewById(R.id.txtBarcodeValue);
        txtBarcodeCountValue = findViewById(R.id.txtBarcodeCountValue);
        surfaceView = findViewById(R.id.surfaceView);
        btnAction = findViewById(R.id.btnAction);
//        btnAction.setOnClickListener(new View.OnClickListener(){;
//
//
//            @Override
//            public void onClick(View v) {
//                if (intentData.length() > 0) {
//                    if (isEmail)
//                        startActivity(new Intent(ScannedBarcodeActivity.this, MainActivity.class).putExtra("email_address", intentData));
//                    else {
//                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(intentData)));
//                    }
//                }
//            }
//        });
    }

    public void startCamera(){
        try {
            if (ActivityCompat.checkSelfPermission(ScannedBarcodeActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                cameraSource.start(surfaceView.getHolder());

            } else {
                ActivityCompat.requestPermissions(ScannedBarcodeActivity.this, new
                        String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void initialiseDetectorsAndSources() {
//        Intent i = new Intent();
//
//
//        Toast.makeText(getApplicationContext(), String.valueOf(getDeviceList(getIntent().getStringExtra("path")).size()), Toast.LENGTH_SHORT).show();
        Toast.makeText(getApplicationContext(), "Barcode scanner started", Toast.LENGTH_SHORT).show();
        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.AZTEC)
                .setBarcodeFormats(Barcode.DATA_MATRIX)
                .build();


        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true) //you should add this featur
                .build();


        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {



            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(ScannedBarcodeActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());

                    } else {
                        ActivityCompat.requestPermissions(ScannedBarcodeActivity.this, new
                                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });


        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                Toast.makeText(getApplicationContext(), "To prevent memory leaks barcode scanner has been stopped", Toast.LENGTH_SHORT).show();
            }


//          action khi detect được thông tin từ code
            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {
                    txtBarcodeValue.post(new Runnable() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void run() {
                            if (barcodes.valueAt(0).displayValue != null) {
                                txtBarcodeValue.removeCallbacks(null);
                                intentData = barcodes.valueAt(0).displayValue;
                                if (!intentData.equals("No Code Detected")){
                                    Log.i("B", intentData);
                                    cameraSource.stop();

                                    View view = findViewById(R.id.scanView);
                                    txtBarcodeValue.setText(intentData);

                                    boolean b = scannedList.add(intentData);

                                    Log.i("SSS", "add SetList "+b);
                                    if (!b){ //co roi
                                        tGen.startTone(ToneGenerator.TONE_CDMA_EMERGENCY_RINGBACK, 150);
                                        tGen.stopTone();
                                        txtBarcodeValue.setBackgroundColor(Color.parseColor("#BBAD32"));

                                        if (dbUtil.isMatch(intentData)){
                                            Snackbar.make(view, "MATCHED - Code DUPLICATED !", Snackbar.LENGTH_SHORT).show();
                                        } else {

                                            Snackbar.make(view, "Code DUPLICATED !", Snackbar.LENGTH_SHORT).show();
                                        }
//                                        Snackbar.make(view, "Code DUPLICATED !", Snackbar.LENGTH_SHORT).show();
                                        Handler handler = new Handler(Looper.getMainLooper());
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                startCamera();
                                            }
                                        }, 1000);

                                    } else { // Check match
                                        if (dbUtil.isMatch(intentData)){ //Match


                                            boolean v = checkMatchScannedDuplicate(intentData);
                                            Log.i("SSSScc", String.valueOf(v));
                                            if (!v) { // match va ko dup
                                                tGen.startTone(ToneGenerator.TONE_PROP_BEEP, 150);
                                                tGen.stopTone();
                                                matchScannedCount = matchScannedCount + 1;
                                                txtBarcodeCountValue.setText(String.valueOf(matchScannedCount) + " /" + String.valueOf(dbUtil.getTotalSet()));
                                                txtBarcodeValue.setBackgroundColor(Color.parseColor("#009688"));
                                                Snackbar.make(view, "MATCH", Snackbar.LENGTH_SHORT).show();
                                            } else {
                                                tGen.startTone(ToneGenerator.TONE_CDMA_EMERGENCY_RINGBACK, 150);
                                                tGen.stopTone();
                                                txtBarcodeValue.setBackgroundColor(Color.parseColor("#BBAD32"));
                                                Snackbar.make(view, "MATCHED - Code DUPLICATED !", Snackbar.LENGTH_SHORT).show();
                                            }


                                            Handler handler = new Handler(Looper.getMainLooper());
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    startCamera();
                                                }
                                            }, 1000);

                                        } else { // Not match
                                            tGen.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT, 150);
                                            tGen.stopTone();
                                            txtBarcodeValue.setBackgroundColor(Color.parseColor("#BD3535"));

                                            Snackbar.make(view, "NOT MATCH !", Snackbar.LENGTH_SHORT).show();
                                            Handler handler = new Handler(Looper.getMainLooper());
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    startCamera();
                                                }
                                            }, 1000);
                                        }
                                    }




                                }


//                                isEmail = true;
                                btnAction.setText("ADD CONTENT TO THE MAIL");

                            } else {
//                                isEmail = false;

                                btnAction.setText("LAUNCH URL");
                                intentData = barcodes.valueAt(0).displayValue;
                                txtBarcodeValue.setText(intentData);
                            }
                        }
                    });
                }
            }
        });
    }

    public boolean checkMatchScannedDuplicate(String intentData){
        String[] infoL = dbUtil.getInfoOfDev(intentData);
        DeviceInfo deviceInfo = new DeviceInfo("",infoL[0], infoL[1], infoL[2]); //DeviceInfo obj vua scan
        if (matchScannedList.size() == 0){ //TH chua co SET nao dc scan
            matchScannedList.add(deviceInfo);
            Log.i("S11", "size=0");
            Log.i("checkMatchScannedDuplicate", "false");
            return false; // ko duplicate
        } else {
            for (DeviceInfo d:matchScannedList) {
                Log.i("s11", "FOR");
                Log.i("s11", intentData);
                Log.i("s11", d.imei);
                Log.i("s11", d.seriNum);
                Log.i("s11", d.label);


                Log.i("checkMatchScannedDuplicate", "deviceInfo");
                Log.i("checkMatchScannedDuplicate", deviceInfo.label);
                Log.i("checkMatchScannedDuplicate", deviceInfo.imei);
                Log.i("checkMatchScannedDuplicate", deviceInfo.seriNum);

                Log.i("checkMatchScannedDuplicate", "d");
                Log.i("checkMatchScannedDuplicate", d.label);
                Log.i("checkMatchScannedDuplicate", d.imei);
                Log.i("checkMatchScannedDuplicate", d.seriNum);

                if (deviceInfo.imei.equals(d.imei)){
                    Log.i("checkMatchScannedDuplicate", "true");
                    return true;
                }

//                if((intentData.equals(d.imei)) || (intentData.equals(d.seriNum)) || (intentData.equals(d.label))){
//                    return true;
//                }
            }
        }
        matchScannedList.add(deviceInfo);
        Log.i("checkMatchScannedDuplicate", "false");
        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraSource.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initialiseDetectorsAndSources();
    }

    public void beep(){

        try {
            if (tGen == null) {
                tGen = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
            }
            tGen.startTone(ToneGenerator.TONE_PROP_BEEP, 150);
            tGen.release();
            Log.i("B", "StartTone");
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (tGen != null) {
                        tGen.release();
                        Log.i("B", "releaseTone");
                        tGen = null;
                    }
                }
            }, 150);
        } catch (Exception e) {
            android.util.Log.d(TAG, "Couldn't play sound:" + e.getMessage());
        }
    }

    public ArrayList<DeviceInfo> getDeviceList(String filePath){
        String line;
        ArrayList<DeviceInfo> deviceList = new ArrayList<>();
        DeviceInfo dev = null;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Level 23

                // Check if we have Call permission
                int permisson = ActivityCompat.checkSelfPermission(ScannedBarcodeActivity.this,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE);

                if (permisson != PackageManager.PERMISSION_GRANTED) {
                    // If don't have permission so prompt the user.
                    this.requestPermissions(
                            new String[]{
                                    android.Manifest.permission.READ_MEDIA_AUDIO,
                                    android.Manifest.permission.READ_MEDIA_IMAGES,
                                    Manifest.permission.READ_MEDIA_VIDEO},
                            1000);

                }
            }
            FileInputStream fileInputStream = new FileInputStream(new File(filePath));
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bfReader = new BufferedReader(inputStreamReader);
            StringBuffer deviceData = new StringBuffer();
            while (!((line = bfReader.readLine()) ==null)){
                if (!(line.trim().startsWith("Model"))){
                    deviceData.append(line + " ");

                }
                bfReader.close();
                inputStreamReader.close();
                fileInputStream.close();
            }

            for (String devL:deviceData.toString().split(" ")){
                String[] l = devL.split(",");
                String modelName = l[0];
                String imei = l[1];
                String seriNum = l[3];
                String label = l[2];
                dev = new DeviceInfo(modelName, imei, seriNum, label);
                deviceList.add(dev);
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return deviceList;
    }
}