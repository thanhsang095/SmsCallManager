package com.nus.SmsCallManager.View;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.nus.SmsCallManager.Services.SmsCallService;
import com.nus.SmsCallManager.R;
import com.nus.SmsCallManager.Utils.Constants;

public class MainActivity extends AppCompatActivity {
    private static final int ASK_MULTIPLE_PERMISSION_REQUEST_CODE = 1;
    private Button btnConfirm;
    private EditText edtPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        init();

        requestMultiplePermission();

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case ASK_MULTIPLE_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startService(new Intent(this, SmsCallService.class));
                } else {
                    Log.d(Constants.TAG, "Read call/phone  permissions denied!");
                }
            }
        }
        return;
    }


    /**
     * Request permission react call log and phone state
     */
    public void requestMultiplePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG)
                + ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                + ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CALL_LOG)
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{
                                Manifest.permission.READ_CALL_LOG,
                                Manifest.permission.READ_PHONE_STATE,
                                Manifest.permission.RECEIVE_SMS
                        },
                        ASK_MULTIPLE_PERMISSION_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{
                                Manifest.permission.READ_CALL_LOG,
                                Manifest.permission.READ_PHONE_STATE,
                                Manifest.permission.RECEIVE_SMS
                        },
                        ASK_MULTIPLE_PERMISSION_REQUEST_CODE);
            }
        }
    }

    private void init() {
        btnConfirm = findViewById(R.id.btnConfirm);
        edtPhoneNumber = findViewById(R.id.edtPhoneNumber);
    }
}
