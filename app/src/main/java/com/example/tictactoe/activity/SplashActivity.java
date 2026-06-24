package com.example.tictactoe.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tictactoe.R;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 2000; // 2 giây

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(() -> {
            // Kiểm tra xem đã đăng nhập chưa
            SharedPreferences prefs = getSharedPreferences("TicTacToePrefs", MODE_PRIVATE);
            int savedUserId = prefs.getInt("userId", -1);
            String savedUsername = prefs.getString("username", null);

            Intent intent;
            if (savedUserId != -1 && savedUsername != null) {
                // Đã đăng nhập → vào MenuActivity
                intent = new Intent(SplashActivity.this, MenuActivity.class);
                intent.putExtra("userId", savedUserId);
                intent.putExtra("username", savedUsername);
            } else {
                // Chưa đăng nhập → vào LoginActivity
                intent = new Intent(SplashActivity.this, LoginActivity.class);
            }
            startActivity(intent);
            finish();
        }, SPLASH_DELAY);
    }
}
