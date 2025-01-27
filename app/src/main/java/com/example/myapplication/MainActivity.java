package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Configuration des insets pour l'activité principale
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Trouver le bouton de connexion par son ID
        Button buttonConnexion = findViewById(R.id.button3);

        // Définir un listener sur le bouton de connexion
        buttonConnexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Afficher un Toast
                Toast.makeText(MainActivity.this, "Redirection vers la page de connexion", Toast.LENGTH_SHORT).show();

                // Créer un Intent pour démarrer la nouvelle activité de connexion
                Intent intent = new Intent(MainActivity.this, connexion.class);
                startActivity(intent);
            }
        });

        // Trouver le bouton d'inscription par son ID
        Button buttonInscription = findViewById(R.id.button);

        // Définir un listener sur le bouton d'inscription
        buttonInscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Afficher un Toast
                Toast.makeText(MainActivity.this, "Redirection vers la page d'inscription", Toast.LENGTH_SHORT).show();

                // Créer un Intent pour démarrer la nouvelle activité d'inscription
                Intent intent = new Intent(MainActivity.this, inscription.class);
                startActivity(intent);
            }
        });
    }
}