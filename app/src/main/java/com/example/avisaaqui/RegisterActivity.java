package com.example.avisaaqui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private EditText emailEditText, nameEditText, cpfEditText, passwordEditText;
    private Button registerButton, backToLoginButton;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Inicializar views
        emailEditText = findViewById(R.id.email);
        nameEditText = findViewById(R.id.name);
        cpfEditText = findViewById(R.id.cpf);
        passwordEditText = findViewById(R.id.password);
        registerButton = findViewById(R.id.btn_register);
        backToLoginButton = findViewById(R.id.btn_back_to_login);

        // Inicializar ApiService
        apiService = new ApiService(this);

        // Configurar listeners
        registerButton.setOnClickListener(v -> registerUser());
        backToLoginButton.setOnClickListener(v -> backToLogin());
    }

    private void registerUser() {
        String email = emailEditText.getText().toString().trim();
        String name = nameEditText.getText().toString().trim();
        String cpf = cpfEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Validações básicas
        if (email.isEmpty() || name.isEmpty() || cpf.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidEmail(email)) {
            Toast.makeText(this, "Email inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidCPF(cpf)) {
            Toast.makeText(this, "CPF inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "A senha deve ter pelo menos 6 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        // Chamar API de cadastro
        apiService.registerUser(email, name, cpf, password, new ApiService.ApiCallback() {
            @Override
            public void onSuccess(String message) {
                runOnUiThread(() -> {
                    Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                    // Voltar para a tela de login após cadastro bem-sucedido
                    backToLogin();
                });
            }

            @Override
            public void onSuccess(Object response) {
                // Método não utilizado neste contexto
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> Toast.makeText(RegisterActivity.this, error, Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void backToLogin() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidCPF(String cpf) {
        // Remove caracteres não numéricos
        cpf = cpf.replaceAll("[^0-9]", "");

        // Verifica se tem 11 dígitos
        if (cpf.length() != 11) {
            return false;
        }

        // Verifica se todos os dígitos são iguais
        if (cpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        // Validação básica do CPF (você pode implementar uma validação mais robusta)
        return true;
    }
}