package com.example.scanning;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import DTO.DatabaseHandler;
import DTO.DeviceInfoUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button btnFullScan, btnModelScan, btnLend;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        btnFullScan = findViewById(R.id.btnFullScan);
        btnFullScan.setOnClickListener(this);

        btnModelScan = findViewById(R.id.btnModelScan);
        btnModelScan.setOnClickListener(this);

        btnLend = findViewById(R.id.btnLend);
        btnLend.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i==R.id.btnFullScan){
            Intent openFileChooserActivity = new Intent(MainActivity.this, SelectFileActivity.class);
            startActivity(openFileChooserActivity);
        } else if (i==R.id.btnLend) {
            Intent openScanActivity = new Intent(MainActivity.this, ScannedBarcodeActivity.class);
            startActivity(openScanActivity);
        } else if (i==R.id.btnModelScan) {
            Intent openFileChooserModelActivity = new Intent(MainActivity.this, SelectFileActivityForModelScan.class);
            startActivity(openFileChooserModelActivity);
        }
    }
}