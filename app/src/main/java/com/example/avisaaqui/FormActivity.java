package com.example.avisaaqui;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
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
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

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
    private Spinner spinner_boolean;
    private EditText edit_text_value;

    private String latitude;
    private String longitude;

    private String hint = "Informe um valor";

    private ApiService apiService;

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        apiService = new ApiService(this);

        spinner_product_id = findViewById(R.id.spinner_product_id);
        obtemProductsIds();

        edit_text_vendor_id = findViewById(R.id.edit_text_vendor_id);
        edit_text_value = findViewById(R.id.edit_text_value);

        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        mlocManager.requestLocationUpdates(LocationManager.FUSED_PROVIDER, 0, 0, this);

        spinner_boolean = findViewById(R.id.spinner_boolean);
        List<SpinnerItem> items = new ArrayList<>();
        items.add(new SpinnerItem("", "Selecione um valor"));
        items.add(new SpinnerItem("0", "Não"));
        items.add(new SpinnerItem("1", "Sim"));
        SpinnerItemAdapter adapter = new SpinnerItemAdapter(this, items);
        spinner_boolean.setAdapter(adapter);

        // Configurar o listener de seleção do Spinner
        spinner_product_id.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Obter o item selecionado
                SpinnerItem selectedItem = (SpinnerItem) parent.getItemAtPosition(position);

                edit_text_value.setKeyListener(null); // Remove qualquer KeyListener customizado
                edit_text_value.setFilters(new InputFilter[0]);
                edit_text_value.setText("");
                edit_text_value.setVisibility(View.VISIBLE);
                spinner_boolean.setSelection(0);
                spinner_boolean.setVisibility(View.GONE);

                // Atualizar o placeholder do EditText com o valor do campo desejado
                switch(selectedItem.getType()){
                    case "INT":
                        edit_text_value.setInputType(InputType.TYPE_CLASS_NUMBER);
                        hint = "Informe um valor inteiro";
                        break;
                    case "TEXT":
                        edit_text_value.setInputType(InputType.TYPE_CLASS_TEXT);
//                        edit_text_value.setKeyListener(TextKeyListener.getInstance()); // Adiciona o comportamento padrão para texto
                        hint = "Informe um texto";
                        break;
                    case "FLOAT":
                        edit_text_value.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                        hint = "Informe um valor decimal";
                        break;
                    case "LOGIC":
                        edit_text_value.setVisibility(View.GONE);
                        spinner_boolean.setVisibility(View.VISIBLE);
//                        edit_text_value.setInputType(InputType.TYPE_CLASS_NUMBER);
//                        edit_text_value.setKeyListener(DigitsKeyListener.getInstance("01"));
//                        edit_text_value.setFilters(new InputFilter[] { new InputFilter.LengthFilter(1) });
                        hint = "Informe um valor lógico (0 ou 1)";
                        break;
                    default:
                        edit_text_value.setInputType(InputType.TYPE_NULL);
                        hint = "Informe um valor";
                        break;
                }

                edit_text_value.setHint(hint);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Caso nada seja selecionado, se necessário, redefina o placeholder
                edit_text_value.setHint("Selecione um item");
            }
        });

        ExtendedFloatingActionButton button_float_salvar = findViewById(R.id.fab_salvar);
        button_float_salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inserirDados();
            }
        });

        ExtendedFloatingActionButton button_float_voltar = findViewById(R.id.fab_voltar);
        button_float_voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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

    private void inserirDados() {
        SpinnerItem spinnerItem = (SpinnerItem) spinner_product_id.getSelectedItem();
        String userId = SessionManager.getUserId(FormActivity.this);
        String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        String vendorId = androidId + "_" + userId;
        String productId = spinnerItem.getValue();

        if (spinnerItem.getValue().isEmpty()) {
            Toast.makeText(FormActivity.this, "Selecione um item", Toast.LENGTH_LONG).show();
            return;
        }

        String value;
        if (spinnerItem.getType().equals("LOGIC")) {
            value = ((SpinnerItem) spinner_boolean.getSelectedItem()).getValue();
        } else {
            value = String.valueOf(edit_text_value.getText());
        }

        String regex = spinnerItem.getRegex();

        if (!regex.isEmpty()) {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(value);
            if (!matcher.matches()) {
                Toast.makeText(FormActivity.this, hint, Toast.LENGTH_LONG).show();
                return;
            }
        }

        if (value.trim().isEmpty()) {
            Toast.makeText(FormActivity.this, hint, Toast.LENGTH_LONG).show();
            return;
        }

        if ((latitude == null || latitude.trim().isEmpty()) || (longitude == null || longitude.trim().isEmpty())) {
            Toast.makeText(FormActivity.this, "É preciso habilitar o GPS para envio de localização.", Toast.LENGTH_LONG).show();
            return;
        }

        apiService.insertData(userId, vendorId, productId, latitude, longitude, value, new ApiService.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(FormActivity.this, "Dados inseridos com sucesso", Toast.LENGTH_LONG).show();
                        limparCampos();
                    }
                });
            }

            @Override
            public void onSuccess(Object response) {

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
        spinner_boolean.setSelection(0);
        edit_text_value.setVisibility(View.VISIBLE);
        spinner_boolean.setVisibility(View.GONE);
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
                    List<SpinnerItem> items = new ArrayList<>();
                    items.add(new SpinnerItem("", "Selecione um item", "", ""));
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jso = jsonArray.getJSONObject(i);
                        items.add(new SpinnerItem(jso.getString("id"), jso.getString("description"), jso.getString("regex_validation"), jso.getString("type")));
                    }
                    SpinnerItemAdapter adapter = new SpinnerItemAdapter(FormActivity.this, items);

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
            public void onSuccess(Object response) {

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
