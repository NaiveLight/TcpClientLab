package com.example.sunkwang.tcpclientlab;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    Button bt_send;
    Button bt_connect;
    Button bt_close;

    EditText bt_edit;

    //String serverIp = "kesl.iptime.org";      // 한인규 서버
    String serverIp = "203.246.112.158";    //  김명회 서버
    int serverPort = 54321;
    Socket socket;
    DataOutputStream out;

    Thread  tConnect;
    Context mContext;
    private Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mContext = this.getApplicationContext();
        bt_send = (Button) findViewById(R.id.bt_send);
        bt_connect = (Button) findViewById(R.id.bt_con);
        bt_edit = (EditText) findViewById(R.id.et_edit);
        bt_close = (Button) findViewById(R.id.bt_close);




        bt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try { //edit의 스트링을 가져와서 writeUTF로 메시지를 쓴다.
                    out.writeUTF(bt_edit.getText().toString()); // 데이타 서버에 전송하게
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        bt_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connect(); //서버 연결 쓰레드 = tConnect 오브젝트 생성
                tConnect.start();  // 쓰레드 시작 및 서버 연결 시작

            }
        });

        bt_close.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                try {
                    socket.close();   // 서버 연결 종료
                    tConnect.interrupt(); // 쓰레드 종료
                    handler.sendEmptyMessage(2);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


        handler = new Handler() {
            public void handleMessage(Message msg) {
                switch(msg.what) {
                    case 0:
                        Toast.makeText(getApplicationContext(), "서버연결 완료.", Toast.LENGTH_SHORT).show();
                        super.handleMessage(msg);
                        break;
                    case 1:
                        Toast.makeText(getApplicationContext(), "서버연결 에러.", Toast.LENGTH_SHORT).show();
                        super.handleMessage(msg);
                        break;
                    case 2:
                        Toast.makeText(getApplicationContext(), "서버연결 종료.", Toast.LENGTH_SHORT).show();
                        super.handleMessage(msg);
                        break;
                }

            }
        };

    }

    //네트워크 사용 시 쓰레드 이용해야함
    void connect() {
        tConnect =   new Thread(new Runnable() {
            @Override
            public void run() {
                try { //ip와 port로 소켓을 만듦
                    socket  = new Socket(serverIp, serverPort);
                    out = new DataOutputStream(socket.getOutputStream()); //out으로 메시지를 전달하거나 읽는다.
                    Log.d("lab", "서버연결 완료.");
                    handler.sendEmptyMessage(0); //위의 Log.d에서 Toast 메시지를 바로 띄울 수 없어서 handler를 사용하여 Toast띄움
                } catch (IOException e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(1);
                    Log.d("lab", "서버연결 에러 .");
                }
            }
        });
    }
}
