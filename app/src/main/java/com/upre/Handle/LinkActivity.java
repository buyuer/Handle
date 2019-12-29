package com.upre.Handle;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class LinkActivity extends AppCompatActivity {

    /*串口UUID*/
    private final static UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private boolean isLink = false;

    /*广播接收器*/
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                arrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
            if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)){
                showBondedDevices();
            }
        }
    };

    /*蓝牙设备列表相关*/
    protected ListView listDevices;
    private ArrayAdapter<String> arrayAdapter;

    /*蓝牙相关*/
    private BluetoothAdapter bluetoothAdapter;
    public static BluetoothSocket bluetoothSocket;
    private BluetoothServerSocket bluetoothServerSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link);

        /*View初始化*/
        listDevices = findViewById(R.id.list_devices);
        arrayAdapter = new ArrayAdapter<>(LinkActivity.this,android.R.layout.simple_list_item_1);
        listDevices.setAdapter(arrayAdapter);
        listDevices.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id)->{
            String temp = arrayAdapter.getItem(position);
            assert temp != null;
            String address = temp.substring(temp.indexOf("\n") + 1);
            Toast.makeText(LinkActivity.this,"连接中。。。",Toast.LENGTH_LONG).show();
            new Thread(()-> {
                try{
                    bluetoothSocket = bluetoothAdapter.getRemoteDevice(address).createRfcommSocketToServiceRecord(MY_UUID);
                    bluetoothSocket.connect();
                    Intent intent = new Intent(LinkActivity.this,HandleActivity.class);
                    startActivity(intent);
                }catch (IOException e){
                    Toast.makeText(LinkActivity.this,"连接失败",Toast.LENGTH_LONG).show();
                }
            }).start();
        });

        /*初始化*/
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        /*判断是否支持蓝牙*/
        if(bluetoothAdapter == null){
            Toast.makeText(this,"不支持蓝牙",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume(){
        super.onResume();

        /*设置竖屏*/
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        /*判断蓝牙是否打开*/
        if(!bluetoothAdapter.isEnabled()){
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }

        /*显示已配对设备列表*/
        showBondedDevices();

        /*注册搜索蓝牙广播*/
        registerReceiver(mReceiver,new IntentFilter(BluetoothDevice.ACTION_FOUND));

        /*开始搜索*/
        bluetoothAdapter.startDiscovery();

        /*监听连接*/
        new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    bluetoothServerSocket = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord("",MY_UUID);
                    while (true) {
                        bluetoothSocket = bluetoothServerSocket.accept();
                        if (bluetoothSocket != null) {
                            isLink = true;
                            bluetoothAdapter.cancelDiscovery();
                            //bluetoothServerSocket.close();
                            //Toast.makeText(LinkActivity.this, "Successed", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(LinkActivity.this,HandleActivity.class);
                            startActivity(intent);
                            break;
                        }
                    }
                } catch (IOException e){}
            }
        }).start();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    private void showBondedDevices(){
        arrayAdapter.clear();
        /*获取已配对蓝牙设备列表*/
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if(pairedDevices.size() > 0){
            for(BluetoothDevice device : pairedDevices){
                arrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        }
    }
}
