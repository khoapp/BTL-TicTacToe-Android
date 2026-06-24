package com.example.tictactoe.model;

public class GameRecord {
    private int id;
    private int userId;
    private String opponent;      // "AI" hoặc tên người chơi 2
    private String gameMode;      // "AI" hoặc "PVP"
    private String result;        // "WIN", "LOSE", "DRAW"
    private int movesCount;
    private String playedAt;

    public GameRecord() {}

    public GameRecord(int userId, String opponent, String gameMode, String result, int movesCount, String playedAt) {
        this.userId = userId;
        this.opponent = opponent;
        this.gameMode = gameMode;
        this.result = result;
        this.movesCount = movesCount;
        this.playedAt = playedAt;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getOpponent() { return opponent; }
    public void setOpponent(String opponent) { this.opponent = opponent; }

    public String getGameMode() { return gameMode; }
    public void setGameMode(String gameMode) { this.gameMode = gameMode; }

    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }

    public int getMovesCount() { return movesCount; }
    public void setMovesCount(int movesCount) { this.movesCount = movesCount; }

    public String getPlayedAt() { return playedAt; }
    public void setPlayedAt(String playedAt) { this.playedAt = playedAt; }
}
