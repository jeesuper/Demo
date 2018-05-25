package com.example.liujiechao.myapp;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by liujiechao on 7/1/17.
 */

public class MyReveiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("liujc", "MyReveiver action:"+intent.getAction());

        String action = intent.getAction();
        if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                    BluetoothAdapter.ERROR);
            switch (state) {
                case BluetoothAdapter.STATE_OFF:
                    Log.d("aaa", "STATE_OFF 手机蓝牙关闭");
                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:
                    Log.d("aaa", "STATE_TURNING_OFF 手机蓝牙正在关闭");
                    break;
                case BluetoothAdapter.STATE_ON:
                    Log.d("aaa", "STATE_ON 手机蓝牙开启");
                    break;
                case BluetoothAdapter.STATE_TURNING_ON:
                    Log.d("aaa", "STATE_TURNING_ON 手机蓝牙正在开启");
                    break;
            }
        }

        Intent intent1 =  new Intent();
        intent1.setClass(context, MainActivity.class);
        context.startActivity(intent1);
    }
}
