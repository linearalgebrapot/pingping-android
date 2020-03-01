package com.example.pingping_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import javax.net.ssl.HandshakeCompletedEvent;

// import static splitties.toast.ToastKt.toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    /* Globals */
    // View
    EditText    editText;
    TextView    targetTextView;
    TextView    lapotText;
    Button      btnSend, btnStart, btnLeft, btnRight, btnEnd;

    // Socket
    Socket                  socket;
    BufferedOutputStream    bout;

    SocketThread st;

    // Connection info.
    String serverIp;
    int serverPort = 7777;

    boolean isConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = (EditText)findViewById(R.id.addr);
        targetTextView = (TextView)findViewById(R.id.isConnected);
        lapotText = (TextView)findViewById(R.id.lapot);

        btnSend = (Button)findViewById(R.id.send);
        btnStart = (Button)findViewById(R.id.start);
        btnLeft = (Button)findViewById(R.id.left);
        btnRight = (Button)findViewById(R.id.right);
        btnEnd = (Button)findViewById(R.id.end);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "ttf_kimdo.ttf");
        lapotText.setTypeface(typeface);

        btnStart.setEnabled(false);
        btnLeft.setEnabled(false);
        btnRight.setEnabled(false);
        btnEnd.setEnabled(false);

        btnSend.setOnClickListener(this);

        /* check whether WiFi, Mobile network is opened */
        if (!isNetworkConnected(this)) {
            Toast.makeText(getApplicationContext(), "인터넷에 연결할 수 없습니다. 네트워크 상태를 확인해주세여.", Toast.LENGTH_SHORT).show();
        }

        st = new SocketThread();
    }


    @Override
    public void onClick(View v) {

        serverIp = editText.getText().toString();
        if (!serverIp.trim().equals("")) {
            st.start();
        } else { // else if (serverIp.trim().equals(""))
            targetTextView.setText("암것도 입력안하셨거든여 ㅡㅡ;");
            targetTextView.setTextColor(Color.parseColor("#ff7675"));
        }
        targetTextView.setVisibility(View.VISIBLE);
    }


    Handler uiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                targetTextView.setText(serverIp + "에 연결됐어요 핑핑!");
                targetTextView.setTextColor(Color.parseColor("#74b9ff"));

                btnSend.setEnabled(false);
                btnStart.setEnabled(true);
                btnLeft.setEnabled(true);
                btnRight.setEnabled(true);
                btnEnd.setEnabled(true);

                Toast.makeText(getApplicationContext(), "오로로로로롴", Toast.LENGTH_SHORT).show();
            } else if (msg.what == 2) { /* may never be happened case */
                targetTextView.setText(serverIp + "에 이미 연결되어있을걸요..? 핑핑!");
                targetTextView.setTextColor(Color.parseColor("#74b9ff"));

            } else if (msg.what == 3) {
                targetTextView.setText(serverIp + "인데 다시 연결해봐여 핑핑...");
                targetTextView.setTextColor(Color.parseColor("#ff7675"));
            }
        }
    };


    class SocketThread extends Thread {

        @Override
        public void run() {

            Message msg = new Message();
            try {
                // create Socket and connect
                if (!isConnected) {
                    socket = new Socket();
                    SocketAddress remoteAddr = new InetSocketAddress(serverIp, serverPort);
                    socket.connect(remoteAddr, 10000);

                    /*
                    bout = new BufferedOutputStream(socket.getOutputStream());

                    if (wt != null) {
                        writeHandler.getLooper().quit();
                    }

                    wt = new WriteThread();
                    wt.start();
                     */

                    isConnected = true;

                    msg.what = 1;
                    uiHandler.sendMessage(msg);

                } else {
                    msg.what = 2;
                    uiHandler.sendMessage(msg);
                }

            } catch (Exception e) {
                msg.what = 3;
                uiHandler.sendMessage(msg);
            }

        }
    }

    //인터넷 연결 여부 확인
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo wimax = manager.getNetworkInfo(ConnectivityManager.TYPE_WIMAX);

        boolean bwimax = false;

        if (wimax != null) {
            bwimax = wimax.isConnected(); // wimax 상태 체크
        }
        if (mobile != null) {
            return mobile.isConnected() || wifi.isConnected() || bwimax;
        } else {
            return wifi.isConnected() || bwimax;
        }
    }

}
