 TicTacToe game



 Tính năng

- Đăng ký / Đăng nhập với lưu trữ SQLite + mã hóa MD5
- Chơi vs AI( dùng thuật toán Minimax )
- Chơi 2 người (cùng 1 thiết bị)
- Lịch sử trận đấu với thống kê Thắng/Thua/Hòa
- Cài đặt: đổi tên, xóa lịch sử, đăng xuất
- Tự động đăng nhập (SharedPreferences)


Đáp ứng yêu cầu môn học

1. Android + Java : Native Android API 24+, Java
2. Đơn / Đa người : Mode AI (Minimax) & PVP
3. Giải đúng bài toán : Logic đầy đủ: thắng/thua/hòa, 8 tổ hợp
4. 2+ Activity trao đổi : Intent + Bundle: Login → Menu → Game → History
5. 2+ Layout : ConstraintLayout + GridLayout + LinearLayout + ScrollView
6. Lưu trữ : SQLite (users + game_history) + SharedPreferences
7. Đăng nhập/đăng ký : LoginActivity + RegisterActivity + UserDAO

Luồng dữ liệu giữa Activity


LoginActivity ──userId,username──► MenuActivity
MenuActivity  ──userId,username,gameMode──► GameActivity
MenuActivity  ──userId──► HistoryActivity
GameActivity  ──kết quả lưu DB──► (refreshed by MenuActivity.onResume)

