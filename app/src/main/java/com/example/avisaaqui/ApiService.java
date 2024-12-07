package com.example.avisaaqui;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ApiService {

    private static final String BASE_URL = "http://177.44.248.19:8080/api/v1";
    private static String BEARER_TOKEN;
    private final OkHttpClient client;
    private Context context;

    public ApiService(Context context) {
        this.client = new OkHttpClient();
        this.context = context;
        Properties properties = new Properties();
        try (InputStream input = context.getAssets().open("config.properties")) {
            properties.load(input);
            BEARER_TOKEN = properties.getProperty("BEARER_TOKEN");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    // Método para login de usuário
    public void loginUser(String document, String password, Context context, ApiCallback callback) {
        JSONObject loginData = new JSONObject();
        try {
            loginData.put("document", document);
            loginData.put("password", password);
        } catch (Exception e) {
            e.printStackTrace();
            callback.onFailure("Erro ao criar dados de login");
            return;
        }

        String url = BASE_URL + "/login";
        RequestBody body = RequestBody.create(loginData.toString(), MediaType.parse("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + BEARER_TOKEN)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("AvisaAqui", e.toString());
                callback.onFailure("Erro de conexão");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {

                    callback.onSuccess("Login realizado com sucesso");

                    // Converte o body para String
                    String responseBody = response.body().string();

                    try {
                        // Converte a String para JSONObject
                        JSONObject jsonObject = new JSONObject(responseBody);

                        // Acessa o objeto "user" e obtém o valor de "id"
                        String userId = jsonObject.getJSONObject("data").getString("id");

                        // Exibe ou usa o valor como necessário
                        Log.d("User ID", userId);
                        SessionManager.saveUserId(context, userId);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.d("AvisaAqui", "Response code: " + response.code());
                    Log.d("AvisaAqui", "Headers: " + response.headers());
                    Log.d("AvisaAqui", "Body: " + response.body().string());
                    callback.onFailure("Falha no login");
                }
            }
        });
    }

    // Método para inserção de dados
    public void insertData(String userId, String vendorId, String productId, String latitude, String longitude, String value, ApiCallback callback) {
        String url = BASE_URL + "/incidents";
        JSONObject data = new JSONObject();
        try {
            data.put("ref_user", userId);
            data.put("ref_vendor_id", vendorId);
            data.put("ref_category", productId);
            data.put("latitude", latitude);
            data.put("longitude", longitude);
            data.put("value", value);
        } catch (Exception e) {
            e.printStackTrace();
            callback.onFailure("Erro ao criar dados de inserção");
            return;
        }

        RequestBody body = RequestBody.create(data.toString(), MediaType.parse("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + BEARER_TOKEN)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("AvisaAqui", e.toString());
                callback.onFailure("Erro de conexão ao inserir dados");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    callback.onSuccess("Dados inseridos com sucesso");
                } else {
                    Log.d("AvisaAqui", "Response code: " + response.code());
                    Log.d("AvisaAqui", "Request data: " + data.toString());
                    Log.d("AvisaAqui", "Body: " + response.body().string());
                    callback.onFailure("Falha ao inserir dados");
                }
            }
        });
    }

    // Método para obter productIds
    public void getProductIds(ApiCallback callback) {
        String url = BASE_URL + "/categories";

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + BEARER_TOKEN)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("AvisaAqui.getProductsIds.onFailure", e.toString());
                callback.onFailure("Erro ao obter product IDs");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        //System.out.println(   );
                        JSONArray  jsonArray  = jsonObject.getJSONArray("data");
                        callback.onSuccess(jsonArray.toString());  // Passar a resposta como String
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d("AvisaAqui.getProductsIds.onResponse.if.catch", e.toString());
                        callback.onFailure("Erro ao processar resposta de product IDs");
                    }
                } else {
                    Log.d("AvisaAqui.getProductsIds.onResponse.else", response.body().string());
                    callback.onFailure("Falha ao obter product IDs");
                }
            }
        });
    }

    public void getIncidents(ApiCallback callback) {
        String userId = SessionManager.getUserId(context);
        String url = BASE_URL + "/incidents?ref_user=" + userId;

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + BEARER_TOKEN)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("AvisaAqui.getIncidents.onFailure", e.toString());
                callback.onFailure("Erro ao obter incidents");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        JSONArray jsonArray = jsonObject.getJSONArray("data");

                        List<Incident> incidentList = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject incidentObject = jsonArray.getJSONObject(i);

                            // Criação de um objeto Incident a partir do JSON
                            Incident incident = new Incident(
                                    incidentObject.getInt("id"),
                                    incidentObject.getInt("ref_user"),
                                    incidentObject.getInt("ref_category"),
                                    incidentObject.getString("latitude"),
                                    incidentObject.getString("longitude"),
                                    incidentObject.getString("value"),
                                    incidentObject.getBoolean("active"),
                                    incidentObject.getString("dt_register")
                            );
                            incidentList.add(incident);
                        }

                        // Passa a lista para o callback
                        callback.onSuccess(incidentList);
                    } catch (Exception e) {
                        Log.d("AvisaAqui.getIncidents.onResponse.catch", e.toString());
                        callback.onFailure("Erro ao processar resposta dos incidents");
                    }
                } else {
                    Log.d("AvisaAqui.getIncidents.onResponse.else", response.body().string());
                    callback.onFailure("Falha ao obter incidents");
                }
            }
        });
    }

    // Interface de callback para respostas assíncronas
    public interface ApiCallback<T> {
        void onSuccess(String response);
        void onSuccess(T response);
        void onFailure(String error);
    }
}