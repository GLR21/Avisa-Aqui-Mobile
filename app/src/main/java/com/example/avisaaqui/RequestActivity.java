package com.example.avisaaqui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class RequestActivity extends AppCompatActivity {

    private Button button_enviar = null;
    private Button button_voltar = null;
    private EditText edit_text_parametro = null;
    private TextView text_view_response_body = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        button_enviar = findViewById(R.id.button_enviar);
        button_voltar = findViewById(R.id.button_voltar2);
        edit_text_parametro = findViewById(R.id.edit_text_parametro);
        text_view_response_body = findViewById(R.id.text_view_response_body);

        button_enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                execute();
            }
        });

        button_voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    public void execute() {

        RequestQueue queue = Volley.newRequestQueue(this);

        String urlConexao = "http://177.44.248.10:8080/teste/index.jsp";   // link da API ou webpage

        urlConexao += (edit_text_parametro.getText().length() > 0) ? "?nome=" + edit_text_parametro.getText() : "";

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,                                        // Método
                urlConexao,                                                // link (acima)
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {             // o que fazer com a resposta

                        text_view_response_body.setText("Response is: " + response);

                    }
                },
                new Response.ErrorListener() {                            // se der erro
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        text_view_response_body.setText("Erro" + error);

                    }
                }
        );

        queue.add(stringRequest);

    }
}