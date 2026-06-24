package com.example.tictactoe.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tictactoe.R;
import com.example.tictactoe.adapter.HistoryAdapter;
import com.example.tictactoe.database.GameDAO;
import com.example.tictactoe.model.GameRecord;

import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private int userId;
    private String username;

    private RecyclerView recyclerView;
    private HistoryAdapter adapter;
    private TextView tvEmpty, tvStatsSummary;

    private GameDAO gameDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // Nhận dữ liệu từ MenuActivity
        userId = getIntent().getIntExtra(MenuActivity.EXTRA_USER_ID, -1);
        username = getIntent().getStringExtra(MenuActivity.EXTRA_USERNAME);

        gameDAO = new GameDAO(this);

        initViews();
        loadHistory();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerHistory);
        tvEmpty = findViewById(R.id.tvEmpty);
        tvStatsSummary = findViewById(R.id.tvStatsSummary);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Lịch Sử - " + username);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void loadHistory() {
        List<GameRecord> history = gameDAO.getGameHistory(userId);

        int wins = gameDAO.countWins(userId);
        int losses = gameDAO.countLosses(userId);
        int draws = gameDAO.countDraws(userId);

        tvStatsSummary.setText(
                "Thắng: " + wins + "  |  Thua: " + losses + "  |  Hòa: " + draws +
                "  |  Tổng: " + (wins + losses + draws)
        );

        if (history.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter = new HistoryAdapter(this, history);
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
