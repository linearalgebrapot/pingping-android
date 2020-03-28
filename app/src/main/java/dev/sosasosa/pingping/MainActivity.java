package dev.sosasosa.pingping;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import dev.sosasosa.pingping.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    /* Globals */
    // View
    EditText    editText;
    TextView    targetTextView;
    TextView    lapotText;
    Button      btnSend, btnStart, btnLeft, btnRight, btnEnd;

    SocketTask socketTask;

    // Connection Info
    //static Socket socket;
    //static int serverPort = 7777;
    //static boolean isConnected = false;

    // BackPressHandler 객체 선언, 할당
    private BackPressHandler backPressHandler = new BackPressHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //View Binding
        editText = findViewById(R.id.addr);
        targetTextView = findViewById(R.id.isConnected);
        lapotText = findViewById(R.id.lapot);

        btnSend = findViewById(R.id.send);
        btnStart = findViewById(R.id.start);
        btnLeft = findViewById(R.id.left);
        btnRight = findViewById(R.id.right);
        btnEnd = findViewById(R.id.end);

        socketTask = new SocketTask();

        //Font
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
        if (!isNetworkConnected(this))
            Toast.makeText(getApplicationContext(), "인터넷에 연결할 수 없습니다. 네트워크 상태를 확인해주세여.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        String command;

        if (v.getId() == R.id.send) {
            //Log.d("DEBUG", "onClick: btnSend");
            String serverIp = editText.getText().toString();

            if (!serverIp.trim().equals("")) {
                socketTask.connect(serverIp, this);
                // new SocketAsyncTask(serverIp).execute();
            } else {
                targetTextView.setText("암것도 입력안하셨거든여 ㅡㅡ;");
                targetTextView.setTextColor(Color.parseColor("#ff7675"));
            }
            targetTextView.setVisibility(View.VISIBLE);

        } else {
            Button b = (Button)v;
            command = b.getText().toString().toLowerCase();

            socketTask.sendMsg(command);

            /*new Thread(() -> {
                try {
                    // set output stream
                    PrintWriter printWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                    // send command
                    //printWriter.println("test");
                    printWriter.write(command);
                    printWriter.flush();
                    printWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();*/
        }
    }

    @Override
    public void onBackPressed() {
        backPressHandler.onBackPressed("\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", 2000);
    }

    public void update(boolean b, String ip) {
        if (b) {
            btnSend.setEnabled(false);
            btnStart.setEnabled(true);
            btnLeft.setEnabled(true);
            btnRight.setEnabled(true);
            btnEnd.setEnabled(true);

            targetTextView.setText(ip + "에 연결됐어요 핑핑!");
            targetTextView.setTypeface(null, Typeface.BOLD);
            targetTextView.setTextColor(Color.parseColor("#74b9ff"));
        } else {
            targetTextView.setText(ip + "인데 다시 연결해봐여 핑핑...");
            targetTextView.setTextColor(Color.parseColor("#ff7675"));
        }
    }


   /* class SocketAsyncTask extends AsyncTask<String, Integer, Void>{
        String ip;

        SocketAsyncTask(String ip){
            this.ip = ip;
        }

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
                    InetAddress serverAddr = InetAddress.getByName(ip);
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

                targetTextView.setText(ip + "에 연결됐어요 핑핑!");
                targetTextView.setTypeface(null, Typeface.BOLD);
                targetTextView.setTextColor(Color.parseColor("#74b9ff"));
            } else if (values[0] == 2) {
                targetTextView.setText(ip + "인데 다시 연결해봐여 핑핑...");
                targetTextView.setTextColor(Color.parseColor("#ff7675"));
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

    }*/

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
