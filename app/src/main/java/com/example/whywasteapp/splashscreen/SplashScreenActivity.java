package com.example.whywasteapp.splashscreen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import com.example.whywasteapp.MainActivity;
import com.example.whywasteapp.R;
import com.example.whywasteapp.intro.IntroActivity;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splach_screen);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                synchronized (this){
                    try {
                        wait(1500);
                    }catch (InterruptedException e){

                    }
                }
                Intent intent = new Intent(SplashScreenActivity.this, IntroActivity.class);
                startActivity(intent);
                finish();
            }
        };

        //runnable.run();
        Thread thread = new Thread(runnable);
        thread.start();
    }
}