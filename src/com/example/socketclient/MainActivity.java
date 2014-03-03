package com.example.socketclient;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.net.InetAddress;
import java.net.Socket;

import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

  private TextView tv = null;
  private ImageView iv = null;
  private Socket socket = null;
  private Bitmap bitmap;
  private String serverUrl = "192.168.1.1";
  private int serverPort = 2014;
  public Handler handler = new Handler();
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);    
    setContentView(R.layout.activity_main);
    Button btnconnect = (Button)this.findViewById(R.id.btnconnect);
    tv = (TextView)this.findViewById(R.id.tv);
    iv = (ImageView)this.findViewById(R.id.iv);
    btnconnect.setOnClickListener(new Button.OnClickListener() {
      @Override
      public void onClick(View view) {
        tv.setText("good");        
        Thread connectPC = new MySendCommondThread();
        connectPC.start();
      }
    });
  }
  
  //当我们按HOME键时，我在onPause方法里，将输入的值赋给mString  
  @Override  
  protected void onPause() { // 原来这里影响了 我的setting, 找了半天
    super.onPause();
  }
  
  public void onStart()//重新启动的时候
  {
    super.onStart();
//    Thread connectPC = new MySendCommondThread();
//    connectPC.start(); 
  }
  
  class MySendCommondThread extends Thread{
    int size = 0;
    public void run(){
      //读取配置文件
      SharedPreferences preParas = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
      serverUrl = preParas.getString("ServerUrl", "192.168.1.1");
      String tempStr = preParas.getString("ServerPort", "9999");
      serverPort = Integer.parseInt(tempStr);
      
      for(;;){
        try {
          socket = new Socket(InetAddress.getByName(serverUrl), serverPort);
//          socket = new Socket("192.168.1.241", 2014);
          DataInputStream dataInput = new DataInputStream(socket.getInputStream());    
          size = dataInput.readInt();
          byte[] data = new byte[size];
          int len = 0;
          while (len < size) {
            len += dataInput.read(data, len, size - len);
          }
          ByteArrayOutputStream outPut = new ByteArrayOutputStream();    
          bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);    
          bitmap.compress(CompressFormat.PNG, 100, outPut);
          handler.post(new Runnable() {                    
            public void run() {
              try {
                tv.setText(size + "");
              } catch(Exception e) {
                Log.e("tv", e.toString());
              }
              iv.setImageBitmap(bitmap);
            }
          });
          socket.close();
        } catch (Exception e) {
          Log.e("connect problem", e.toString());
          break;
        }
      }
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }
  
  //点击Menu菜单选项响应事件   
  @Override  
  public boolean onOptionsItemSelected(MenuItem item) {
    super.onOptionsItemSelected(item);//获取菜单
    switch(item.getItemId()) {  
      case R.id.action_settings:
      {
        Intent intent = new Intent(this, SettingActivity.class);
        try{
          startActivity(intent);
        } catch(Exception e) {
          // Toast.makeText(this, "option error1" + e.toString(), Toast.LENGTH_SHORT).show();
          Log.e("MYAPP", "exception: " + e.toString());
        } catch(Error e2) {
          Toast.makeText(this, e2.toString(), Toast.LENGTH_SHORT).show();
          Log.e("MYAPP", "exception: " + e2.toString());
        }
        break;
      }
      case R.id.exit:{
        try{
          //杀掉线程强制退出
          android.os.Process.killProcess(android.os.Process.myPid());
        } catch(Exception e) {
          
        }
        break;
      }
    }
    return true;
  }
}
