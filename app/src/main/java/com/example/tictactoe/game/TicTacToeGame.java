package com.example.tictactoe.game;

public class TicTacToeGame {

    public static final int PLAYER_X = 1;
    public static final int PLAYER_O = 2;
    public static final int EMPTY = 0;

    // 8 tổ hợp thắng
    private static final int[][] WIN_COMBINATIONS = {
            {0, 1, 2}, // Hàng 1
            {3, 4, 5}, // Hàng 2
            {6, 7, 8}, // Hàng 3
            {0, 3, 6}, // Cột 1
            {1, 4, 7}, // Cột 2
            {2, 5, 8}, // Cột 3
            {0, 4, 8}, // Đường chéo chính
            {2, 4, 6}  // Đường chéo phụ
    };

    private int[] board;         // 9 ô bàn cờ
    private int currentPlayer;   // Người đang đánh (PLAYER_X hoặc PLAYER_O)
    private int movesCount;      // Số nước đã đi
    private int winner;          // Người thắng (0 = chưa có)
    private int[] winningLine;   // 3 ô tạo thành đường thắng (để highlight)

    public TicTacToeGame() {
        reset();
    }

    /**
     * Đặt lại bàn cờ
     */
    public void reset() {
        board = new int[9];
        currentPlayer = PLAYER_X;
        movesCount = 0;
        winner = 0;
        winningLine = null;
    }

    /**
     * Thực hiện nước đi tại vị trí position (0-8)
     * @return true nếu hợp lệ, false nếu ô đã có quân
     */
    public boolean makeMove(int position) {
        if (position < 0 || position > 8) return false;
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
     * Kiểm tra xem đã có người thắng chưa
     * @return true nếu có người thắng
     */
    public boolean checkWinner() {
        for (int[] combo : WIN_COMBINATIONS) {
            if (board[combo[0]] != EMPTY &&
                board[combo[0]] == board[combo[1]] &&
                board[combo[1]] == board[combo[2]]) {
                winner = board[combo[0]];
                winningLine = combo;
                return true;
            }
        }
        return false;
    }

    /**
     * Kiểm tra hòa
     */
    public boolean isDraw() {
        return movesCount == 9 && winner == 0;
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
