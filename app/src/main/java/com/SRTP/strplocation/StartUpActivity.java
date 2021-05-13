package com.SRTP.strplocation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

public class StartUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_up);
        Timer timer = new Timer();
        TimerTask timerTask =new TimerTask() {
            @Override
            public void run() {
                startActivity(new Intent(StartUpActivity.this,NavigationActivity.class));
                finish();
            }
        };timer.schedule(timerTask,2000);
    }
}
