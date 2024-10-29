package com.code.blinchik;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private int userId;
    private String currentDate;
    private TextView textViewDishes;
    private TextView textViewCalorieCounter;
    private TextView textViewWeight;
    private TextView textViewHeight;
    private TextView textViewBMI;
    private TextView textViewRecommendedCalories;

    private double totalCalories = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewDishes = findViewById(R.id.textViewDishes);
        textViewCalorieCounter = findViewById(R.id.textViewCalorieCounter);
        textViewWeight = findViewById(R.id.textViewWeight);
        textViewHeight = findViewById(R.id.textViewHeight);
        textViewBMI = findViewById(R.id.textViewBMI);
        textViewRecommendedCalories = findViewById(R.id.textViewRecommendedCalories);

        Button buttonAddDish = findViewById(R.id.buttonAddDish);
        buttonAddDish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddDishDialog();
            }
        });


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        currentDate = sdf.format(new Date());

        // получение userId
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        userId = sharedPreferences.getInt("current_user_id", -1);


        updateDishesList();


        updateUserInfo();

        ImageButton buttonSettings = findViewById(R.id.buttonSettings);
        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
    }

    private void updateUserInfo() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        User user = dbHelper.getUserInfo(userId);

        if (user != null) {
            textViewWeight.setText("Вес: " + user.getWeight() + " kg");
            textViewHeight.setText("Рост: " + user.getHeight() + " cm");

            double bmi = calculateBMI(user.getWeight(), user.getHeight());
            double recommendedCalories = calculateRecommendedCalories(user.getWeight(), user.getHeight());

            textViewBMI.setText("ИМТ: " + String.format("%.2f", bmi));
            textViewRecommendedCalories.setText("Рекомендуемая норма каллорий: " + recommendedCalories + " kcal");
        }
    }
    private double calculateBMI(double weight, double height) {
        if (height <= 0) return 0;
        return weight / ((height / 100) * (height / 100));
    }

    private double calculateRecommendedCalories(double weight, double height) {

        return 10 * weight + 6.25 * height - 5 * 30 + 5;
    }
    private void showAddDishDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Добавить блюдо");

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_dish, null);
        builder.setView(dialogView);

        EditText editTextDishName = dialogView.findViewById(R.id.editTextDishName);
        EditText editTextCalories = dialogView.findViewById(R.id.editTextCalories);
        EditText editTextProtein = dialogView.findViewById(R.id.editTextProtein);
        EditText editTextFat = dialogView.findViewById(R.id.editTextFat);
        EditText editTextCarbs = dialogView.findViewById(R.id.editTextCarbs);
        EditText editTextMealType = dialogView.findViewById(R.id.editTextMealType);

        builder.setPositiveButton("Добавить", (dialog, which) -> {
            String name = editTextDishName.getText().toString().trim();
            String caloriesStr = editTextCalories.getText().toString().trim();
            String proteinStr = editTextProtein.getText().toString().trim();
            String fatStr = editTextFat.getText().toString().trim();
            String carbsStr = editTextCarbs.getText().toString().trim();
            String mealType = editTextMealType.getText().toString().trim();

            if (name.isEmpty() || caloriesStr.isEmpty() || proteinStr.isEmpty() ||
                    fatStr.isEmpty() || carbsStr.isEmpty() || mealType.isEmpty()) {
                Toast.makeText(this, "Пожалуйста заполните все поля", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double calories = Double.parseDouble(caloriesStr);
                double protein = Double.parseDouble(proteinStr);
                double fat = Double.parseDouble(fatStr);
                double carbs = Double.parseDouble(carbsStr);


                addDishToDatabase(name, calories, protein, fat, carbs, userId, currentDate, mealType);


                updateDishesList();
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Введите корректное число", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void addDishToDatabase(String name, double calories, double protein, double fat, double carbs, int userId, String date, String mealType) {

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        dbHelper.addDish(name, calories, protein, fat, carbs, userId, date, mealType);


        totalCalories += calories;
        textViewCalorieCounter.setText("Всего каллорий " + totalCalories); // Обновление TextView
    }


    private void updateDishesList() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("current_user_id", -1);
        List<Dish> dishes = dbHelper.getDishesForToday(userId);

        StringBuilder dishesList = new StringBuilder("Блюда: ");
        totalCalories = 0;

        for (Dish dish : dishes) {
            dishesList.append("\n").append(dish.getName()).append(" (").append(dish.getCalories()).append(" cal)");
            totalCalories += dish.getCalories();
        }

        textViewDishes.setText(dishesList.toString());
        textViewCalorieCounter.setText("Всего каллорий " + totalCalories);
    }







}