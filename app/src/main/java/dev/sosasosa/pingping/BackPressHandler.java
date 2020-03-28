package dev.sosasosa.pingping;

import android.app.Activity;
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

    /**
     * Splash 화면 종료 방지, default 스펙 설정
     * @param isSplash
     */
    public void onBackPressed(String isSplash) {
        if (isSplash == "SPLASH") {
            // do nothing.
        } else {
            if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
                backKeyPressedTime = System.currentTimeMillis();
                Toast.makeText(activity, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
                activity.finish();
            }
        }
    }
}
