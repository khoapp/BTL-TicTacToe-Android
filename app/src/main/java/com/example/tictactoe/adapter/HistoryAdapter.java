package com.example.tictactoe.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tictactoe.R;
import com.example.tictactoe.model.GameRecord;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private List<GameRecord> records;
    private Context context;

    public HistoryAdapter(Context context, List<GameRecord> records) {
        this.context = context;
        this.records = records;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        GameRecord record = records.get(position);

        // Số thứ tự
        holder.tvNumber.setText(String.valueOf(position + 1));

        // Đối thủ & chế độ
        String modeLabel = record.getGameMode().equals("AI") ? "vs Máy" : "vs Người";
        holder.tvOpponent.setText(record.getOpponent() + " (" + modeLabel + ")");

        // Ngày giờ & số nước
        holder.tvDate.setText(record.getPlayedAt() + "  •  " + record.getMovesCount() + " nước");

        // Kết quả
        switch (record.getResult()) {
            case "WIN":
                holder.tvResult.setText("THẮNG");
                holder.tvResult.setTextColor(Color.parseColor("#4CAF50"));
                holder.cardView.setCardBackgroundColor(Color.parseColor("#E8F5E9"));
                break;
            case "LOSE":
                holder.tvResult.setText("THUA");
                holder.tvResult.setTextColor(Color.parseColor("#F44336"));
                holder.cardView.setCardBackgroundColor(Color.parseColor("#FFEBEE"));
                break;
            case "DRAW":
                holder.tvResult.setText("HÒA");
                holder.tvResult.setTextColor(Color.parseColor("#FF9800"));
                holder.cardView.setCardBackgroundColor(Color.parseColor("#FFF3E0"));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return records != null ? records.size() : 0;
    }

    public void updateData(List<GameRecord> newRecords) {
        this.records = newRecords;
        notifyDataSetChanged();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvNumber, tvOpponent, tvDate, tvResult;

        HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardHistory);
            tvNumber = itemView.findViewById(R.id.tvNumber);
            tvOpponent = itemView.findViewById(R.id.tvOpponent);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvResult = itemView.findViewById(R.id.tvResult);
        }
    }
}
