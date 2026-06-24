package com.example.tictactoe.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "tictactoe.db";
    private static final int DATABASE_VERSION = 1;

    // Table: users
    public static final String TABLE_USERS = "users";
    public static final String COL_USER_ID = "id";
    public static final String COL_USER_NAME = "username";
    public static final String COL_USER_EMAIL = "email";
    public static final String COL_USER_PASSWORD = "password";
    public static final String COL_USER_CREATED_AT = "created_at";

    // Table: game_history
    public static final String TABLE_GAME_HISTORY = "game_history";
    public static final String COL_GAME_ID = "id";
    public static final String COL_GAME_USER_ID = "user_id";
    public static final String COL_GAME_OPPONENT = "opponent";
    public static final String COL_GAME_MODE = "game_mode";
    public static final String COL_GAME_RESULT = "result";
    public static final String COL_GAME_MOVES = "moves_count";
    public static final String COL_GAME_PLAYED_AT = "played_at";

    private static final String CREATE_TABLE_USERS =
            "CREATE TABLE " + TABLE_USERS + " (" +
            COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_USER_NAME + " TEXT NOT NULL, " +
            COL_USER_EMAIL + " TEXT UNIQUE NOT NULL, " +
            COL_USER_PASSWORD + " TEXT NOT NULL, " +
            COL_USER_CREATED_AT + " TEXT" +
            ");";

    private static final String CREATE_TABLE_GAME_HISTORY =
            "CREATE TABLE " + TABLE_GAME_HISTORY + " (" +
            COL_GAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_GAME_USER_ID + " INTEGER, " +
            COL_GAME_OPPONENT + " TEXT, " +
            COL_GAME_MODE + " TEXT, " +
            COL_GAME_RESULT + " TEXT, " +
            COL_GAME_MOVES + " INTEGER, " +
            COL_GAME_PLAYED_AT + " TEXT, " +
            "FOREIGN KEY (" + COL_GAME_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_USER_ID + ")" +
            ");";

    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_GAME_HISTORY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GAME_HISTORY);
        onCreate(db);
    }
}
