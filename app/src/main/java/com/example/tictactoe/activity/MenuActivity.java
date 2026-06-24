package com.example.tictactoe.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tictactoe.R;
import com.example.tictactoe.database.GameDAO;

public class MenuActivity extends AppCompatActivity {

    public static final String EXTRA_USER_ID = "userId";
    public static final String EXTRA_USERNAME = "username";
    public static final String EXTRA_GAME_MODE = "gameMode";
    public static final String EXTRA_PLAYER2_NAME = "player2Name";
    public static final String MODE_AI = "AI";
    public static final String MODE_PVP = "PVP";

    private int userId;
    private String username;

    private TextView tvWelcome, tvWins, tvLosses, tvDraws, tvTotal;
    private Button btnPlayAI, btnPlayPVP, btnHistory, btnSettings;

    private GameDAO gameDAO;

    // Launcher để nhận kết quả từ GameActivity
    private ActivityResultLauncher<Intent> gameLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                // Làm mới thống kê sau mỗi trận
                loadStats();
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Nhận dữ liệu từ LoginActivity qua Intent
        userId = getIntent().getIntExtra(EXTRA_USER_ID, -1);
        username = getIntent().getStringExtra(EXTRA_USERNAME);

        gameDAO = new GameDAO(this);

        initViews();
        setupListeners();
        loadStats();
    }

    private void initViews() {
        tvWelcome = findViewById(R.id.tvWelcome);
        tvWins = findViewById(R.id.tvWins);
        tvLosses = findViewById(R.id.tvLosses);
        tvDraws = findViewById(R.id.tvDraws);
        tvTotal = findViewById(R.id.tvTotal);
        btnPlayAI = findViewById(R.id.btnPlayAI);
        btnPlayPVP = findViewById(R.id.btnPlayPVP);
        btnHistory = findViewById(R.id.btnHistory);
        btnSettings = findViewById(R.id.btnSettings);

        tvWelcome.setText("Xin chào, " + username + "! 👋");
    }

    private void setupListeners() {
        // Chơi vs AI
        btnPlayAI.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, GameActivity.class);
            intent.putExtra(EXTRA_USER_ID, userId);
            intent.putExtra(EXTRA_USERNAME, username);
            intent.putExtra(EXTRA_GAME_MODE, MODE_AI);
            intent.putExtra(EXTRA_PLAYER2_NAME, "Máy Tính");
            gameLauncher.launch(intent);
        });

        // Chơi 2 người
        btnPlayPVP.setOnClickListener(v -> showPlayer2Dialog());

        // Lịch sử
        btnHistory.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, HistoryActivity.class);
            intent.putExtra(EXTRA_USER_ID, userId);
            intent.putExtra(EXTRA_USERNAME, username);
            startActivity(intent);
        });

        // Cài đặt
        btnSettings.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, SettingsActivity.class);
            intent.putExtra(EXTRA_USER_ID, userId);
            intent.putExtra(EXTRA_USERNAME, username);
            startActivity(intent);
        });
    }

    /**
     * Dialog nhập tên người chơi 2 cho chế độ PVP
     */
    private void showPlayer2Dialog() {
        android.widget.EditText input = new android.widget.EditText(this);
        input.setHint("Nhập tên người chơi 2");
        input.setPadding(48, 24, 48, 24);

        new AlertDialog.Builder(this)
                .setTitle("Chơi 2 Người")
                .setMessage("Nhập tên người chơi thứ 2:")
                .setView(input)
                .setPositiveButton("Bắt đầu", (dialog, which) -> {
                    String player2Name = input.getText().toString().trim();
                    if (player2Name.isEmpty()) player2Name = "Người Chơi 2";

                    Intent intent = new Intent(MenuActivity.this, GameActivity.class);
                    intent.putExtra(EXTRA_USER_ID, userId);
                    intent.putExtra(EXTRA_USERNAME, username);
                    intent.putExtra(EXTRA_GAME_MODE, MODE_PVP);
                    intent.putExtra(EXTRA_PLAYER2_NAME, player2Name);
                    gameLauncher.launch(intent);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    /**
     * Tải thống kê từ database
     */
    private void loadStats() {
        int wins = gameDAO.countWins(userId);
        int losses = gameDAO.countLosses(userId);
        int draws = gameDAO.countDraws(userId);
        int total = gameDAO.countTotal(userId);

        tvWins.setText(String.valueOf(wins));
        tvLosses.setText(String.valueOf(losses));
        tvDraws.setText(String.valueOf(draws));
        tvTotal.setText(String.valueOf(total));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadStats();

        // Cập nhật tên nếu đã thay đổi trong SettingsActivity
        SharedPreferences prefs = getSharedPreferences("TicTacToePrefs", MODE_PRIVATE);
        username = prefs.getString("username", username);
        tvWelcome.setText("Xin chào, " + username + "! 👋");
    }
}
