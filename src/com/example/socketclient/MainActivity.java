package com.example.socketclient;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.net.InetAddress;
import java.net.Socket;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {

  private TextView tv = null;
  private ImageView iv = null;
  private Socket socket = null;
  private Bitmap bitmap;
  private String serverUrl = "192.168.199.186";
  private int serverPort = 9999;
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
  
  class MySendCommondThread extends Thread{
    int size = 0;
    public void run(){
      for(;;){
        try {
          socket = new Socket(InetAddress.getByName(serverUrl), serverPort);
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
}
