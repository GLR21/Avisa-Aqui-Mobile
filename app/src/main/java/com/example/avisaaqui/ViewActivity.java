package com.example.avisaaqui;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

public class ViewActivity extends AppCompatActivity {

    private Button button_voltar = null;
    private WebView webview_tabela = null;
    private SQLiteDatabase db = null;
    private Cursor cursor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        button_voltar = findViewById(R.id.button_voltar);
        webview_tabela = findViewById(R.id.webview_tabela);

        db = new DatabaseManager(this, "BancoDados", null, 1).getWritableDatabase();

        button_voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Execute a query
        Cursor cursor = db.rawQuery("SELECT * FROM agenda ORDER BY id DESC", null);

        // Inicialize a StringBuilder para montar o HTML
        StringBuilder htmlTable = new StringBuilder();
        htmlTable.append("<table border='1' style='width: 100%'>");

        // Adicione o cabeçalho da tabela (opcional)
        htmlTable.append("<tr>");
        for (int i = 0; i < cursor.getColumnCount(); i++) {
            htmlTable.append("<th>").append(cursor.getColumnName(i)).append("</th>");
        }
        htmlTable.append("</tr>");

        // Percorra os resultados do Cursor
        if (cursor.moveToFirst()) {
            do {
                htmlTable.append("<tr>");
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    htmlTable.append("<td>").append(cursor.getString(i)).append("</td>");
                }
                htmlTable.append("</tr>");
            } while (cursor.moveToNext());
        }

        // Feche a tabela
        htmlTable.append("</table>");

        // Feche o cursor
        cursor.close();

        webview_tabela.loadDataWithBaseURL(null, htmlTable.toString(), "text/html", "UTF-8", null);
    }
}