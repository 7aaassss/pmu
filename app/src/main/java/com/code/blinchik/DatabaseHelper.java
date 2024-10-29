package com.code.blinchik;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {


    private static final String DATABASE_NAME = "blin.db";
    private static final int DATABASE_VERSION = 4;

    private static final String TABLE_USERS = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";

    private static final String COLUMN_WEIGHT = "weight";
    private static final String COLUMN_HEIGHT = "height";


    private static final String TABLE_DISHES = "dishes";
    private static final String COLUMN_DISH_ID = "id";
    private static final String COLUMN_DISH_NAME = "name";
    private static final String COLUMN_CALORIES = "calories";
    private static final String COLUMN_PROTEIN = "protein";
    private static final String COLUMN_FAT = "fat";
    private static final String COLUMN_CARBS = "uglevody";
    private static final String COLUMN_USER_ID = "user";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_MEAL_TYPE = "meal_type";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String users_table = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USERNAME + " TEXT,"
                + COLUMN_PASSWORD + " TEXT,"
                + COLUMN_WEIGHT + " REAL,"
                + COLUMN_HEIGHT + " REAL" + ")";
        db.execSQL(users_table);

        String dishes_table = "CREATE TABLE " + TABLE_DISHES + "("
                + COLUMN_DISH_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_DISH_NAME + " TEXT,"
                + COLUMN_CALORIES + " REAL,"
                + COLUMN_PROTEIN + " REAL,"
                + COLUMN_FAT + " REAL,"
                + COLUMN_CARBS + " REAL,"
                + COLUMN_USER_ID + " INTEGER,"
                + COLUMN_DATE + " TEXT,"
                + COLUMN_MEAL_TYPE + " TEXT,"
                + "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + "))";
        db.execSQL(dishes_table);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN " + COLUMN_WEIGHT + " REAL");
            db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN " + COLUMN_HEIGHT + " REAL");
        }
    }



    public void addUser(String username, String password, double weight, double height) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_WEIGHT, weight);
        values.put(COLUMN_HEIGHT, height);

        db.insert(TABLE_USERS, null, values);
        db.close();
    }


    public int checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM Users WHERE username = ? AND password = ?", new String[]{username, password});

        int userId = -1;
        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex("id");
            if (columnIndex != -1) {
                userId = cursor.getInt(columnIndex);
            }
            cursor.close();
        }

        db.close();
        return userId;  // id пользователя или -1
    }

    public User getUserInfo(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        User user = null;

        Cursor cursor = db.query(
                TABLE_USERS,
                new String[]{COLUMN_ID, COLUMN_USERNAME, COLUMN_WEIGHT, COLUMN_HEIGHT},
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(userId)},
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
            String username = cursor.getString(cursor.getColumnIndex(COLUMN_USERNAME));
            double weight = cursor.getDouble(cursor.getColumnIndex(COLUMN_WEIGHT));
            double height = cursor.getDouble(cursor.getColumnIndex(COLUMN_HEIGHT));
            user = new User(id, username, weight, height);
            cursor.close();
        }
        db.close();
        return user;
    }


    public void addDish(String name, double calories, double protein, double fat, double carbs, int userId, String date, String mealType) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DISH_NAME, name);
        values.put(COLUMN_CALORIES, calories);
        values.put(COLUMN_PROTEIN, protein);
        values.put(COLUMN_FAT, fat);
        values.put(COLUMN_CARBS, carbs);
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_MEAL_TYPE, mealType);

        db.insert(TABLE_DISHES, null, values);
        db.close();
    }

    public List<Dish> getDishesForUser(int userId) {
        List<Dish> dishes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_DISHES, null, COLUMN_USER_ID + " = ?", new String[]{String.valueOf(userId)}, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex(COLUMN_DISH_ID));
                String name = cursor.getString(cursor.getColumnIndex(COLUMN_DISH_NAME));
                double calories = cursor.getDouble(cursor.getColumnIndex(COLUMN_CALORIES));
                double protein = cursor.getDouble(cursor.getColumnIndex(COLUMN_PROTEIN));
                double fat = cursor.getDouble(cursor.getColumnIndex(COLUMN_FAT));
                double carbs = cursor.getDouble(cursor.getColumnIndex(COLUMN_CARBS));
                String date = cursor.getString(cursor.getColumnIndex(COLUMN_DATE));
                String mealType = cursor.getString(cursor.getColumnIndex(COLUMN_MEAL_TYPE));

                dishes.add(new Dish(id, name, calories, protein, fat, carbs, date, mealType));
            }
            cursor.close();
        }
        db.close();
        return dishes;
    }


    public List<Dish> getDishesForToday(int userId) {
        List<Dish> dishes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();


        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());


        Cursor cursor = db.query(TABLE_DISHES, null,
                COLUMN_USER_ID + " = ? AND " + COLUMN_DATE + " = ?",
                new String[]{String.valueOf(userId), currentDate},
                null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex(COLUMN_DISH_ID));
                String name = cursor.getString(cursor.getColumnIndex(COLUMN_DISH_NAME));
                double calories = cursor.getDouble(cursor.getColumnIndex(COLUMN_CALORIES));
                double protein = cursor.getDouble(cursor.getColumnIndex(COLUMN_PROTEIN));
                double fat = cursor.getDouble(cursor.getColumnIndex(COLUMN_FAT));
                double carbs = cursor.getDouble(cursor.getColumnIndex(COLUMN_CARBS));
                String date = cursor.getString(cursor.getColumnIndex(COLUMN_DATE));
                String mealType = cursor.getString(cursor.getColumnIndex(COLUMN_MEAL_TYPE));

                dishes.add(new Dish(id, name, calories, protein, fat, carbs, date, mealType));
            }
            cursor.close();
        }
        db.close();
        return dishes;
    }

    public boolean updateUserCredentials(int userId, String newLogin, String newPassword, double newWeight, double newHeight) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", newLogin);
        contentValues.put("password", newPassword);
        contentValues.put("weight", newWeight);
        contentValues.put("height", newHeight);

        int result = db.update("users", contentValues, "id = ?", new String[]{String.valueOf(userId)});
        return result > 0;
    }





}
