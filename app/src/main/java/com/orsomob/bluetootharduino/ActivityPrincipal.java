package com.orsomob.bluetootharduino;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.IntentFilter;
import android.speech.RecognizerIntent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.UUID;

public class ActivityPrincipal extends AppCompatActivity {

    /*Variaveis staticas*/
    private static final int SOLICITA_ATIVACAO_BLUETOOTH = 1;
    private static final int SOLICITA_CONEXAO            = 2;
    private static final int SOLICITA_RECONHECIMENTO_VOZ = 3;
    private static String MAC;
    private static String NOME_DISPOSITIVO;
    private static final UUID BLUETOOTH_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    /*Variaveis de ambiente*/
    private static BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mBluetoothDispositivo;
    private BluetoothSocket mBluetoothSocket;
    private boolean isClickButton1;
    private boolean isClickButton2;
    private boolean isClickButton3;
    private String mPalavraReconhecida;
    private ConstraintLayout rootLayout;

    /*Componentes*/
    private Button mButtonled1;
    private Button mButtonled2;
    private Button mButtonled3;
    private ImageButton mButtonMic;
    private TextView mTextStatus;
    private TextView mTextDispositivo;
    private Button mButtonConexao;
    public static ActivityPrincipal activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        rootLayout = (ConstraintLayout) findViewById(R.id.activity_principal);

        inicializacao();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case SOLICITA_ATIVACAO_BLUETOOTH:
                if (resultCode == Activity.RESULT_OK){
                    Toast.makeText(this,getString(R.string.bluetoth_ativado),Toast.LENGTH_SHORT).show();
                } else {
                    finish();
                }
                break;
            case SOLICITA_CONEXAO:
                if(resultCode == Activity.RESULT_OK){
                    MAC = data.getExtras().getString(ActivityListaDispositivos.ENDERECO_MAC);
                    NOME_DISPOSITIVO = data.getExtras().getString(ActivityListaDispositivos.NOME_DISPOSITIVO);

                    mButtonConexao.setText(getString(R.string.desconectar));
                    mTextStatus.setText(getString(R.string.conectado));
                    mTextStatus.setTextColor(getResources().getColor(R.color.verde));

                    String format = getString(R.string.dispositivo);
                    String dispositivo = String.format(format, NOME_DISPOSITIVO, MAC);
                    mTextDispositivo.setText(dispositivo);

                    try{
                        mBluetoothDispositivo = getBluetoothAdapter().getRemoteDevice(MAC);
                        mBluetoothSocket = mBluetoothDispositivo.createRfcommSocketToServiceRecord(BLUETOOTH_UUID);
                        mBluetoothSocket.connect();
                        Toast.makeText(this, "Conectado ao MAC :" + MAC ,Toast.LENGTH_SHORT).show();
                    } catch (IOException e){
                        e.printStackTrace();
                        Toast.makeText(this, "Não foi possivel conectar !" ,Toast.LENGTH_SHORT).show();
                        mButtonConexao.setText(getString(R.string.conectar));
                        mTextStatus.setText(getString(R.string.desconectado));
                        mTextStatus.setTextColor(getResources().getColor(R.color.vermelho));
                        mTextDispositivo.setText("");
                        isClickButton1 = false;
                    }
                }
                else {
                    Toast.makeText(this,getString(R.string.nenhum_dispositivo_selecionado),Toast.LENGTH_SHORT).show();
                }
                break;
            case SOLICITA_RECONHECIMENTO_VOZ:
                if (resultCode == Activity.RESULT_OK) {
                    ArrayList<String> palavrasReconhecidas = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    //Toast.makeText(this, "Palavras: " + palavrasReconhecidas.get(0), Toast.LENGTH_LONG).show();
                    mPalavraReconhecida = palavrasReconhecidas.get(0);

                    if (verificaConexaoBuetooth()){
                        try {
                            mBluetoothSocket.getOutputStream().write(removerAcentos(mPalavraReconhecida.toLowerCase()).getBytes());
                            Toast.makeText(this, mPalavraReconhecida, Toast.LENGTH_SHORT).show();
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                    switch (mPalavraReconhecida){

                        case "Paulo":
                            /**
                             * Estern Egg
                             * Essa é para o professor Paulo que é Gremista
                             * */
                            rootLayout.setBackground(getDrawable(R.drawable.gremio));
                            mButtonled1.setVisibility(View.INVISIBLE);
                            mButtonled2.setVisibility(View.INVISIBLE);
                            mButtonled3.setVisibility(View.INVISIBLE);
                            break;
                        case "voltar":
                            rootLayout.setBackground(getDrawable(R.drawable.bg_gradient));
                            mButtonled1.setVisibility(View.VISIBLE);
                            mButtonled2.setVisibility(View.VISIBLE);
                            mButtonled3.setVisibility(View.VISIBLE);
                    }
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {

        try {
            if (mBluetoothSocket != null && mBluetoothSocket.isConnected()){
                mBluetoothSocket.close();
                super.onDestroy();
            } else {
                super.onDestroy();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void inicializacao() {
        activity = this;
        iniciaBroadCastReceiver();
        recuperarComponentes();
        acoesComponentes();
        setBluetoothAdapter();
        ativaBluetooth();
        verificaBluetooth();
    }

    private void iniciaBroadCastReceiver() {
        BluetoothBroadcastReceiver btReceiver = new BluetoothBroadcastReceiver();

        IntentFilter btFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(btReceiver, btFilter);
    }

    private boolean verificaConexaoBuetooth(){
        if (mBluetoothSocket != null && mBluetoothSocket.isConnected()){
            return true;
        }
        Toast.makeText(this, getString(R.string.desconectado), Toast.LENGTH_SHORT).show();
        return false;
    }

    private void acoesComponentes() {

        mButtonled1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (verificaConexaoBuetooth()){
                    try {
                        if (!isClickButton1){
                            mBluetoothSocket.getOutputStream().write("ligar vermelho".getBytes());
                            isClickButton1 = true;
                        } else {
                            mBluetoothSocket.getOutputStream().write("desligar vermelho".getBytes());
                            isClickButton1 = false;
                        }
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }
        });

        mButtonled2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (verificaConexaoBuetooth()){
                    try {
                        if (!isClickButton2){
                            mBluetoothSocket.getOutputStream().write("ligar azul".getBytes());
                            isClickButton2 = true;
                        } else {
                            mBluetoothSocket.getOutputStream().write("desligar azul".getBytes());
                            isClickButton2 = false;
                        }
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }
        });

        mButtonled3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (verificaConexaoBuetooth()){
                    try {
                        if (!isClickButton3){
                            mBluetoothSocket.getOutputStream().write("ligar verde".getBytes());
                            isClickButton3 = true;
                        } else {
                            mBluetoothSocket.getOutputStream().write("desligar verde".getBytes());
                            isClickButton3 = false;
                        }
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }
        });

        mButtonMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reconhecimentoDeVoz();
            }
        });

        mButtonConexao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (BluetoothAdapter.getDefaultAdapter().getState() != BluetoothAdapter.STATE_OFF) {
                    if (mBluetoothSocket != null && mBluetoothSocket.isConnected()){
                        try {
                            mButtonConexao.setText(getString(R.string.conectar));
                            mBluetoothSocket.close();
                            mTextStatus.setText(getString(R.string.desconectado));
                            mTextStatus.setTextColor(getResources().getColor(R.color.vermelho));
                            mTextDispositivo.setText(getString(R.string.nenhum_dispositivo_selecionado));
                        }catch (IOException e){
                            e.printStackTrace();
                            Toast.makeText(ActivityPrincipal.this, "Não foi possivel desconectar !" ,Toast.LENGTH_SHORT).show();
                            mTextDispositivo.setText(getString(R.string.nenhum_dispositivo_selecionado));
                        }
                    } else {
                        mTextStatus.setText(getResources().getString(R.string.desconectado));
                        mTextStatus.setTextColor(getResources().getColor(R.color.vermelho));
                        mTextDispositivo.setText("");
                        Intent intent = new Intent(ActivityPrincipal.this, ActivityListaDispositivos.class);
                        startActivityForResult(intent, SOLICITA_CONEXAO);
                    }
                } else {
                    ativaBluetooth();
                }
            }
        });
    }

    private void recuperarComponentes() {
        mButtonled1 = (Button) findViewById(R.id.buttonLed1);
        mButtonled2 = (Button) findViewById(R.id.buttonLed2);
        mButtonled3 = (Button) findViewById(R.id.buttonLed3);
        mButtonMic = (ImageButton) findViewById(R.id.buttonMic);
        mTextDispositivo = (TextView) findViewById(R.id.textViewDispositivo);
        mTextStatus = (TextView) findViewById(R.id.textViewStatus);
        mButtonConexao = (Button) findViewById(R.id.buttonConexao);
    }

    private void verificaBluetooth() {
        if(!mBluetoothAdapter.isEnabled()){
            Toast.makeText(this,getString(R.string.bluetooth_desativado),Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this,getString(R.string.bluetoth_ativado),Toast.LENGTH_SHORT).show();
        }
    }

    private void ativaBluetooth() {
        if (!mBluetoothAdapter.isEnabled()){
            Intent lAtivaBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(lAtivaBluetooth, SOLICITA_ATIVACAO_BLUETOOTH);
        }
    }

    private void setBluetoothAdapter() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, getString(R.string.dispositivo_sem_bluetooth), Toast.LENGTH_SHORT).show();
        }
    }

    public static BluetoothAdapter getBluetoothAdapter(){
        return mBluetoothAdapter;
    }

    public void reconhecimentoDeVoz() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Fale com o Arduino !");
        try {
            startActivityForResult(intent, SOLICITA_RECONHECIMENTO_VOZ);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(), getString(R.string.reconhecimento_nao_suportado), Toast.LENGTH_SHORT).show();
        }
    }

    public static String removerAcentos(String str) {
        return Normalizer.normalize(str, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    }

    public void setBluetoothOff(){
        mButtonConexao.setText(getString(R.string.conectar));
        mTextStatus.setText(getString(R.string.desconectado));
        mTextStatus.setTextColor(getResources().getColor(R.color.vermelho));
    }
}
