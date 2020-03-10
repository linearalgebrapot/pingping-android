package com.example.pingping_android;

import android.app.Activity;
import android.os.SystemClock;
import android.widget.Toast;

/**
 * @date 2020-03-10
 * @author Bammer
 */

public class BackPressHandler {

    // 마지막으로 뒤로가기 버튼을 눌렀던 시간 저장
    private long backKeyPressedTime = 0;

    // 종료시킬 Activity
    private Activity activity;

    /**
     * Constructor
     * @param activity (to be exited)
     */
    public BackPressHandler(Activity activity) {
        this.activity = activity;
    }

    /**
     * Toast 메세지 사용자 지정, 뒤로가기 간격 사용자 지정
     * @param msg
     * @param time
     */
    public void onBackPressed(String msg, int time) {
        if (System.currentTimeMillis() > backKeyPressedTime + time) {
            backKeyPressedTime = System.currentTimeMillis();
            Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + time) {
            activity.finish();
        }
    }
}
