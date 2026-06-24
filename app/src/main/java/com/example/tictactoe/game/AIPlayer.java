package com.example.tictactoe.game;

/**
 * AI Player sử dụng thuật toán Minimax - bất khả chiến bại
 */
public class AIPlayer {

    private static final int WIN_SCORE = 10;
    private static final int LOSE_SCORE = -10;
    private static final int DRAW_SCORE = 0;

    private int aiPlayer;    // Ký hiệu của AI (PLAYER_X hoặc PLAYER_O)
    private int humanPlayer; // Ký hiệu của người

    public AIPlayer(int aiPlayer) {
        this.aiPlayer = aiPlayer;
        this.humanPlayer = (aiPlayer == TicTacToeGame.PLAYER_X)
                ? TicTacToeGame.PLAYER_O
                : TicTacToeGame.PLAYER_X;
    }

    /**
     * Trả về vị trí tốt nhất cho AI
     */
    public int getBestMove(int[] board) {
        int bestScore = Integer.MIN_VALUE;
        int bestMove = -1;

        for (int i = 0; i < 9; i++) {
            if (board[i] == TicTacToeGame.EMPTY) {
                board[i] = aiPlayer;
                int score = minimax(board, 0, false);
                board[i] = TicTacToeGame.EMPTY;

                if (score > bestScore) {
                    bestScore = score;
                    bestMove = i;
                }
            }
        }
        return bestMove;
    }

    /**
     * Thuật toán Minimax đệ quy
     */
    private int minimax(int[] board, int depth, boolean isMaximizing) {
        int winner = checkWinner(board);

        // Trường hợp cơ bản
        if (winner == aiPlayer) return WIN_SCORE - depth;
        if (winner == humanPlayer) return LOSE_SCORE + depth;
        if (isBoardFull(board)) return DRAW_SCORE;

        if (isMaximizing) {
            // Lượt của AI → tối đa hóa điểm
            int bestScore = Integer.MIN_VALUE;
            for (int i = 0; i < 9; i++) {
                if (board[i] == TicTacToeGame.EMPTY) {
                    board[i] = aiPlayer;
                    bestScore = Math.max(bestScore, minimax(board, depth + 1, false));
                    board[i] = TicTacToeGame.EMPTY;
                }
            }
            return bestScore;
        } else {
            // Lượt của người → tối thiểu hóa điểm
            int bestScore = Integer.MAX_VALUE;
            for (int i = 0; i < 9; i++) {
                if (board[i] == TicTacToeGame.EMPTY) {
                    board[i] = humanPlayer;
                    bestScore = Math.min(bestScore, minimax(board, depth + 1, true));
                    board[i] = TicTacToeGame.EMPTY;
                }
            }
            return bestScore;
        }
    }

    /**
     * Kiểm tra người thắng trên bảng (nội bộ cho minimax)
     * @return player thắng, hoặc 0 nếu chưa có
     */
    private int checkWinner(int[] board) {
        int[][] winCombos = {
                {0, 1, 2}, {3, 4, 5}, {6, 7, 8},
                {0, 3, 6}, {1, 4, 7}, {2, 5, 8},
                {0, 4, 8}, {2, 4, 6}
        };
        for (int[] combo : winCombos) {
            if (board[combo[0]] != TicTacToeGame.EMPTY &&
                board[combo[0]] == board[combo[1]] &&
                board[combo[1]] == board[combo[2]]) {
                return board[combo[0]];
            }
        }
        return 0;
    }

    private boolean isBoardFull(int[] board) {
        for (int cell : board) {
            if (cell == TicTacToeGame.EMPTY) return false;
        }
        return true;
    }

    public int getAiPlayer() { return aiPlayer; }
}
