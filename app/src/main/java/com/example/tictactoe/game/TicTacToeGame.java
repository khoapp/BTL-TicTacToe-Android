package com.example.tictactoe.game;

public class TicTacToeGame {

    public static final int PLAYER_X = 1;
    public static final int PLAYER_O = 2;
    public static final int EMPTY = 0;

    // Skill indices
    public static final int SKILL_BLOCK = 0;
    public static final int SKILL_DESTROY = 1;
    public static final int SKILL_DOUBLE_MOVE = 2;

    private int[] board;         // 25 ô bàn cờ (5x5)
    private int currentPlayer;   // Người đang đánh (PLAYER_X hoặc PLAYER_O)
    private int movesCount;      // Số ô đã bị chiếm
    private int winner;          // Người thắng (0 = chưa có)
    private int[] winningLine;   // 4 ô tạo thành đường thắng (để highlight)

    // Skill tracking
    private boolean[] p1SkillsUsed;
    private boolean[] p2SkillsUsed;
    
    // Active skill states
    private boolean opponentBlocked;
    private int extraMovesRemaining;
    private boolean isDestroyActive;

    public TicTacToeGame() {
        reset();
    }

    /**
     * Đặt lại bàn cờ và các kỹ năng
     */
    public void reset() {
        board = new int[25];
        currentPlayer = PLAYER_X;
        movesCount = 0;
        winner = 0;
        winningLine = null;
        
        p1SkillsUsed = new boolean[3];
        p2SkillsUsed = new boolean[3];
        opponentBlocked = false;
        extraMovesRemaining = 0;
        isDestroyActive = false;
    }

    /**
     * Sử dụng kỹ năng
     * @return true nếu sử dụng thành công
     */
    public boolean useSkill(int skillType) {
        boolean[] currentSkillsUsed = (currentPlayer == PLAYER_X) ? p1SkillsUsed : p2SkillsUsed;
        
        if (currentSkillsUsed[skillType]) {
            return false; // Đã sử dụng
        }
        
        currentSkillsUsed[skillType] = true;
        
        if (skillType == SKILL_BLOCK) {
            opponentBlocked = true;
            return true;
        } else if (skillType == SKILL_DOUBLE_MOVE) {
            extraMovesRemaining = 1; // Thêm 1 lượt đánh
            return true;
        } else if (skillType == SKILL_DESTROY) {
            isDestroyActive = true;
            return true;
        }
        
        return false;
    }

    /**
     * Hủy trạng thái destroy nếu người dùng đổi ý
     */
    public void cancelDestroySkill() {
        if (isDestroyActive) {
            isDestroyActive = false;
            // Trả lại kỹ năng chưa dùng
            boolean[] currentSkillsUsed = (currentPlayer == PLAYER_X) ? p1SkillsUsed : p2SkillsUsed;
            currentSkillsUsed[SKILL_DESTROY] = false;
        }
    }

    /**
     * Thực hiện phá hủy 1 quân cờ của đối phương
     */
    public boolean makeDestroyMove(int position) {
        if (!isDestroyActive) return false;
        if (position < 0 || position > 24) return false;
        
        int opponent = (currentPlayer == PLAYER_X) ? PLAYER_O : PLAYER_X;
        if (board[position] != opponent) return false; // Chỉ được phá cờ đối phương
        
        board[position] = EMPTY;
        movesCount--;
        isDestroyActive = false;
        
        // Phá xong thì mất lượt (trừ khi có các hiệu ứng khác)
        endTurn();
        return true;
    }

    /**
     * Thực hiện nước đi tại vị trí position (0-24)
     * @return true nếu hợp lệ, false nếu ô đã có quân
     */
    public boolean makeMove(int position) {
        if (isDestroyActive) {
            return makeDestroyMove(position);
        }

        if (position < 0 || position > 24) return false;
        if (board[position] != EMPTY) return false;
        if (winner != 0) return false;

        board[position] = currentPlayer;
        movesCount++;

        if (!checkWinner()) {
            endTurn();
        }
        return true;
    }

    private void endTurn() {
        if (extraMovesRemaining > 0) {
            extraMovesRemaining--;
            // Không đổi người chơi, được đánh tiếp
        } else if (opponentBlocked) {
            opponentBlocked = false;
            // Không đổi người chơi, đối phương mất lượt
        } else {
            // Đổi lượt
            currentPlayer = (currentPlayer == PLAYER_X) ? PLAYER_O : PLAYER_X;
        }
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

    public boolean[] getP1SkillsUsed() { return p1SkillsUsed; }
    public boolean[] getP2SkillsUsed() { return p2SkillsUsed; }
    public boolean isDestroyActive() { return isDestroyActive; }
    
    /**
     * Trả về ký hiệu theo player
     */
    public static String getSymbol(int player) {
        return player == PLAYER_X ? "X" : "O";
    }
}
