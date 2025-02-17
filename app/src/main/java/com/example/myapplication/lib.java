package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class lib extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lib);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        /*
        User user = new User("lisa","lisa@gmail.com");
        TextView nom = (TextView) findViewById(R.id.textView3);
        nom.setText(user.nom);
         */

        Intent old_intent = getIntent();
        String getName = old_intent.getStringExtra("name");
        String getId = old_intent.getStringExtra("id");
        TextView nom = (TextView) findViewById(R.id.textView3);
        nom.setText(getName);

        //System.out.println("Nom: "+user.nom+" Email: "+user.email);
        /*
        // Trouver le bouton de connexion par son ID
        Button buttonPlaylist = findViewById(R.id.buttonP);

        // Définir un listener sur le bouton de connexion
        buttonPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Créer un Intent pour démarrer la nouvelle activité de connexion
                Intent intent = new Intent(MainActivity.this, Playlistes.class);
                startActivity(intent);
            }
        });
        */

        Button buttonPlaylist = findViewById(R.id.buttonP);

        buttonPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(lib.this, playlist.class);
                intent.putExtra("name", getName);
                intent.putExtra("id", getId);
                startActivity(intent);
            }
        });

        Button buttonLikes = findViewById(R.id.buttonL);

        buttonLikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(lib.this, like.class);
                intent.putExtra("name", getName);
                intent.putExtra("id", getId);
                startActivity(intent);
            }
        });

        // Initialize and assign variable
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);

        // Set Home selected
        bottomNavigationView.setSelectedItemId(R.id.library);

        // Perform item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(item.getItemId() == R.id.home){
                    Intent intent = new Intent(getApplicationContext(), acceuil.class);
                    intent.putExtra("name", getName);
                    intent.putExtra("id", getId);
                    startActivity(intent);
                    overridePendingTransition(0,0);
                    return true;
                } else if (item.getItemId() == R.id.search) {
                    Intent intent = new Intent(getApplicationContext(), search.class);
                    intent.putExtra("name", getName);
                    intent.putExtra("id", getId);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    return true;
                }
                else if(item.getItemId() == R.id.library){
                    Intent intent = new Intent(getApplicationContext(), lib.class);
                    intent.putExtra("name", getName);
                    intent.putExtra("id", getId);
                    startActivity(intent);
                    overridePendingTransition(0,0);
                    return true;
                }
                else{
                    return false;
                }
            }
        });
    }
}