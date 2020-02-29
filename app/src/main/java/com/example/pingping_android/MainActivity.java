package com.example.pingping_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import static splitties.toast.ToastKt.toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // View
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

        targetTextView = (TextView)findViewById(R.id.isConnected);

        /* register Button Event */
        sendBtn = (Button)findViewById(R.id.send);
        sendBtn.setOnClickListener(this);

        if(!isNetworkConnected(this))
            toast("인터넷에 연결할 수 없습니다. 네트워크 상태를 확인해주세여.");
    }

    //인터넷 연결 여부 확인
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo wimax = manager.getNetworkInfo(ConnectivityManager.TYPE_WIMAX);

        boolean bwimax = false;

        if (wimax != null)
            bwimax = wimax.isConnected(); // wimax 상태 체크
        if (mobile != null) return mobile.isConnected() || wifi.isConnected() || bwimax;
        else return wifi.isConnected() || bwimax;
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
        if (isConnected && !serverIp.trim().equals("")) {
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

    //라이브러리 넣어둬서 이제 toast(하고싶은말)로 사용 가능
    /*private void showToast(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }*/
}
