package com.code.blinchik;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Reg extends AppCompatActivity {

    private EditText editTextNewUsername;
    private EditText editTextNewPassword;
    private EditText editTextWeight;
    private EditText editTextHeight;
    private TextView textViewRegError;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);

        editTextNewUsername = findViewById(R.id.editTextNewUsername);
        editTextNewPassword = findViewById(R.id.editTextNewPassword);
        editTextWeight = findViewById(R.id.editTextWeight);
        editTextHeight = findViewById(R.id.editTextHeight);
        textViewRegError = findViewById(R.id.textViewRegError);
        Button buttonRegister = findViewById(R.id.buttonRegister);

        dbHelper = new DatabaseHelper(this);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newUsername = editTextNewUsername.getText().toString();
                String newPassword = editTextNewPassword.getText().toString();
                String weightText = editTextWeight.getText().toString();
                String heightText = editTextHeight.getText().toString();

                if (newUsername.isEmpty() || newPassword.isEmpty() || weightText.isEmpty() || heightText.isEmpty()) {
                    textViewRegError.setText("Введите все данные");
                    textViewRegError.setVisibility(View.VISIBLE);
                } else if (dbHelper.checkUser(newUsername, newPassword) != -1) {
                    textViewRegError.setText("Пользователь уже существует");
                    textViewRegError.setVisibility(View.VISIBLE);
                } else {
                    try {
                        double weight = Double.parseDouble(weightText);
                        double height = Double.parseDouble(heightText);
                        dbHelper.addUser(newUsername, newPassword, weight, height);
                        textViewRegError.setVisibility(View.GONE);

                        Intent intent = new Intent(Reg.this, Auth.class);
                        startActivity(intent);
                        finish();
                    } catch (NumberFormatException e) {
                        textViewRegError.setText("Некорректный вес или рост");
                        textViewRegError.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }
}
