package com.example.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Название и версия базы данных
    private static final String DATABASE_NAME = "app.db";
    private static final int SCHEMA = 1;

    // Название таблицы и имена столбцов
    static final String TABLE_NAME = "users";
    public static final String COLUMN_ID = "_id";
    public static final  String COLUMN_NAME = "name";
    public static final String COLUMN_LOGIN = "login";
    public static final String COLUMN_PASSWORD = "password";

    // Конструктор
    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, SCHEMA);
    }

    // Вызывается при первом создании базы данных
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Создание таблицы с указанными столбцами
        db.execSQL(
                "CREATE TABLE " + TABLE_NAME + "(" +
                        COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_NAME + " TEXT, " +
                        COLUMN_LOGIN + " TEXT UNIQUE, " +
                        COLUMN_PASSWORD + " TEXT" +
                        ");"
        );
    }

    // Вызывается при обновлении базы данных
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Удаление существующей таблицы и её воссоздание
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME);
        onCreate(db);
    }
}
