package com.example.pingping_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
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
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import javax.net.ssl.HandshakeCompletedEvent;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    /* Globals */
    // View
    EditText    editText;
    TextView    targetTextView;
    TextView    lapotText;
    Button      btnSend, btnStart, btnLeft, btnRight, btnEnd;

    // Socket
    private static Socket socket;
    private static PrintWriter printWriter;
    String command = "";

    // Connection info.
    private static String serverIp;
    int serverPort = 7777;

    boolean isConnected = false;

    // AsyncTask for socket connection
    SocketAsyncTask sat;


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
        btnStart.setOnClickListener(this);
        btnLeft.setOnClickListener(this);
        btnRight.setOnClickListener(this);
        btnEnd.setOnClickListener(this);

        /* check whether WiFi, Mobile network is opened */
        if (!isNetworkConnected(this)) {
            Toast.makeText(getApplicationContext(), "인터넷에 연결할 수 없습니다. 네트워크 상태를 확인해주세여.", Toast.LENGTH_SHORT).show();
        }

        sat = new SocketAsyncTask();
    }


    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.send) {
            Log.d("DEBUG", "onClick: btnSend");
            serverIp = editText.getText().toString();

            if (!serverIp.trim().equals("")) {
                sat.execute();
            } else { // else if (serverIp.trim().equals(""))
                targetTextView.setText("암것도 입력안하셨거든여 ㅡㅡ;");
                targetTextView.setTextColor(Color.parseColor("#ff7675"));
            }
            targetTextView.setVisibility(View.VISIBLE);

        } else {
            Button b = (Button)v;
            command = b.getText().toString().toLowerCase();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // set output stream
                        printWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                        // send command
                        // TODO: what is diff. between write(command), flush(), close()
                        printWriter.println(command);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }


    class SocketAsyncTask extends AsyncTask<String, Integer, Void>{

        private int what;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... strings) {

            // Socket Connection.
            if (!isConnected) {
                try {
                    // connect to socket at port 7777
                    InetAddress serverAddr = InetAddress.getByName(serverIp);
                    socket = new Socket(serverAddr, serverPort);

                    publishProgress(1);
                    isConnected = true;
                    // socket.close();

                } catch (IOException e) {
                    e.printStackTrace();
                    publishProgress(2);
                }
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            Log.d("DEBUG", "onProgressUpdate(" + values[0] + ")");

            if (values[0] == 1) {
                btnSend.setEnabled(false);
                btnStart.setEnabled(true);
                btnLeft.setEnabled(true);
                btnRight.setEnabled(true);
                btnEnd.setEnabled(true);

                targetTextView.setText(serverIp + "에 연결됐어요 핑핑!");
                targetTextView.setTypeface(null, Typeface.BOLD);
                targetTextView.setTextColor(Color.parseColor("#74b9ff"));
            } else if (values[0] == 2) {
                targetTextView.setText(serverIp + "인데 다시 연결해봐여 핑핑...");
                targetTextView.setTextColor(Color.parseColor("#ff7675"));
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
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
