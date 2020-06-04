package com.upre.Handle;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class HandleActivity extends AppCompatActivity {

    private RockerView rockerViewRight,
                   rockerViewLeft;
    private EditText editView;

    private OutputStream out;
    private InputStream in;
    private byte[] buffer = new byte[1024];
    private int bytes = 0;

    private Handler handler;



    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            assert action != null;
            if(action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)){
                finish();
            }
            if(action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)){
                finish();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_handle);

        registerReceiver(mReceiver,new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
        registerReceiver(mReceiver,new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED));

        try{
            out = LinkActivity.bluetoothSocket.getOutputStream();
            in  = LinkActivity.bluetoothSocket.getInputStream();
        }catch (IOException e){}

        rockerViewRight = findViewById(R.id.rocker_right);
        rockerViewLeft = findViewById(R.id.rocker_left);
        editView = findViewById(R.id.editText);

        editView.setSingleLine(false);

        handler = new Handler(msg -> {
            byte[] temp = new byte[bytes];
            System.arraycopy(buffer,0,temp,0,bytes);
            editView.append(new String(temp));
            return false;
        });

        //右摇杆回调函数，参数x，y分别表示摇杆当前占总体的比例，范围为正负1，0表示原点
        rockerViewRight.setCallBack(new RockerView.CallBack() {

            //摇杆按下事件
            @Override
            public void onDown(float x, float y) {

            }

            //摇杆移动事件
            @Override
            public void onMove(float x, float y) {
                try{
                    out.write((byte)( x>=0 ? x*25+178 : 178-Math.abs(x)*25) );
                    out.write((byte)( y>=0 ? y*25+127 : 127-Math.abs(y)*25) );
                }catch (IOException e){}
            }

            //摇杆抬起事件
            @Override
            public void onUp(float x, float y) {
                try{
                    out.write(127);
                    out.write(178);
                }catch (IOException e){}
            }
        });

        //左摇杆回调函数，参数x，y分别表示摇杆当前占总体的比例，范围为正负1，0表示原点
        rockerViewLeft.setCallBack(new RockerView.CallBack() {

            //摇杆按下事件
            @Override
            public void onDown(float x, float y) {

            }

            //摇杆移动事件
            @Override
            public void onMove(float x, float y) {
                try{
                    out.write((byte)( x>=0 ? x*25+76 : 76-Math.abs(x)*25) );
                    out.write((byte)( y>=0 ? y*25+25 : 25-Math.abs(y)*25) );
                }catch (IOException e){}
            }

            //摇杆抬起事件
            @Override
            public void onUp(float x, float y) {
                try{
                    out.write(25);
                    out.write(26);
                }catch (IOException e){}
            }
        });

        new Thread(() -> {

            while(true){
                try{
                    bytes = in.read(buffer);
                }catch (IOException e){}
                handler.sendEmptyMessage(0);
            }
        }).start();
    }

    @Override
    protected void onResume(){
        super.onResume();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        unregisterReceiver(mReceiver);
        try{
            LinkActivity.bluetoothSocket.close();
        }catch (IOException e){}
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            finish();
        }

        return super.onKeyDown(keyCode, event);
    }

    public void buttonClicked(View v){
        String temp = String.valueOf(((Button)v).getText());
        int data = Integer.valueOf(temp);
        try{
            out.write(data);
        }catch (IOException e){}
    }
}
