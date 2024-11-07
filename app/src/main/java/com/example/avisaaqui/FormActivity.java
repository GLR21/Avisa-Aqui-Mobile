package com.example.avisaaqui;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.avisaaqui.SpinnerItem;
import com.example.avisaaqui.SpinnerItemAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FormActivity extends AppCompatActivity implements LocationListener {

    private Button button_inserir;
    private Button button_voltar;
    private EditText edit_text_vendor_id;
    private Spinner spinner_product_id;
    private EditText edit_text_value;

    private String latitude;
    private String longitude;

    private ApiService apiService;

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        apiService = new ApiService();

        spinner_product_id = findViewById(R.id.spinner_product_id);
        obtemProductsIds();

        button_inserir = findViewById(R.id.button_inserir);
        button_voltar = findViewById(R.id.button_voltar);
        edit_text_vendor_id = findViewById(R.id.edit_text_vendor_id);
        edit_text_value = findViewById(R.id.edit_text_value);

        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        mlocManager.requestLocationUpdates(LocationManager.FUSED_PROVIDER, 0, 0, this);

        button_inserir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inserirDados();
            }
        });

        button_voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void inserirDados() {
        SpinnerItem spinnerItem = (SpinnerItem) spinner_product_id.getSelectedItem();
        String userId = SessionManager.getUserId(FormActivity.this);
        String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        String vendorId = androidId + "_" + userId;
        String productId = spinnerItem.getValue();

        String value = String.valueOf(edit_text_value.getText());
        String regex = spinnerItem.getRegex();

        if (!regex.isEmpty()) {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(value);
            if (!matcher.matches()) {
                Toast.makeText(FormActivity.this, "Verifique o seu campo 'Value'", Toast.LENGTH_LONG).show();
                return;
            }
        }

        apiService.insertData(userId, vendorId, productId, latitude, longitude, value, new ApiService.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                Toast.makeText(FormActivity.this, "Dados inseridos com sucesso", Toast.LENGTH_LONG).show();
                limparCampos();
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(FormActivity.this, "Erro ao inserir dados: " + error, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private void limparCampos() {
        edit_text_vendor_id.setText("");
        edit_text_value.setText("");
        spinner_product_id.setSelection(0);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        this.latitude = Double.toString(location.getLatitude());
        this.longitude = Double.toString(location.getLongitude());
    }

    private void obtemProductsIds() {
        apiService.getProductIds(new ApiService.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    System.out.println( response );
                    List<SpinnerItem> items = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jso = jsonArray.getJSONObject(i);
                        items.add(new SpinnerItem(jso.getString("id"), jso.getString("description"), jso.getString("regex_validation")));
                    }
                    SpinnerItemAdapter adapter = new SpinnerItemAdapter(FormActivity.this, items);
                    System.out.println(items.toString());
                    // No Android, apenas a thread principal pode manipular elementos da interface do usuário. Para resolver isso, você pode utilizar o método runOnUiThread para garantir que as atualizações na interface sejam executadas na thread principal.
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            spinner_product_id.setAdapter(adapter);
                        }
                    });
                } catch (JSONException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(FormActivity.this, "Erro ao processar product IDs", Toast.LENGTH_LONG).show();
                        }
                    });

                }
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Erro ao obter product IDs: " + error, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}