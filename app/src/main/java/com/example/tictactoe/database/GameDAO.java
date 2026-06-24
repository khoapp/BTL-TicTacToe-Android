package com.example.tictactoe.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.tictactoe.model.GameRecord;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GameDAO {

    private DatabaseHelper dbHelper;

    public GameDAO(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
    }

    /**
     * Lưu kết quả trận đấu
     */
    public boolean saveGame(int userId, String opponent, String gameMode, String result, int movesCount) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String playedAt = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_GAME_USER_ID, userId);
        values.put(DatabaseHelper.COL_GAME_OPPONENT, opponent);
        values.put(DatabaseHelper.COL_GAME_MODE, gameMode);
        values.put(DatabaseHelper.COL_GAME_RESULT, result);
        values.put(DatabaseHelper.COL_GAME_MOVES, movesCount);
        values.put(DatabaseHelper.COL_GAME_PLAYED_AT, playedAt);

        long id = db.insert(DatabaseHelper.TABLE_GAME_HISTORY, null, values);
        db.close();
        return id != -1;
    }

    /**
     * Lấy lịch sử tất cả trận đấu của user (mới nhất trước)
     */
    public List<GameRecord> getGameHistory(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<GameRecord> history = new ArrayList<>();

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_GAME_HISTORY,
                null,
                DatabaseHelper.COL_GAME_USER_ID + "=?",
                new String[]{String.valueOf(userId)},
                null, null,
                DatabaseHelper.COL_GAME_ID + " DESC"
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                GameRecord record = new GameRecord();
                record.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_GAME_ID)));
                record.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_GAME_USER_ID)));
                record.setOpponent(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_GAME_OPPONENT)));
                record.setGameMode(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_GAME_MODE)));
                record.setResult(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_GAME_RESULT)));
                record.setMovesCount(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_GAME_MOVES)));
                record.setPlayedAt(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_GAME_PLAYED_AT)));
                history.add(record);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return history;
    }

    /**
     * Đếm số trận thắng của user
     */
    public int countWins(int userId) {
        return countByResult(userId, "WIN");
    }

    /**
     * Đếm số trận thua
     */
    public int countLosses(int userId) {
        return countByResult(userId, "LOSE");
    }

    /**
     * Đếm số trận hòa
     */
    public int countDraws(int userId) {
        return countByResult(userId, "DRAW");
    }

    /**
     * Tổng số trận đã chơi
     */
    public int countTotal(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_GAME_HISTORY +
                " WHERE " + DatabaseHelper.COL_GAME_USER_ID + "=?",
                new String[]{String.valueOf(userId)}
        );
        int count = 0;
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
            cursor.close();
        }
        db.close();
        return count;
    }

    private int countByResult(int userId, String result) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_GAME_HISTORY +
                " WHERE " + DatabaseHelper.COL_GAME_USER_ID + "=? AND " +
                DatabaseHelper.COL_GAME_RESULT + "=?",
                new String[]{String.valueOf(userId), result}
        );
        int count = 0;
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
            cursor.close();
        }
        db.close();
        return count;
    }

    /**
     * Xóa toàn bộ lịch sử của user
     */
    public void clearHistory(int userId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_GAME_HISTORY,
                DatabaseHelper.COL_GAME_USER_ID + "=?",
                new String[]{String.valueOf(userId)});
        db.close();
    }
}
