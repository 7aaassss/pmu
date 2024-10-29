package com.code.blinchik;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    private EditText editTextNewLogin;
    private EditText editTextNewPassword;
    private EditText editTextNewWeight;
    private EditText editTextNewHeight;
    private Button buttonSaveChanges;
    private DatabaseHelper databaseHelper;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        databaseHelper = new DatabaseHelper(this);

        editTextNewLogin = findViewById(R.id.editTextNewLogin);
        editTextNewPassword = findViewById(R.id.editTextNewPassword);
        editTextNewWeight = findViewById(R.id.editTextNewWeight);
        editTextNewHeight = findViewById(R.id.editTextNewHeight);
        buttonSaveChanges = findViewById(R.id.buttonSaveChanges);


        userId = 1;

        buttonSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newLogin = editTextNewLogin.getText().toString();
                String newPassword = editTextNewPassword.getText().toString();
                String weightStr = editTextNewWeight.getText().toString();
                String heightStr = editTextNewHeight.getText().toString();


                if (newLogin.isEmpty() || newPassword.isEmpty() || weightStr.isEmpty() || heightStr.isEmpty()) {
                    Toast.makeText(SettingsActivity.this, "Заполните все поля!", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        double newWeight = Double.parseDouble(weightStr);
                        double newHeight = Double.parseDouble(heightStr);


                        boolean isUpdated = databaseHelper.updateUserCredentials(userId, newLogin, newPassword, newWeight, newHeight);
                        if (isUpdated) {
                            Toast.makeText(SettingsActivity.this, "Изменения сохраненый!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SettingsActivity.this, "Ошибка при сохранении", Toast.LENGTH_SHORT).show();
                        }
                    } catch (NumberFormatException e) {
                        Toast.makeText(SettingsActivity.this, "Введите валидные данные", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        Button buttonLogout = findViewById(R.id.buttonLogout);
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.apply();


                startActivity(new Intent(SettingsActivity.this, Auth.class));
                finish();
            }
        });

    }

    private void logout() {

        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

        startActivity(new Intent(SettingsActivity.this, Auth.class));
        finish();
    }
}
