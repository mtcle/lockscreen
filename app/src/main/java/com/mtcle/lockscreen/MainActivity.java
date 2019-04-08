package com.mtcle.lockscreen;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int ADMIN_INTENT = 15;
    private static final String description = "一键锁屏 by mtcle";
    private DevicePolicyManager mDevicePolicyManager;
    private ComponentName mComponentName;

    private String TAG = "mtcle ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDevicePolicyManager = (DevicePolicyManager) getSystemService(
                Context.DEVICE_POLICY_SERVICE);
        mComponentName = new ComponentName(this, MyAdminReceiver.class);
        boolean isAdmin = mDevicePolicyManager.isAdminActive(mComponentName);
        if (isAdmin) {
            mDevicePolicyManager.lockNow();
            finish();
            return;
        } else {
            Toast.makeText(getApplicationContext(), "设备管理器未激活", Toast.LENGTH_SHORT).show();
        }

        setContentView(R.layout.activity_main);
        /** **/


        Button btnEnableAdmin = (Button) findViewById(R.id.btnEnableAdmin);
        Button btnDisableAdmin = (Button) findViewById(R.id.btnDisableAdmin);
        Button btnLock = (Button) findViewById(R.id.btnLock);
        btnEnableAdmin.setOnClickListener(this);
        btnDisableAdmin.setOnClickListener(this);
        btnLock.setOnClickListener(this);

        if (!mDevicePolicyManager.isAdminActive(mComponentName)) {
            Toast.makeText(this, "第一次使用需要先激活锁屏", Toast.LENGTH_SHORT).show();

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.DevAdmMsg)
                    .setTitle("激活一键锁屏");
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                    Log.d(TAG, "onClick: OKay");


                    Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                    intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mComponentName);
                    intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, description);
                    startActivityForResult(intent, ADMIN_INTENT);

                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                    Log.d(TAG, "onClick: Cancel");
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnEnableAdmin:
                Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mComponentName);
                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, description);
                startActivityForResult(intent, ADMIN_INTENT);
                break;

            case R.id.btnDisableAdmin:
                mDevicePolicyManager.removeActiveAdmin(mComponentName);
                Toast.makeText(getApplicationContext(), "设备管理器已经关闭", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btnLock:
                boolean isAdmin = mDevicePolicyManager.isAdminActive(mComponentName);
                if (isAdmin) {
                    mDevicePolicyManager.lockNow();
                } else {
                    Toast.makeText(getApplicationContext(), "设备管理器未激活", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADMIN_INTENT) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(getApplicationContext(), "激活成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "激活失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

}