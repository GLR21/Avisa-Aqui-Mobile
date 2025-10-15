package com.example.avisaaqui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ExtendedFloatingActionButton button_abrir_mapa;
    private RecyclerView cards_incidents;
    private ApiService apiService;
    private List<Incident> incidentList = new ArrayList<>();
    private List<SpinnerItem> categoriaList = new ArrayList<>(); // CATEGORIAS

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        apiService = new ApiService(this);

        button_abrir_mapa = findViewById(R.id.button_abrir_mapa);
        button_abrir_mapa.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, MapsActivity.class);
            startActivity(intent);
        });

        ExtendedFloatingActionButton button_float_insercao = findViewById(R.id.fab_inserir);
        button_float_insercao.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, FormActivity.class);
            startActivity(intent);
        });

        ImageButton userButton = findViewById(R.id.user_button);
        userButton.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(this, userButton);
            popupMenu.getMenuInflater().inflate(R.menu.user_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.menu_profile) return true;
                else if (itemId == R.id.menu_settings) return true;
                else if (itemId == R.id.menu_logout) {
                    finishAffinity();
                    return true;
                }
                return false;
            });
            popupMenu.show();
        });

        cards_incidents = findViewById(R.id.recycler_view_incidentes);
        cards_incidents.setLayoutManager(new LinearLayoutManager(this));
        cards_incidents.setHasFixedSize(true);

        carregarCategorias(); // Primeiro passo
    }

    private void carregarCategorias() {
        apiService.getProductIds(new ApiService.ApiCallback<String>() {
            @Override
            public void onSuccess(String response) {
                try {
                    List<SpinnerItem> lista = new ArrayList<>();
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jso = jsonArray.getJSONObject(i);
                        lista.add(new SpinnerItem(
                                jso.getString("id"),
                                jso.getString("description"),
                                jso.getString("regex_validation"),
                                jso.getString("type")
                        ));
                    }
                    runOnUiThread(() -> {
                        categoriaList = lista;
                        listaIncidentes(); // Chama incidentes depois de ter categorias
                    });
                } catch (JSONException e) {
                    runOnUiThread(() -> {
                        Toast.makeText(MainActivity.this, "Erro ao processar categorias", Toast.LENGTH_SHORT).show();
                    });
                }
            }


            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Erro ao carregar categorias: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void listaIncidentes() {
        apiService.getIncidents(new ApiService.ApiCallback<List<Incident>>() {
            @Override
            public void onSuccess(String response) {}

            @Override
            public void onSuccess(List<Incident> result) {
                runOnUiThread(() -> {
                    incidentList = result;
                    atualizarCards();
                });
            }

            @Override
            public void onFailure(String errorMessage) {
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

            IncidentAdapter adapter = new IncidentAdapter(incidentList, categoriaList, this);
            cards_incidents.setAdapter(adapter);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarCategorias(); // Recarrega categorias e incidentes ao voltar
    }
}
