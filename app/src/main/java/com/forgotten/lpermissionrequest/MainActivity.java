package com.forgotten.lpermissionrequest;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;

import com.forgotten.lpermissionrequset.LPermissionCallback;
import com.forgotten.lpermissionrequset.LPermissionHelper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LPermissionHelper.with(this)    // 添加上下文环境
                // 添加要赋予的权限
                .addPermissions(Manifest.permission.CAMERA,Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.READ_SMS,Manifest.permission.SEND_SMS,
                        Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCEPT_HANDOVER)
                // 设置强制申请权限(一直弹窗) 默认false
                .setForceAllGranted(true)
                // 设置对话框内容(可选)
                .setForceDeniedTips("未授权，请前往设置手动授予")
                // 设置成功回调与失败回调
                .onResult(new LPermissionCallback() {
                    @Override
                    public void onRequestSuccess() {
                        Log.d("MainActivity", "onRequestSuccess: ");
                    }

                    @Override
                    public void onRequestFailed(String[] grantedPermissions, String[] deniedPermissions, String[] forceDeniedPermissions) {
                        Log.d("MainActivity", "onRequestFailed: ");
                    }
                })
                // 进行权限的检查申请
                .check();
    }
}