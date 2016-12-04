package com.orsomob.bluetootharduino;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by LucasOrso on 11/29/16.
 */

public class BluetoothBroadcastReceiver extends BroadcastReceiver {

    public static String TAG = "BT STATUS";

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();

        ActivityPrincipal ac = ActivityPrincipal.activity;
        try {
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch(state) {
                    case BluetoothAdapter.STATE_OFF:
                        ac.setBluetoothOff();
                        Log.i(TAG, "Bluetooth Desligado");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        ac.setBluetoothOff();
                        Log.i(TAG, "Bluetooth sendo desligado");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.i(TAG, "Bluetooth Ligado");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.i(TAG, "Bluetooth sendo ligado");
                        break;
                }
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
