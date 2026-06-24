package com.example.tictactoe.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tictactoe.R;
import com.example.tictactoe.database.GameDAO;
import com.example.tictactoe.database.UserDAO;

public class SettingsActivity extends AppCompatActivity {

    private int userId;
    private String username;

    private EditText etNewUsername;
    private Button btnSaveName, btnClearHistory, btnLogout;

    private UserDAO userDAO;
    private GameDAO gameDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        userId = getIntent().getIntExtra(MenuActivity.EXTRA_USER_ID, -1);
        username = getIntent().getStringExtra(MenuActivity.EXTRA_USERNAME);

        userDAO = new UserDAO(this);
        gameDAO = new GameDAO(this);

        initViews();
        setupListeners();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Cài Đặt");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initViews() {
        etNewUsername = findViewById(R.id.etNewUsername);
        btnSaveName = findViewById(R.id.btnSaveName);
        btnClearHistory = findViewById(R.id.btnClearHistory);
        btnLogout = findViewById(R.id.btnLogout);

        etNewUsername.setText(username);
    }

    private void setupListeners() {
        // Đổi tên
        btnSaveName.setOnClickListener(v -> {
            String newName = etNewUsername.getText().toString().trim();
            if (TextUtils.isEmpty(newName) || newName.length() < 3) {
                etNewUsername.setError("Tên ít nhất 3 ký tự");
                return;
            }
            if (userDAO.updateUsername(userId, newName)) {
                username = newName;
                // Cập nhật SharedPreferences
                SharedPreferences prefs = getSharedPreferences("TicTacToePrefs", MODE_PRIVATE);
                prefs.edit().putString("username", newName).apply();
                Toast.makeText(this, "Đã cập nhật tên thành công!", Toast.LENGTH_SHORT).show();
            }
        });

        // Xóa lịch sử
        btnClearHistory.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Xóa Lịch Sử")
                    .setMessage("Bạn có chắc muốn xóa toàn bộ lịch sử trận đấu?")
                    .setPositiveButton("Xóa", (d, w) -> {
                        gameDAO.clearHistory(userId);
                        Toast.makeText(this, "Đã xóa lịch sử!", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });

        // Đăng xuất
        btnLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Đăng Xuất")
                    .setMessage("Bạn có chắc muốn đăng xuất?")
                    .setPositiveButton("Đăng Xuất", (d, w) -> {
                        SharedPreferences prefs = getSharedPreferences("TicTacToePrefs", MODE_PRIVATE);
                        prefs.edit().clear().apply();

                        Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
