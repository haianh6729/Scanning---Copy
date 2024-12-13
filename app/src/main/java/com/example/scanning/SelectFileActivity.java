package com.example.scanning;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import DTO.DeviceInfo;
import DTO.DeviceInfoUtils;
import DTO.FileUtils;

public class SelectFileActivity extends AppCompatActivity  {
    String path;
    Uri fileUri;
    private TextView tvShowInfo;
    private TextView tvTotalSet;
    private TextView tvTotalModel;
    private TextView tvModelNameList;
    private Button btnScan;
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;
    private static final int MY_REQUEST_CODE_PERMISSION = 1000;
    private static final int MY_RESULT_CODE_FILECHOOSER = 2000;

    private Button btnBrowse;
    private EditText editTextPath;
    DeviceInfoUtils dbUtil;
    private static final String LOG_TAG = "AndroidExample";


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_file);


        dbUtil = new DeviceInfoUtils(this);
        dbUtil.openDB();

        this.tvShowInfo = this.findViewById(R.id.button_showInfo);
        this.tvTotalSet = findViewById(R.id.totalSet);
        this.tvTotalModel = findViewById(R.id.totalModel);
        this.tvModelNameList = findViewById(R.id.modelNameList);
        this.editTextPath = findViewById(R.id.editText_path);
//        this.tvShowInfo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showInfo();
//            }
//        });
        try {
            String filePath = String.valueOf(this.editTextPath.getText());
            if (filePath.equals("")){
                this.tvShowInfo.setText(filePath);
                dbUtil.replaceTable();
                getDeviceList(fileUri);
                this.tvShowInfo.setText(filePath);
                Integer totalSet = dbUtil.getTotalSet();
                this.tvTotalSet.setText("SAMPLE TOTAL: "+String.valueOf(totalSet)+" ea");
                Integer totalModel = dbUtil.getTotalModel();
                this.tvTotalModel.setText("MODEL TOTAL: "+ String.valueOf(totalModel) + " ea");
            }
        }
        catch (NullPointerException e) {
            Toast.makeText(SelectFileActivity.this, "You need selecting a device list file.", Toast.LENGTH_SHORT).show();
            }

        this.btnBrowse = this.findViewById(R.id.button_browse);
        this.btnBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askPermissionAndBrowseFile();

            }
        });

        this.btnScan = this.findViewById(R.id.btnScan);
        this.btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((tvShowInfo.getText().toString().equals(""))){
                    Toast.makeText(SelectFileActivity.this, "You need selecting a device list file.", Toast.LENGTH_SHORT).show();
                }
                else {
//                    tvShowInfo.getText().toString();
//                    ArrayList<DeviceInfo> l = getDeviceList(tvShowInfo.getText().toString());
//                    Toast.makeText(SelectFileActivity.this, String.valueOf(l.size()), Toast.LENGTH_SHORT).show();
                    if (!tvTotalSet.getText().toString().equals("")) {
                        Intent openScanPreview = new Intent(SelectFileActivity.this, ScannedBarcodeActivity.class);
                        startActivity(openScanPreview);
                    } else {
                        Toast.makeText(SelectFileActivity.this, "Issue on your file !", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }



//    @Override
//    protected void onRestart() {
//        super.onRestart();
//        tvShowInfo.setText(tvShowInfo.getText().toString());
//        ArrayList<DeviceInfo> l = getDeviceList(tvShowInfo.getText().toString());
//        Toast.makeText(SelectFileActivity.this, String.valueOf(l.size()), Toast.LENGTH_SHORT).show();
//    }

    //    private void showInfo()  {
//        path = this.fragment.getPath();
//        Toast.makeText(this, "Path: " + path, Toast.LENGTH_LONG).show();
//    }
    public ArrayList<DeviceInfo> getDeviceList(Uri fileUri) {
        String line;
        ArrayList<DeviceInfo> deviceList = new ArrayList<>();


//        FileInputStream inputStream;
        InputStream inputStream;
        StringBuffer deviceData;
        try {
            Log.i("SSS","1");
//            inputStream = new FileInputStream(new File(filePath));
            inputStream = getContentResolver().openInputStream(fileUri);
            boolean isFirst = true;
            Log.i("SSS","1");
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            Log.i("SSS","1");
            BufferedReader bfReader = new BufferedReader(inputStreamReader);

            deviceData = new StringBuffer();
            Log.i("SSS","1");
            while (!((line = bfReader.readLine()) == null)) {
                if (isFirst) {
                    isFirst = false;
                    continue; // Skip the first line
                }
                if (!(line.startsWith("Model"))) {
                    Log.i("getDevList", line);
                    String[] l = line.split(",");
                    for (String e : l) {
                        System.out.println(e);
                    }

                    Log.i("l[0]", l[0]);
                    Log.i("l[1]", l[1]);
                    Log.i("l[2]", l[2]);


                    DeviceInfo dev = null;
                    if (l.length != 4) {
                        dev = new DeviceInfo(l[0], l[1], "", l[2]);
                    } else {
                        Log.i("l[3]", l[3]);
                        dev = new DeviceInfo(l[0], l[1], l[3], l[2]);
                    }
                    boolean a;
                    a = dbUtil.addDeviceInfo(dev);
                    Log.i("SSS", String.valueOf(a));
                }

            }

            bfReader.close();
            inputStreamReader.close();
            inputStream.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


//        for (String devL : deviceData.toString().split(" ")) {
//            String[] l = devL.split(",");
//            String modelName = l[0];
//            String imei = l[1];
//            tvShowInfo.setText(imei);
//            String seriNum = l[3];
//            dev = new DeviceInfo(modelName, imei, seriNum);
//            deviceList.add(dev);
//        }


        return deviceList;
    }

    private void askPermissionAndBrowseFile()  {
        // With Android Level >= 23, you have to ask the user
        // for permission to access External Storage.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Level 23

            // Check if we have Call permission
            int permisson = ActivityCompat.checkSelfPermission(SelectFileActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE);
            Log.i("Frag", "checkPermission");

            if (permisson != PackageManager.PERMISSION_GRANTED) {
                // If don't have permission so prompt the user.
                this.requestPermissions(
                        new String[]{
                                Manifest.permission.READ_MEDIA_AUDIO,
                                Manifest.permission.READ_MEDIA_IMAGES,
                                Manifest.permission.READ_MEDIA_VIDEO},
                        MY_REQUEST_CODE_PERMISSION);
                Log.i("Frag", "cap permiss");
                return;
            }
        }
        this.doBrowseFile();
        Log.i("Frag", "doBrowse");
    }

    private void  doBrowseFile()  {
        Intent chooseFileIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        chooseFileIntent.setType("text/plain");
        // Only return URIs that can be opened with ContentResolver
        chooseFileIntent.addCategory(Intent.CATEGORY_OPENABLE);

        chooseFileIntent = Intent.createChooser(chooseFileIntent, "Choose a file");
        startActivityForResult(chooseFileIntent, MY_RESULT_CODE_FILECHOOSER);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case MY_RESULT_CODE_FILECHOOSER:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        fileUri = data.getData();
                        Log.i(LOG_TAG, "Uri: " + fileUri);

                        String filePath = null;
                        try {
                            filePath = FileUtils.getPath(SelectFileActivity.this, fileUri);
                            Log.i("Frag", filePath);
                        } catch (Exception e) {
                            Log.e(LOG_TAG, "Error: " + e);
                            Toast.makeText(SelectFileActivity.this, "Error: " + filePath, Toast.LENGTH_SHORT).show();
                        }
                        dbUtil.replaceTable();
                        this.editTextPath.setText(filePath);
                        this.tvShowInfo.setText(filePath);
                        getDeviceList(fileUri);
                        this.tvShowInfo.setText(filePath);
                        Integer totalSet = dbUtil.getTotalSet();
                        this.tvTotalSet.setText("SAMPLE TOTAL: "+String.valueOf(totalSet)+" ea");
                        Integer totalModel = dbUtil.getTotalModel();
                        this.tvTotalModel.setText("MODEL TOTAL: "+ String.valueOf(totalModel) + " ea");
                        String modelNameList = dbUtil.getModelNameListString();
                        this.tvModelNameList.setText(modelNameList);


                    }
                }
        }
    }



    // When you have the request results
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //
        switch (requestCode) {
            case MY_REQUEST_CODE_PERMISSION: {

                // Note: If request is cancelled, the result arrays are empty.
                // Permissions granted (CALL_PHONE).
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.i( LOG_TAG,"Permission granted!");
                    Toast.makeText(SelectFileActivity.this, "Permission granted!", Toast.LENGTH_SHORT).show();

                    this.doBrowseFile();
                }
                // Cancelled or denied.
                else {
                    Log.i(LOG_TAG,"Permission denied!");
                    Toast.makeText(SelectFileActivity.this, "Permission denied!", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

//    @Override
//    public void onFileSelected(Uri fileUri) {
//        Log.i("SEL", String.valueOf(fileUri));
//        ArrayList<DeviceInfo> l = getDeviceList(fileUri);
//        Toast.makeText(SelectFileActivity.this, String.valueOf(l.size()), Toast.LENGTH_SHORT).show();
//    }
}