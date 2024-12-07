package com.example.avisaaqui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button button_abrir_insercao = null;
    private Button button_abrir_consulta = null;
    private Button button_abrir_requisicao = null;
    private RecyclerView cards_incidents = null;
    private ApiService apiService;
    private List<Incident> incidentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        apiService = new ApiService(this);

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

        // Referências para os elementos da interface
        cards_incidents = findViewById(R.id.recycler_view_incidentes);
        TextView placeholder = findViewById(R.id.placeholder_no_data);

        // Lista de incidentes simulada
        listaIncidentes();

        // Configura o RecyclerView
        cards_incidents.setLayoutManager(new LinearLayoutManager(this));
        cards_incidents.setHasFixedSize(true);
    }

    private void listaIncidentes() {
        apiService.getIncidents(new ApiService.ApiCallback<List<Incident>>() {
            @Override
            public void onSuccess(String response) {
                System.out.println("teste");
            }

            @Override
            public void onSuccess(List<Incident> result) {
                // Atualiza o RecyclerView com os dados recebidos
                System.out.println("teste2");
                System.out.println(result);
                runOnUiThread(() -> {
                    incidentList = result;
                    atualizarCards();
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                // Exibe uma mensagem de erro
                System.out.println("teste3");

                runOnUiThread(() -> {
                    incidentList = new ArrayList<>();
                    atualizarCards();
                    Toast.makeText(MainActivity.this, "Erro: " + errorMessage, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void atualizarCards() {
        TextView placeholder = findViewById(R.id.placeholder_no_data);
        if (incidentList == null || incidentList.isEmpty()) {
            cards_incidents.setVisibility(View.GONE);
            placeholder.setVisibility(View.VISIBLE);
        } else {
            cards_incidents.setVisibility(View.VISIBLE);
            placeholder.setVisibility(View.GONE);

            IncidentAdapter adapter = new IncidentAdapter(incidentList);
            cards_incidents.setAdapter(adapter);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        listaIncidentes();
    }
}