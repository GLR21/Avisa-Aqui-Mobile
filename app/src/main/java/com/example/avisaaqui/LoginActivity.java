package com.example.avisaaqui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText documentEditText, passwordEditText;
    private Button loginButton;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        documentEditText = findViewById(R.id.document);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.btn_login);

        apiService = new ApiService(this);

        loginButton.setOnClickListener(v -> loginUser());
    }

    private void loginUser() {
        String document = documentEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (document.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        apiService.loginUser(document, password, this, new ApiService.ApiCallback() {
            @Override
            public void onSuccess(String message) {
                runOnUiThread(() -> {
                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                    // Redirecionar para MainActivity após login
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                });
            }

            @Override
            public void onSuccess(Object response) {

            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show());
            }
        });
    }
}