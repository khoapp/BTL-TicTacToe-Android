package com.example.tictactoe.game;

public class TicTacToeGame {

    public static final int PLAYER_X = 1;
    public static final int PLAYER_O = 2;
    public static final int EMPTY = 0;

    private int[] board;         // 25 ô bàn cờ (5x5)
    private int currentPlayer;   // Người đang đánh (PLAYER_X hoặc PLAYER_O)
    private int movesCount;      // Số nước đã đi
    private int winner;          // Người thắng (0 = chưa có)
    private int[] winningLine;   // 4 ô tạo thành đường thắng (để highlight)

    public TicTacToeGame() {
        reset();
    }

    /**
     * Đặt lại bàn cờ
     */
    public void reset() {
        board = new int[25];
        currentPlayer = PLAYER_X;
        movesCount = 0;
        winner = 0;
        winningLine = null;
    }

    /**
     * Thực hiện nước đi tại vị trí position (0-24)
     * @return true nếu hợp lệ, false nếu ô đã có quân
     */
    public boolean makeMove(int position) {
        if (position < 0 || position > 24) return false;
        if (board[position] != EMPTY) return false;
        if (winner != 0) return false;

        board[position] = currentPlayer;
        movesCount++;

        if (!checkWinner()) {
            // Đổi lượt
            currentPlayer = (currentPlayer == PLAYER_X) ? PLAYER_O : PLAYER_X;
        }
        return true;
    }

    /**
     * Kiểm tra xem đã có người thắng chưa (4 ô liên tiếp)
     * @return true nếu có người thắng
     */
    public boolean checkWinner() {
        int n = 5;
        for (int r = 0; r < n; r++) {
            for (int c = 0; c < n; c++) {
                int player = board[r * n + c];
                if (player == EMPTY) continue;

                // Ngang
                if (c <= n - 4 &&
                    player == board[r * n + c + 1] &&
                    player == board[r * n + c + 2] &&
                    player == board[r * n + c + 3]) {
                    winner = player;
                    winningLine = new int[]{r * n + c, r * n + c + 1, r * n + c + 2, r * n + c + 3};
                    return true;
                }
                // Dọc
                if (r <= n - 4 &&
                    player == board[(r + 1) * n + c] &&
                    player == board[(r + 2) * n + c] &&
                    player == board[(r + 3) * n + c]) {
                    winner = player;
                    winningLine = new int[]{r * n + c, (r + 1) * n + c, (r + 2) * n + c, (r + 3) * n + c};
                    return true;
                }
                // Chéo chính
                if (r <= n - 4 && c <= n - 4 &&
                    player == board[(r + 1) * n + c + 1] &&
                    player == board[(r + 2) * n + c + 2] &&
                    player == board[(r + 3) * n + c + 3]) {
                    winner = player;
                    winningLine = new int[]{r * n + c, (r + 1) * n + c + 1, (r + 2) * n + c + 2, (r + 3) * n + c + 3};
                    return true;
                }
                // Chéo phụ
                if (r <= n - 4 && c >= 3 &&
                    player == board[(r + 1) * n + c - 1] &&
                    player == board[(r + 2) * n + c - 2] &&
                    player == board[(r + 3) * n + c - 3]) {
                    winner = player;
                    winningLine = new int[]{r * n + c, (r + 1) * n + c - 1, (r + 2) * n + c - 2, (r + 3) * n + c - 3};
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Kiểm tra hòa
     */
    public boolean isDraw() {
        return movesCount == 25 && winner == 0;
    }

    /**
     * Trò chơi đã kết thúc chưa
     */
    public boolean isGameOver() {
        return winner != 0 || isDraw();
    }

    // ──── Getters ────

    public int[] getBoard() { return board; }

    public int getCurrentPlayer() { return currentPlayer; }

    public int getWinner() { return winner; }

    public int getMovesCount() { return movesCount; }

    public int[] getWinningLine() { return winningLine; }

    public boolean isCellEmpty(int position) {
        return board[position] == EMPTY;
    }

    /**
     * Trả về ký hiệu theo player
     */
    public static String getSymbol(int player) {
        return player == PLAYER_X ? "X" : "O";
    }
}
