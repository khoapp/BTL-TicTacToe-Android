# 🎮 TicTacToe Android

Ứng dụng game TicTacToe cho Android (Java), xây dựng cho môn học Lập Trình Android.

## 📱 Tính năng

- ✅ **Đăng ký / Đăng nhập** với lưu trữ SQLite + mã hóa MD5
- ✅ **Chơi vs AI** (Minimax – bất khả chiến bại)
- ✅ **Chơi 2 người** (cùng 1 thiết bị)
- ✅ **Lịch sử trận đấu** với thống kê Thắng/Thua/Hòa
- ✅ **Cài đặt**: đổi tên, xóa lịch sử, đăng xuất
- ✅ **Tự động đăng nhập** (SharedPreferences)

## 🛠️ Cài đặt

1. Clone hoặc copy thư mục `TicTacToe/` vào Android Studio
2. **File > Open** → chọn thư mục `TicTacToe/`
3. Đợi Gradle sync
4. Chạy trên emulator hoặc thiết bị thật (API 24+)

## 📂 Cấu trúc

```
app/src/main/java/com/example/tictactoe/
├── activity/        # 7 Activities
├── database/        # DatabaseHelper, UserDAO, GameDAO
├── game/            # TicTacToeGame, AIPlayer (Minimax)
├── model/           # User, GameRecord
└── adapter/         # HistoryAdapter (RecyclerView)
```

## 📋 Đáp ứng yêu cầu môn học

| Yêu cầu | Cách thực hiện |
|---|---|
| Android + Java | Native Android API 24+, Java |
| Đơn / Đa người | Mode AI (Minimax) & PVP |
| Giải đúng bài toán | Logic đầy đủ: thắng/thua/hòa, 8 tổ hợp |
| 2+ Activity trao đổi | Intent + Bundle: Login→Menu→Game→History |
| 2+ Layout | ConstraintLayout + GridLayout + LinearLayout + ScrollView |
| Lưu trữ | SQLite (users + game_history) + SharedPreferences |
| Đăng nhập/đăng ký | LoginActivity + RegisterActivity + UserDAO |

## 🔄 Luồng dữ liệu giữa Activity

```
LoginActivity ──userId,username──► MenuActivity
MenuActivity  ──userId,username,gameMode──► GameActivity
MenuActivity  ──userId──► HistoryActivity
GameActivity  ──kết quả lưu DB──► (refreshed by MenuActivity.onResume)
```
