package com.code.blinchik;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Auth extends AppCompatActivity {

    private EditText editTextUsername;
    private EditText editTextPassword;
    private TextView textViewError;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        setTheme(R.style.splash_screen);


        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        int savedUserId = preferences.getInt("current_user_id", -1); // -1 если нет данных

        if (savedUserId != -1) {

            startActivity(new Intent(Auth.this, MainActivity.class));
            finish();
            return;
        }


        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        textViewError = findViewById(R.id.textViewError);
        Button buttonLogin = findViewById(R.id.buttonLogin);
        Button buttonRegister = findViewById(R.id.buttonRegister);


        dbHelper = new DatabaseHelper(this);


        buttonLogin.setOnClickListener(v -> {
            String username = editTextUsername.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();


            int userId = dbHelper.checkUser(username, password);
            if (userId != -1) { //  пользователь найден
                saveUserIdToPreferences(userId);
                textViewError.setVisibility(View.GONE);
                startActivity(new Intent(Auth.this, MainActivity.class));
                finish();
            } else {
                textViewError.setVisibility(View.VISIBLE);
                textViewError.setText("Неверное имя пользователя или пароль");
            }
        });

        buttonRegister.setOnClickListener(v -> startActivity(new Intent(Auth.this, Reg.class)));
    }



    private void saveUserIdToPreferences(int userId) {
        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("current_user_id", userId);
        editor.apply();
    }
}
