package com.example.avisaaqui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private Button button_abrir_insercao = null;
    private Button button_abrir_consulta = null;
    private Button button_abrir_requisicao = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button_abrir_insercao = findViewById(R.id.button_abrir_insercao);
        button_abrir_consulta = findViewById(R.id.button_abrir_consulta);
        button_abrir_requisicao = findViewById(R.id.button_abrir_requisicao);

        button_abrir_insercao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, FormActivity.class);
                startActivity(intent);
            }
        });

        button_abrir_consulta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ViewActivity.class);
                startActivity(intent);
            }
        });

        button_abrir_requisicao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RequestActivity.class);
                startActivity(intent);
            }
        });

        ExtendedFloatingActionButton button_float_insercao = findViewById(R.id.fab_inserir);
        button_float_insercao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, FormActivity.class);
                startActivity(intent);
            }
        });

        ImageButton userButton = findViewById(R.id.user_button);
        userButton.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(this, userButton);
            popupMenu.getMenuInflater().inflate(R.menu.user_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.menu_profile) {// Ação para o perfil
                    return true;
                } else if (itemId == R.id.menu_settings) {// Ação para as configurações
                    return true;
                } else if (itemId == R.id.menu_logout) {// Ação para sair
                    finishAffinity();
                    return true;
                }
                return false;
            });
            popupMenu.show();
        });
    }
}