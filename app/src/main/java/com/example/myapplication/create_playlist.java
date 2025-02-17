package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.modele.Play_list;
import com.example.myapplication.modele.Users;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class create_playlist extends AppCompatActivity {
    //private EditText playlistInput;
    //private Button createPlaylist;
    //private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_playlist);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.library), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        /*
        Intent intent = getIntent();
        String getName = intent.getStringExtra("name");
        String getId = intent.getStringExtra("id");

        playlistInput = findViewById(R.id.playlistInput);
        createPlaylist = findViewById(R.id.createPlaylist);


        // Initialisation de Firestore
        db = FirebaseFirestore.getInstance();

        createPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String playlistName = playlistInput.getText().toString().trim();
                Play_list pl = new Play_list(playlistName,getId);

                if(playlistInput != null){
                    // Vérification des informations dans Firestore
                    db.collection("playlist")
                            .whereEqualTo("nom", playlistName)  // Filtrer par le nom de la playlist
                            .whereEqualTo("user", getId)              // Filtrer par l'ID utilisateur
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    QuerySnapshot querySnapshot = task.getResult();
                                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                        Toast.makeText(create_playlist.this, "Vous avez déjà une playlist de ce nom", Toast.LENGTH_SHORT).show();
                                    } else {//Si existe pas alors on crée
                                        // Enregistrement des données dans Firestore
                                        db.collection("playlist").document(playlistName)
                                                .set(pl)
                                                .addOnSuccessListener(aVoid -> {
                                                    // Redirection vers la page playlist
                                                    Intent intent = new Intent(create_playlist.this, playlist.class);
                                                    startActivity(intent);
                                                })
                                                .addOnFailureListener(e -> {
                                                    Toast.makeText(create_playlist.this, "Erreur lors de création", Toast.LENGTH_SHORT).show();
                                                });
                                    }
                                } else {
                                    // Gérer les erreurs si la requête échoue
                                    Toast.makeText(create_playlist.this, "Erreur", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
                else{
                    Toast.makeText(create_playlist.this, "Le champ vide", Toast.LENGTH_SHORT).show();
                }

            }
        });*/
    }
}
