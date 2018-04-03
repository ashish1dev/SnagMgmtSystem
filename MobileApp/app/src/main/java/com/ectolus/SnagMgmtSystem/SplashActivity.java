package com.ectolus.SnagMgmtSystem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends Activity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                SharedPreferences mPrefs = getSharedPreferences("USER_PREFERENCES", Context.MODE_PRIVATE);
                String str = mPrefs.getString("userName", null);
                if (str == null) {
                    Intent i = new Intent(getApplication(), LoginActivity.class);
                    startActivity(i);
                } else {
                    Intent i = new Intent(getApplication(), ListViewActivity.class);
                    startActivity(i);
                }


                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

}