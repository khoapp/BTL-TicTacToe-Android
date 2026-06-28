package com.example.tictactoe.game;

public class AIPlayer {

    private static final int MAX_DEPTH = 4; // Giới hạn độ sâu để tránh treo máy
    private int aiPlayer;    // Ký hiệu của AI (PLAYER_X hoặc PLAYER_O)
    private int humanPlayer; // Ký hiệu của người

    public AIPlayer(int aiPlayer) {
        this.aiPlayer = aiPlayer;
        this.humanPlayer = (aiPlayer == TicTacToeGame.PLAYER_X)
                ? TicTacToeGame.PLAYER_O
                : TicTacToeGame.PLAYER_X;
    }

    public static class AIAction {
        public int skillToUse = -1; // -1: không dùng, 0: Block, 1: Destroy, 2: DoubleMove
        public int destroyPosition = -1;
        public int bestMove = -1;
    }

    public AIAction getBestAction(int[] board, boolean[] aiSkillsUsed) {
        AIAction action = new AIAction();
        action.bestMove = getBestMove(board);
        
        int currentScore = evaluateBoard(board);
        
        // 1. Kiểm tra nguy hiểm: Nếu điểm số hiện tại rất thấp (người chơi sắp thắng)
        if (currentScore <= -100) {
            // Ưu tiên dùng Destroy nếu chưa dùng
            if (!aiSkillsUsed[TicTacToeGame.SKILL_DESTROY]) {
                int bestDestroyPos = -1;
                int bestDestroyScore = currentScore;
                
                for (int i = 0; i < 25; i++) {
                    if (board[i] == humanPlayer) {
                        board[i] = TicTacToeGame.EMPTY;
                        int newScore = evaluateBoard(board);
                        board[i] = humanPlayer;
                        
                        if (newScore > bestDestroyScore) {
                            bestDestroyScore = newScore;
                            bestDestroyPos = i;
                        }
                    }
                }
                
                if (bestDestroyPos != -1 && bestDestroyScore > currentScore + 50) {
                    action.skillToUse = TicTacToeGame.SKILL_DESTROY;
                    action.destroyPosition = bestDestroyPos;
                    action.bestMove = -1; 
                    return action;
                }
            }
            
            // Nếu không dùng được Destroy hoặc Destroy không đủ hiệu quả, dùng Block
            if (!aiSkillsUsed[TicTacToeGame.SKILL_BLOCK]) {
                action.skillToUse = TicTacToeGame.SKILL_BLOCK;
                return action;
            }
        }
        
        // 2. Cơ hội tấn công bằng Double Move
        if (!aiSkillsUsed[TicTacToeGame.SKILL_DOUBLE_MOVE]) {
            if (action.bestMove != -1 && board[action.bestMove] == TicTacToeGame.EMPTY) {
                board[action.bestMove] = aiPlayer;
                if (checkWinner(board) != aiPlayer) { 
                    int secondMove = getBestMove(board);
                    if (secondMove != -1) {
                        board[secondMove] = aiPlayer;
                        if (checkWinner(board) == aiPlayer) {
                            action.skillToUse = TicTacToeGame.SKILL_DOUBLE_MOVE;
                        }
                        board[secondMove] = TicTacToeGame.EMPTY;
                    }
                }
                board[action.bestMove] = TicTacToeGame.EMPTY;
                
                if (action.skillToUse == TicTacToeGame.SKILL_DOUBLE_MOVE) {
                    return action;
                }
            }
        }
        
        // 3. Dùng ngẫu nhiên Block nếu có lợi thế nhẹ
        if (!aiSkillsUsed[TicTacToeGame.SKILL_BLOCK] && currentScore >= 10 && Math.random() < 0.2) {
            action.skillToUse = TicTacToeGame.SKILL_BLOCK;
        }

        return action;
    }

    public int getBestMove(int[] board) {
        int bestScore = Integer.MIN_VALUE;
        int bestMove = -1;

        // Ưu tiên duyệt các ô ở giữa trước để cắt tỉa Alpha-Beta nhanh hơn
        int[] moveOrder = {
                12, 11, 13, 7, 17, 6, 8, 16, 18,
                1, 2, 3, 5, 9, 10, 14, 15, 19, 21, 22, 23,
                0, 4, 20, 24
        };

        for (int i : moveOrder) {
            if (board[i] == TicTacToeGame.EMPTY) {
                board[i] = aiPlayer;
                int score = minimax(board, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, false);
                board[i] = TicTacToeGame.EMPTY;

                if (score > bestScore) {
                    bestScore = score;
                    bestMove = i;
                }
            }
        }

        // Đề phòng trường hợp không tìm thấy (thường không xảy ra trừ khi bàn cờ đầy)
        if (bestMove == -1) {
            for (int i = 0; i < 25; i++) {
                if (board[i] == TicTacToeGame.EMPTY) return i;
            }
        }

        return bestMove;
    }

    private int minimax(int[] board, int depth, int alpha, int beta, boolean isMaximizing) {
        int winner = checkWinner(board);

        // Đánh giá trạng thái kết thúc
        if (winner == aiPlayer) return 100000 - depth;
        if (winner == humanPlayer) return -100000 + depth;
        if (isBoardFull(board)) return 0;

        // Nếu đạt đến độ sâu tối đa, dùng hàm đánh giá heuristic
        if (depth >= MAX_DEPTH) {
            return evaluateBoard(board);
        }

        int[] moveOrder = {
                12, 11, 13, 7, 17, 6, 8, 16, 18,
                1, 2, 3, 5, 9, 10, 14, 15, 19, 21, 22, 23,
                0, 4, 20, 24
        };

        if (isMaximizing) {
            int maxEval = Integer.MIN_VALUE;
            for (int i : moveOrder) {
                if (board[i] == TicTacToeGame.EMPTY) {
                    board[i] = aiPlayer;
                    int eval = minimax(board, depth + 1, alpha, beta, false);
                    board[i] = TicTacToeGame.EMPTY;
                    maxEval = Math.max(maxEval, eval);
                    alpha = Math.max(alpha, eval);
                    if (beta <= alpha) break; // Cắt tỉa Alpha-Beta
                }
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (int i : moveOrder) {
                if (board[i] == TicTacToeGame.EMPTY) {
                    board[i] = humanPlayer;
                    int eval = minimax(board, depth + 1, alpha, beta, true);
                    board[i] = TicTacToeGame.EMPTY;
                    minEval = Math.min(minEval, eval);
                    beta = Math.min(beta, eval);
                    if (beta <= alpha) break; // Cắt tỉa Alpha-Beta
                }
            }
            return minEval;
        }
    }

    private int evaluateBoard(int[] board) {
        int score = 0;
        int n = 5;
        // Duyệt qua tất cả các tổ hợp 4 ô liên tiếp (tương tự như checkWinner nhưng chỉ tính điểm)
        for (int r = 0; r < n; r++) {
            for (int c = 0; c < n; c++) {
                // Ngang
                if (c <= n - 4) score += evaluateLine(board, r*n+c, r*n+c+1, r*n+c+2, r*n+c+3);
                // Dọc
                if (r <= n - 4) score += evaluateLine(board, r*n+c, (r+1)*n+c, (r+2)*n+c, (r+3)*n+c);
                // Chéo chính
                if (r <= n - 4 && c <= n - 4) score += evaluateLine(board, r*n+c, (r+1)*n+c+1, (r+2)*n+c+2, (r+3)*n+c+3);
                // Chéo phụ
                if (r <= n - 4 && c >= 3) score += evaluateLine(board, r*n+c, (r+1)*n+c-1, (r+2)*n+c-2, (r+3)*n+c-3);
            }
        }
        return score;
    }

    private int evaluateLine(int[] board, int i1, int i2, int i3, int i4) {
        int aiCount = 0;
        int humanCount = 0;
        int[] cells = {board[i1], board[i2], board[i3], board[i4]};

        for (int cell : cells) {
            if (cell == aiPlayer) aiCount++;
            else if (cell == humanPlayer) humanCount++;
        }

        // Nếu một hàng có cả cờ của 2 bên thì không tạo thành đường thắng được
        if (aiCount > 0 && humanCount > 0) return 0;

        if (aiCount > 0) {
            if (aiCount == 1) return 1;
            if (aiCount == 2) return 10;
            if (aiCount == 3) return 100;
            if (aiCount == 4) return 10000;
        } else if (humanCount > 0) {
            if (humanCount == 1) return -1;
            if (humanCount == 2) return -10;
            if (humanCount == 3) return -100;
            if (humanCount == 4) return -10000;
        }
        return 0;
    }

    private int checkWinner(int[] board) {
        int n = 5;
        for (int r = 0; r < n; r++) {
            for (int c = 0; c < n; c++) {
                int player = board[r * n + c];
                if (player == TicTacToeGame.EMPTY) continue;

                if (c <= n - 4 && player == board[r*n+c+1] && player == board[r*n+c+2] && player == board[r*n+c+3]) return player;
                if (r <= n - 4 && player == board[(r+1)*n+c] && player == board[(r+2)*n+c] && player == board[(r+3)*n+c]) return player;
                if (r <= n - 4 && c <= n - 4 && player == board[(r+1)*n+c+1] && player == board[(r+2)*n+c+2] && player == board[(r+3)*n+c+3]) return player;
                if (r <= n - 4 && c >= 3 && player == board[(r+1)*n+c-1] && player == board[(r+2)*n+c-2] && player == board[(r+3)*n+c-3]) return player;
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
