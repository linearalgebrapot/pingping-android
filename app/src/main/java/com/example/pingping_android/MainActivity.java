package com.example.pingping_android;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // VIew
    EditText editText;
    Button sendBtn;
    TextView targetTextView;

    // Socket
    Socket socket;
    BufferedInputStream bin;
    BufferedOutputStream bout;

    SocketThread st;
    /*
    ReadThread rt;
    WriteThread wt;
    */

    //Handler writeHandler;

    // Connection info.
    String serverIp;
    int serverPort = 7777;

    boolean flagConnection = true;
    boolean isConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* adjust custom font */
        TextView lapotText = (TextView)findViewById(R.id.lapot);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "ttf_kimdo.ttf");
        lapotText.setTypeface(typeface);

        /* initialize network setting */
        /*
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        // TODO: deprecated!
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                // ...
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                // ...
            }
        } else {
            // can't connect to WiFi network
        }
        */

        targetTextView = (TextView)findViewById(R.id.isConnected);

        /* register Button Event */
        sendBtn = (Button)findViewById(R.id.send);
        sendBtn.setOnClickListener(this);
    }

    // Button Event callback function
    @Override
    public void onClick(View v) {

        /* Get ip addr as user input from EditText View */
        editText = (EditText)findViewById(R.id.addr);
        serverIp = editText.getText().toString();

        /* networking... -> set inConnected value */
        st = new SocketThread();
        st.start();

        /* check, and inform to user if connected */
        if (isConnected == true && !serverIp.trim().equals("")) {
            targetTextView.setText(serverIp + "에 연결됐어요 핑핑!");
            targetTextView.setTextColor(Color.parseColor("#74b9ff"));
        } else if (serverIp.trim().equals("")) {
            targetTextView.setText("암것도 입력안하셨거든여 ㅡㅡ;");
            targetTextView.setTextColor(Color.parseColor("#ff7675"));
        } else {
            targetTextView.setText(serverIp + "인데 다시 연결해봐여 핑핑...");
            targetTextView.setTextColor(Color.parseColor("#ff7675"));
        }
        targetTextView.setVisibility(View.VISIBLE);

    }

    class SocketThread extends Thread {

        public void run() {

            while (flagConnection) {

                try {
                    if (!isConnected) {
                        socket = new Socket();
                        SocketAddress remoteAddr = new InetSocketAddress(serverIp, serverPort);
                        socket.connect(remoteAddr, 5000);

                        bout = new BufferedOutputStream(socket.getOutputStream());
                        bin = new BufferedInputStream(socket.getInputStream());

                        /*
                        if (rt != null) {
                            flagRead = false;
                        }
                        if (wt != null) {
                            writeHandler.getLooper().quit();
                        }
                        wt =
                         */

                        isConnected = true;

                    } else {
                        SystemClock.sleep(5000);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    SystemClock.sleep(5000);
                }

            }
        }
    }

    private void showToast(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

}
