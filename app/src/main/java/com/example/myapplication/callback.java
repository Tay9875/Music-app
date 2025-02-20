package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class callback extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Uri uri = getIntent().getData();
        if (uri != null) {
            String code = uri.getQueryParameter("code");
            if (code != null) {
                // Utilise ce code pour obtenir un token d'accès
                Intent intent = new Intent(this, acceuil.class);
                intent.putExtra("AUTH_CODE", code);
                startActivity(intent);
            }
        }
        finish();
    }
}