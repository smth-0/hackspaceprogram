package com.example.lunar.hackspaceprogram;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.scan.BleScanRuleConfig;
import com.example.lunar.hackspaceprogram.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    BluetoothGatt gatt;
    Boolean bool = false;
    public String TAG = "dfklsdsdijfisdjftfkns";
    private final static int REQUEST_ENABLE_BT = 1;
    private BluetoothDevice myDevice;
    private String sampleMac = "3C:A0:67:D2:1C:80";//todo: mac
    private String sampleName = "MaX-NB";//todo: name
    private ArrayList<BleDevice> scanList = new ArrayList<>(25);
    String str;

    Button loginb;
    EditText login, passw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (loadText() == "ALREADY_LOGGED_IN") {
            bool = true;
        } else if (loadText() == "NOT_ALREADY_LOGGED_IN") {
            bool = false;
        }

        login = findViewById(R.id.editTextEmail);
        passw = findViewById(R.id.editTextpassword);
        loginb = findViewById(R.id.buttonLogin);
        ////////////////////////////////////////////////////////////////////////////////

        str = "login "+String.valueOf(login.getText())+" passw "+String.valueOf(passw.getText());//todo:messege for server

        //////////////////////////
        BleManager.getInstance().init(getApplication());
        BleManager.getInstance()
                .enableLog(true)
                .setReConnectCount(1, 5000)
                .setSplitWriteNum(20)
                .setConnectOverTime(10000)
                .setOperateTimeout(5000);
        BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder()
                .setAutoConnect(true)
                .setScanTimeOut(10000)
                .build();
        BleManager.getInstance().initScanRule(scanRuleConfig);
        if(!BleManager.getInstance().isSupportBle()){
            Toast.makeText(this,"your phone not support ble, you are lox", Toast.LENGTH_SHORT).show();
        }

//        BleManager.getInstance().connect(pi, new BleGattCallback() {
//            @Override
//            public void onStartConnect() {
//                Log.d(TAG,"starting connect...");
//            }
//
//            @Override
//            public void onConnectFail(BleDevice bleDevice, BleException exception) {
//                Log.d(TAG,"fail to connect!");
//            }
//
//            @Override
//            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
//                Log.d(TAG,"success to connect!");
//            }
//
//            @Override
//            public void onDisConnected(boolean isActiveDisConnected, BleDevice bleDevice, BluetoothGatt gatt, int status) {
//
//            }
//        });

        loginb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (
                        loginnow(
                                String.valueOf(
                                        login.getText()
                                ),
                                String.valueOf(
                                        passw.getText()
                                )
                        )) {
                    bool = true;
                    Log.d(TAG,"access is"+bool);
                    push();
                }

            }
        });



    ////////////////////////////////////////////////////////////////////////////////
    //todo: any things to do in start up





    ////////////////////////////////////////////////////////////////////////////////

}


    protected void onDestroy() {
        super.onDestroy();
        if(bool){
            saveText("ALREADY_LOGGED_IN");
        } else {
            saveText("NOT_ALREADY_LOGGED_IN");
        }
        BleManager.getInstance().disconnectAllDevice();
    }
    void saveText(String text) {
        SharedPreferences sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString("ldsjhgkjdfhkjgdfhgkjhdfghkdfg", text);
        ed.commit();
    }

    String loadText() {
        SharedPreferences sPref = getPreferences(MODE_PRIVATE);
        String savedText = sPref.getString("ldsjhgkjdfhkjgdfhgkjhdfghkdfg", "");
        return savedText;
    }

    Boolean loginnow(String login, String passw){
//        if(login==passw&&login=="admin"){
//            return true;
//        }
//        return false;//todo: login system

        return true;
    }

    void push(){

    BleDevice pi = null;

        for(int i=0;i<scanList.size();i++){
            if(scanList.get(i).getName().equals(sampleName)){
                pi = scanList.get(i);
            }
        }

        try{
            if(pi==null){
                Log.e(TAG, "just not enough device!");
            }
        } catch (NullPointerException e ){
            Log.e(TAG,e.toString());
        }

        if(bool) {
            BleManager.getInstance().scan(new BleScanCallback() {
                @Override
                public void onScanStarted(boolean success) {
                    if (success) {
                        Log.d(TAG, "Scan started!");
                    } else {
                        Log.e(TAG, "cant start scan");
                    }
                }

                @Override
                public void onScanning(BleDevice bleDevice) {
                    Log.d(TAG,"device detected! name is : '"+bleDevice.getName()+"' and mac is '"+bleDevice.getMac()+"';");

                    scanList.add(bleDevice);

                }

                @Override
                public void onScanFinished(List<BleDevice> scanResultList) {
                    for(int i=0;i<scanResultList.size();i++){
                        BleDevice bleDevice = scanResultList.get(i);
                        Log.d(TAG,"device detected! name is : '"+bleDevice.getName()+"' and mac is '"+bleDevice.getMac()+"';");
                    }
                    Log.d(TAG,"isempty1"+scanResultList.isEmpty()+"|isempty2:"+scanList.isEmpty());
                    Log.d(TAG, "scan finished!");

                }
            });

            BleManager.getInstance().connect(sampleMac, new BleGattCallback() {
                @Override
                public void onStartConnect() {
                    Log.d(TAG, "starting connect...");
                }

                @Override
                public void onConnectFail(BleDevice bleDevice, BleException exception) {

                }

                @Override
                public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {

                }

                @Override
                public void onDisConnected(boolean isActiveDisConnected, BleDevice bleDevice, BluetoothGatt gatt, int status) {
                }
            });
            BleManager.getInstance().write(
                    pi,
                    "",
                    "",
                    str.getBytes(),
                    new BleWriteCallback() {
                        @Override
                        public void onWriteSuccess(int current, int total, byte[] justWrite) {
                            Log.e(TAG,  "success");
                        }

                        @Override
                        public void onWriteFailure(BleException exception) {
                            Log.e(TAG, "failed to connect");
                        }
                    });
        if(bool){
            startActivity(new Intent(this,DoingActivity.class));
        }
    }
}
}
