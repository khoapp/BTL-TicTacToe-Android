package com.example.tictactoe.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.tictactoe.R;
import com.example.tictactoe.database.GameDAO;
import com.example.tictactoe.game.AIPlayer;
import com.example.tictactoe.game.TicTacToeGame;

public class GameActivity extends AppCompatActivity {

    // Nhận từ MenuActivity
    private int userId;
    private String username;
    private String gameMode;  // "AI" hoặc "PVP"
    private String player2Name;

    // Views
    private TextView tvPlayer1Name, tvPlayer2Name;
    private TextView tvScore1, tvScore2;
    private TextView tvTurnInfo, tvGameStatus;
    private Button[] cellButtons;
    private Button btnRestart, btnMenu;
    private CardView cardPlayer1, cardPlayer2;
    private Button btnP1Block, btnP1Destroy, btnP1Double;
    private Button btnP2Block, btnP2Destroy, btnP2Double;

    // Game logic
    private TicTacToeGame game;
    private AIPlayer aiPlayer;

    // Điểm số phiên
    private int score1 = 0, score2 = 0;

    private GameDAO gameDAO;
    private boolean aiTurn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Nhận dữ liệu từ MenuActivity
        userId = getIntent().getIntExtra(MenuActivity.EXTRA_USER_ID, -1);
        username = getIntent().getStringExtra(MenuActivity.EXTRA_USERNAME);
        gameMode = getIntent().getStringExtra(MenuActivity.EXTRA_GAME_MODE);
        player2Name = getIntent().getStringExtra(MenuActivity.EXTRA_PLAYER2_NAME);

        gameDAO = new GameDAO(this);
        game = new TicTacToeGame();

        if (MenuActivity.MODE_AI.equals(gameMode)) {
            // AI đánh ký hiệu O, người chơi đánh X
            aiPlayer = new AIPlayer(TicTacToeGame.PLAYER_O);
        }

        initViews();
        setupBoard();
        setupSkills();
        updateUI();
    }

    private void initViews() {
        tvPlayer1Name = findViewById(R.id.tvPlayer1Name);
        tvPlayer2Name = findViewById(R.id.tvPlayer2Name);
        tvScore1 = findViewById(R.id.tvScore1);
        tvScore2 = findViewById(R.id.tvScore2);
        tvTurnInfo = findViewById(R.id.tvTurnInfo);
        tvGameStatus = findViewById(R.id.tvGameStatus);
        btnRestart = findViewById(R.id.btnRestart);
        btnMenu = findViewById(R.id.btnMenu);
        cardPlayer1 = findViewById(R.id.cardPlayer1);
        cardPlayer2 = findViewById(R.id.cardPlayer2);

        btnP1Block = findViewById(R.id.btnP1Block);
        btnP1Destroy = findViewById(R.id.btnP1Destroy);
        btnP1Double = findViewById(R.id.btnP1Double);
        
        btnP2Block = findViewById(R.id.btnP2Block);
        btnP2Destroy = findViewById(R.id.btnP2Destroy);
        btnP2Double = findViewById(R.id.btnP2Double);

        tvPlayer1Name.setText(username + " (X)");
        tvPlayer2Name.setText(player2Name + " (O)");
    }

    private void setupBoard() {
        cellButtons = new Button[25];
        int[] cellIds = {
                R.id.cell0, R.id.cell1, R.id.cell2, R.id.cell3, R.id.cell4,
                R.id.cell5, R.id.cell6, R.id.cell7, R.id.cell8, R.id.cell9,
                R.id.cell10, R.id.cell11, R.id.cell12, R.id.cell13, R.id.cell14,
                R.id.cell15, R.id.cell16, R.id.cell17, R.id.cell18, R.id.cell19,
                R.id.cell20, R.id.cell21, R.id.cell22, R.id.cell23, R.id.cell24
        };

        for (int i = 0; i < 25; i++) {
            final int pos = i;
            cellButtons[i] = findViewById(cellIds[i]);
            cellButtons[i].setOnClickListener(v -> handleCellClick(pos));
        }

        btnRestart.setOnClickListener(v -> restartGame());
        btnMenu.setOnClickListener(v -> finish());
    }

    private void setupSkills() {
        btnP1Block.setOnClickListener(v -> useSkill(TicTacToeGame.PLAYER_X, TicTacToeGame.SKILL_BLOCK, btnP1Block));
        btnP1Destroy.setOnClickListener(v -> useSkill(TicTacToeGame.PLAYER_X, TicTacToeGame.SKILL_DESTROY, btnP1Destroy));
        btnP1Double.setOnClickListener(v -> useSkill(TicTacToeGame.PLAYER_X, TicTacToeGame.SKILL_DOUBLE_MOVE, btnP1Double));

        btnP2Block.setOnClickListener(v -> useSkill(TicTacToeGame.PLAYER_O, TicTacToeGame.SKILL_BLOCK, btnP2Block));
        btnP2Destroy.setOnClickListener(v -> useSkill(TicTacToeGame.PLAYER_O, TicTacToeGame.SKILL_DESTROY, btnP2Destroy));
        btnP2Double.setOnClickListener(v -> useSkill(TicTacToeGame.PLAYER_O, TicTacToeGame.SKILL_DOUBLE_MOVE, btnP2Double));
    }

    private void useSkill(int player, int skillType, Button btnSkill) {
        if (aiTurn) return;
        if (game.isGameOver()) return;
        if (game.getCurrentPlayer() != player) {
            tvGameStatus.setText("Chưa đến lượt của bạn!");
            return;
        }

        if (game.useSkill(skillType)) {
            btnSkill.setEnabled(false);
            btnSkill.setAlpha(0.5f);
            
            if (skillType == TicTacToeGame.SKILL_DESTROY) {
                tvGameStatus.setText("💣 Vui lòng chọn 1 quân cờ của đối phương để phá!");
                // Enable opponent's cells for destroying
                int opponent = (player == TicTacToeGame.PLAYER_X) ? TicTacToeGame.PLAYER_O : TicTacToeGame.PLAYER_X;
                for (int i = 0; i < 25; i++) {
                    if (game.getBoard()[i] == opponent) {
                        cellButtons[i].setEnabled(true);
                    } else {
                        cellButtons[i].setEnabled(false);
                    }
                }
            } else if (skillType == TicTacToeGame.SKILL_DOUBLE_MOVE) {
                tvGameStatus.setText("⚡ Bạn được đánh 2 lần liên tiếp!");
            } else if (skillType == TicTacToeGame.SKILL_BLOCK) {
                tvGameStatus.setText("🛡️ Đối phương sẽ bị mất 1 lượt tiếp theo!");
            }
            updateUI();
        }
    }

    private void handleCellClick(int position) {
        if (aiTurn) return; // Chặn click khi AI đang xử lý
        if (game.isGameOver()) return;
        
        boolean wasDestroy = game.isDestroyActive();

        if (wasDestroy) {
            int opponent = (game.getCurrentPlayer() == TicTacToeGame.PLAYER_X) ? TicTacToeGame.PLAYER_O : TicTacToeGame.PLAYER_X;
            if (game.getBoard()[position] != opponent) return;
        } else {
            if (!game.isCellEmpty(position)) return;
        }

        // Thực hiện nước đi
        if (!game.makeMove(position)) return;
        
        updateCellUI(position);
        
        if (wasDestroy) {
            tvGameStatus.setText("💣 Phá hủy thành công!");
            setBoardEnabled(true);
        } else {
            tvGameStatus.setText("");
        }
        
        updateUI();

        if (game.isGameOver()) {
            handleGameOver();
        } else if (MenuActivity.MODE_AI.equals(gameMode) && game.getCurrentPlayer() == TicTacToeGame.PLAYER_O) {
            // Đến lượt AI
            aiTurn = true;
            setBoardEnabled(false);
            new Handler().postDelayed(this::makeAIMove, 600);
        }
    }

    private void makeAIMove() {
        boolean[] aiSkillsUsed = game.getP2SkillsUsed();
        AIPlayer.AIAction action = aiPlayer.getBestAction(game.getBoard().clone(), aiSkillsUsed);

        if (action.skillToUse != -1) {
            if (action.skillToUse == TicTacToeGame.SKILL_DESTROY) {
                if (btnP2Destroy != null) {
                    btnP2Destroy.setEnabled(false);
                    btnP2Destroy.setAlpha(0.5f);
                }
                game.useSkill(TicTacToeGame.SKILL_DESTROY);
                game.makeMove(action.destroyPosition); // phá cờ, mất 1 lượt (của AI)
                updateCellUI(action.destroyPosition);
                tvGameStatus.setText("💣 AI đã phá hủy 1 quân cờ của bạn!");
            } else if (action.skillToUse == TicTacToeGame.SKILL_DOUBLE_MOVE) {
                if (btnP2Double != null) {
                    btnP2Double.setEnabled(false);
                    btnP2Double.setAlpha(0.5f);
                }
                game.useSkill(TicTacToeGame.SKILL_DOUBLE_MOVE);
                tvGameStatus.setText("⚡ AI sử dụng đánh 2 lần!");
            } else if (action.skillToUse == TicTacToeGame.SKILL_BLOCK) {
                if (btnP2Block != null) {
                    btnP2Block.setEnabled(false);
                    btnP2Block.setAlpha(0.5f);
                }
                game.useSkill(TicTacToeGame.SKILL_BLOCK);
                tvGameStatus.setText("🛡️ AI khóa lượt tiếp theo của bạn!");
            }
        }

        if (action.skillToUse != TicTacToeGame.SKILL_DESTROY && action.bestMove != -1) {
            game.makeMove(action.bestMove);
            updateCellUI(action.bestMove);
        }

        updateUI();
        
        if (game.isGameOver()) {
            aiTurn = false;
            setBoardEnabled(true);
            handleGameOver();
        } else if (game.getCurrentPlayer() == TicTacToeGame.PLAYER_O) {
            aiTurn = true;
            setBoardEnabled(false);
            new Handler().postDelayed(this::makeAIMove, 1000); // Tăng delay xíu để dễ nhìn AI đánh đúp
        } else {
            aiTurn = false;
            setBoardEnabled(true);
        }
    }

    private void updateCellUI(int position) {
        int player = game.getBoard()[position];
        if (player == TicTacToeGame.PLAYER_X) {
            cellButtons[position].setText("X");
            cellButtons[position].setTextColor(Color.parseColor("#1976D2")); // Xanh dương
            cellButtons[position].setEnabled(false);
        } else if (player == TicTacToeGame.PLAYER_O) {
            cellButtons[position].setText("O");
            cellButtons[position].setTextColor(Color.parseColor("#D32F2F")); // Đỏ
            cellButtons[position].setEnabled(false);
        } else {
            cellButtons[position].setText("");
            cellButtons[position].setBackgroundResource(R.drawable.cell_background);
            cellButtons[position].setEnabled(true);
        }
    }

    private void updateUI() {
        // Highlight card người đang đánh
        if (game.getCurrentPlayer() == TicTacToeGame.PLAYER_X) {
            cardPlayer1.setCardBackgroundColor(Color.parseColor("#E3F2FD"));
            cardPlayer2.setCardBackgroundColor(Color.WHITE);
            tvTurnInfo.setText("⬡ Lượt của " + username + " (X)");
        } else {
            cardPlayer2.setCardBackgroundColor(Color.parseColor("#FFEBEE"));
            cardPlayer1.setCardBackgroundColor(Color.WHITE);
            tvTurnInfo.setText("⬡ Lượt của " + player2Name + " (O)");
        }
    }

    private void handleGameOver() {
        int winner = game.getWinner();
        String resultForDB;
        String message;

        // Highlight đường thắng
        int[] winLine = game.getWinningLine();
        if (winLine != null) {
            for (int pos : winLine) {
                cellButtons[pos].setBackgroundColor(Color.parseColor("#FFF176"));
            }
        }

        if (winner == TicTacToeGame.PLAYER_X) {
            resultForDB = "WIN";
            message = "🏆 " + username + " THẮNG!";
            score1++;
            tvScore1.setText(String.valueOf(score1));
        } else if (winner == TicTacToeGame.PLAYER_O) {
            resultForDB = "LOSE";
            message = "🏆 " + player2Name + " THẮNG!";
            score2++;
            tvScore2.setText(String.valueOf(score2));
        } else {
            resultForDB = "DRAW";
            message = "🤝 Hòa! Không ai thắng.";
        }

        tvTurnInfo.setText(message);
        setBoardEnabled(false);

        // Lưu vào database
        gameDAO.saveGame(userId, player2Name, gameMode, resultForDB, game.getMovesCount());

        // Hiện dialog kết quả sau 1 giây
        new Handler().postDelayed(() -> showResultDialog(message, resultForDB), 800);
    }

    private void showResultDialog(String message, String result) {
        if (isFinishing()) return;

        String emoji;
        switch (result) {
            case "WIN": emoji = "🎉"; break;
            case "LOSE": emoji = "😔"; break;
            default: emoji = "🤝"; break;
        }

        new AlertDialog.Builder(this)
                .setTitle(emoji + " Kết Thúc!")
                .setMessage(message + "\n\nSố nước đi: " + game.getMovesCount())
                .setPositiveButton("Chơi Lại", (d, w) -> restartGame())
                .setNegativeButton("Về Menu", (d, w) -> finish())
                .setCancelable(false)
                .show();
    }

    private void restartGame() {
        game.reset();
        aiTurn = false;

        // Reset UI buttons
        for (Button btn : cellButtons) {
            btn.setText("");
            btn.setEnabled(true);
            btn.setBackgroundResource(R.drawable.cell_background);
            btn.setTextColor(Color.BLACK);
        }
        
        Button[] skillBtns = {btnP1Block, btnP1Destroy, btnP1Double, btnP2Block, btnP2Destroy, btnP2Double};
        for (Button btn : skillBtns) {
            if (btn != null) {
                btn.setEnabled(true);
                btn.setAlpha(1.0f);
            }
        }
        
        tvGameStatus.setText("");
        updateUI();
    }

    private void setBoardEnabled(boolean enabled) {
        for (int i = 0; i < 25; i++) {
            if (game.isCellEmpty(i)) {
                cellButtons[i].setEnabled(enabled);
            } else {
                cellButtons[i].setEnabled(false);
            }
        }
    }
}
