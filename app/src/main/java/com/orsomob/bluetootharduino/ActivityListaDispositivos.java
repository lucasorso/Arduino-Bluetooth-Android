package com.orsomob.bluetootharduino;

import android.app.Activity;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Set;

/**
 * Created by LucasOrso on 11/26/16.
 */

public class ActivityListaDispositivos extends ListActivity {

    public static String ENDERECO_MAC = "mac";
    public static String NOME_DISPOSITIVO = "nome";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);

        BluetoothAdapter bluetoothAdapter = ActivityPrincipal.getBluetoothAdapter();

        Set<BluetoothDevice> dispositivosPareados = bluetoothAdapter.getBondedDevices();

        if (dispositivosPareados.size() > 0){
            for (BluetoothDevice b : dispositivosPareados){
                String nome = b.getName();
                String mac = b.getAddress();
                arrayAdapter.add(nome + "\n" + mac);
            }
        }
        setListAdapter(arrayAdapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        String info = ((TextView) v).getText().toString();
        String[] infoSplit = info.split("\n");
        String nome = infoSplit[0];
        String mac = infoSplit[1];

        Intent intent = new Intent();
        intent.putExtra(ENDERECO_MAC, mac);
        intent.putExtra(NOME_DISPOSITIVO, nome);

        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
