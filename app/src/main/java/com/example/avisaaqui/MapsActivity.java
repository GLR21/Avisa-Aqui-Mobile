package com.example.avisaaqui;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ApiService apiService;
    private List<Incident> incidentList = new ArrayList<>();
    private HeatmapTileProvider mProvider;
    private TileOverlay mOverlay;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        apiService = new ApiService(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Configurar botões
        setupButtons();
    }



    private void setupButtons() {
        findViewById(R.id.btn_refresh_heatmap).setOnClickListener(v -> updateHeatmap());

        findViewById(R.id.btn_map_type).setOnClickListener(v -> toggleMapType());

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        findViewById(R.id.fab_settings).setOnClickListener(v -> showHeatmapSettings());

    }


    private void toggleMapType() {
        if (mMap != null) {
            int currentType = mMap.getMapType();
            switch (currentType) {
                case GoogleMap.MAP_TYPE_NORMAL:
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    break;
                case GoogleMap.MAP_TYPE_SATELLITE:
                    mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                    break;
                case GoogleMap.MAP_TYPE_HYBRID:
                    mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                    break;
                default:
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    break;
            }
        }
    }

    private void showHeatmapSettings() {
        // Dialog simples para configurar heatmap
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Configurações do Heatmap");

        String[] options = {"Raio Pequeno (30px)", "Raio Médio (50px)", "Raio Grande (70px)"};
        builder.setItems(options, (dialog, which) -> {
            int radius = 50; // padrão
            switch (which) {
                case 0: radius = 30; break;
                case 1: radius = 50; break;
                case 2: radius = 70; break;
            }
            configureHeatmap(radius, 0.7);
            Toast.makeText(this, "Heatmap atualizado", Toast.LENGTH_SHORT).show();
        });

        builder.show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Configure o mapa
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // Definir uma localização inicial (Brasil) se não tiver permissão
        LatLng brasil = new LatLng(-14.235, -51.9253);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(brasil, 4));


        // Carregar os incidentes
        loadIncidents();
    }

    private void loadIncidents() {
        apiService.getIncidents(new ApiService.ApiCallback<List<Incident>>() {
            @Override
            public void onSuccess(String response) {
                // Não usado neste contexto
            }

            @Override
            public void onSuccess(List<Incident> result) {
                runOnUiThread(() -> {
                    incidentList = result;
                    if (incidentList != null && !incidentList.isEmpty()) {
                        createHeatmap();
                    } else {
                        Toast.makeText(MapsActivity.this, "Nenhum incidente encontrado", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                runOnUiThread(() -> {
                    Toast.makeText(MapsActivity.this, "Erro ao carregar incidentes: " + errorMessage, Toast.LENGTH_SHORT).show();
                    Log.e("MapsActivity", "Erro: " + errorMessage);
                });
            }
        });
    }

    private void createHeatmap() {
        List<LatLng> latLngs = new ArrayList<>();

        // Converter os incidentes ativos em pontos LatLng
        for (Incident incident : incidentList) {
            if (incident.isActive()) {  // FILTRO: só inclui incidentes ativos
                try {
                    double latitude = Double.parseDouble(incident.getLatitude());
                    double longitude = Double.parseDouble(incident.getLongitude());
                    latLngs.add(new LatLng(latitude, longitude));
                } catch (NumberFormatException e) {
                    Log.e("MapsActivity", "Erro ao converter coordenadas: " + e.getMessage() +
                            " - Lat: " + incident.getLatitude() + ", Lng: " + incident.getLongitude());
                }
            }
        }

        if (!latLngs.isEmpty()) {
            // Remover heatmap anterior se existir
            if (mOverlay != null) {
                mOverlay.remove();
            }

            // Criar o provedor de heatmap
            mProvider = new HeatmapTileProvider.Builder()
                    .data(latLngs)
                    .radius(50) // Raio do heatmap em pixels
                    .opacity(0.7) // Opacidade do heatmap
                    .build();

            // Adicionar o heatmap ao mapa
            mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));

            Toast.makeText(this, "Heatmap criado com " + latLngs.size() + " pontos", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Nenhuma coordenada válida encontrada", Toast.LENGTH_SHORT).show();
        }
    }


    // Método para abrir o mapa em coordenadas específicas (pode ser chamado de outras activities)
    public void openMapAtLocation(double latitude, double longitude, String title) {
        LatLng location = new LatLng(latitude, longitude);
        if (mMap != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15f));
            mMap.addMarker(new MarkerOptions().position(location).title(title));
        }
    }

    // Método público para ser chamado por outras activities
    public static void openWithLocation(android.content.Context context, double latitude, double longitude) {
        android.content.Intent intent = new android.content.Intent(context, MapsActivity.class);
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        context.startActivity(intent);
    }

    // Verificar se há coordenadas passadas via Intent
    private void checkIntentExtras() {
        android.content.Intent intent = getIntent();
        if (intent.hasExtra("latitude") && intent.hasExtra("longitude")) {
            double latitude = intent.getDoubleExtra("latitude", 0);
            double longitude = intent.getDoubleExtra("longitude", 0);

            if (latitude != 0 && longitude != 0) {
                LatLng location = new LatLng(latitude, longitude);
                if (mMap != null) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15f));
                    mMap.addMarker(new MarkerOptions()
                            .position(location)
                            .title("Localização Específica"));
                }
            }
        }
    }

    // Método para atualizar o heatmap (caso queira implementar filtros)
    public void updateHeatmap() {
        loadIncidents();
    }

    // Método para alterar configurações do heatmap
    public void configureHeatmap(int radius, double opacity) {
        if (mProvider != null && mOverlay != null) {
            mOverlay.remove();

            List<LatLng> latLngs = new ArrayList<>();
            for (Incident incident : incidentList) {
                try {
                    double latitude = Double.parseDouble(incident.getLatitude());
                    double longitude = Double.parseDouble(incident.getLongitude());
                    latLngs.add(new LatLng(latitude, longitude));
                } catch (NumberFormatException e) {
                    Log.e("MapsActivity", "Erro ao converter coordenadas: " + e.getMessage());
                }
            }

            mProvider = new HeatmapTileProvider.Builder()
                    .data(latLngs)
                    .radius(radius)
                    .opacity(opacity)
                    .build();

            mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Verificar extras do Intent quando a activity é retomada
        checkIntentExtras();
    }
}