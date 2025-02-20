package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class lib extends AppCompatActivity {
    private String id;
    private LinearLayout container;
    private FirebaseFirestore db;
    private FirebaseFirestore dbSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lib);

        // Initialisation de FirebaseFirestore
        db = FirebaseFirestore.getInstance();
        dbSearch = FirebaseFirestore.getInstance();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent old_intent = getIntent();
        String getName = old_intent.getStringExtra("name");
        String getId = old_intent.getStringExtra("id");
        TextView nom = findViewById(R.id.textView3);
        nom.setText(getName);

        id = getId;
        Toast.makeText(lib.this, getId, Toast.LENGTH_SHORT).show();

        container = findViewById(R.id.linearliblike);

        // Récupérer les documents de la collection "like"
        getLikes();

        // Initialize and assign variable
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set Home selected
        bottomNavigationView.setSelectedItemId(R.id.library);

        // Perform item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home) {
                startActivityWithExtras(acceuil.class, getName, getId);
                return true;
            } else if (item.getItemId() == R.id.search) {
                startActivityWithExtras(search.class, getName, getId);
                return true;
            } else if (item.getItemId() == R.id.library) {
                startActivityWithExtras(lib.class, getName, getId);
                return true;
            }
            return false;
        });
    }

    private void startActivityWithExtras(Class<?> cls, String name, String id) {
        Intent intent = new Intent(getApplicationContext(), cls);
        intent.putExtra("name", name);
        intent.putExtra("id", id);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }
    /*
    private void getLikes() {
        db.collection("like")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(lib.this, "Récupération des likes réussie.", Toast.LENGTH_SHORT).show();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String user = document.getString("user");
                            List<String> chansons = (List<String>) document.get("chansons");

                            //Toast.makeText(lib.this, "User: " + user + ", ID: " + id, Toast.LENGTH_SHORT).show();

                            if (user != null && user.toLowerCase().contains(id.toLowerCase())) {
                                if (chansons != null) {
                                    for (String chanson : chansons) {
                                        //Toast.makeText(lib.this, "Chanson ID: " + chanson, Toast.LENGTH_SHORT).show();
                                        //creerBoutonsMusic();
                                        searchLikes(chanson);
                                    }
                                } else {
                                    Toast.makeText(lib.this, "Aucune chanson trouvée pour cet utilisateur.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    } else {
                        Toast.makeText(lib.this, "Erreur lors de la récupération des likes: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

     */

    private void getLikes() {
        db.collection("like")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String user = document.getString("user");
                            String chanson = document.getString("chanson");
                            if (user != null && user.toLowerCase().contains(id.toLowerCase())) {
                                searchLikes(chanson);
                            }
                        }
                    } else {
                        Toast.makeText(lib.this, "Erreur lors de la récupération des likes: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void searchLikes(String ref) {
        db.collection("music")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            container.removeAllViews(); // Clear previous results
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String currentId = document.getId();
                                // Toast.makeText(lib.this, "ID: " + currentId, Toast.LENGTH_SHORT).show();
                                String musique = document.getString("title");
                                String url = document.getString("url");
                                String artiste = document.getString("artiste");
                                String genre = document.getString("genre");
                                if (currentId.equals(ref)) {
                                    creerCarteMusique(musique, url, artiste, genre);
                                }
                            }
                        }
                    }
                });
    }

    private void creerCarteMusique(String musique, String url, String artiste, String genre) {
        LayoutInflater inflater = LayoutInflater.from(this);
        CardView cardView = (CardView) inflater.inflate(R.layout.cardview_template_2, container, false);

        ImageView imageView = cardView.findViewById(R.id.imageView3);
        imageView.setImageResource(R.drawable.cover); // Utiliser l'image statique

        TextView titleTextView = cardView.findViewById(R.id.textView5);
        titleTextView.setText(musique);

        TextView artisteAndGenreTextView = cardView.findViewById(R.id.textView2);
        artisteAndGenreTextView.setText(artiste+" - "+genre);

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(lib.this, "Card clicked: " + musique, Toast.LENGTH_SHORT).show();
                /*
                if (isPlaying && currentUrl != null && currentUrl.equals(url)) {
                    // Si la musique est en cours de lecture, la mettre en pause
                    player.pause();
                    isPlaying = false;
                    btnPlayPause.setImageResource(R.drawable.iconplay);
                } else {
                    // Sinon, lire la musique
                    currentUrl = url;
                    MediaItem mediaItem;
                    if (url.startsWith("http")) {
                        // Si l'URL est une URL externe
                        mediaItem = new MediaItem.Builder().setUri(Uri.parse(url)).build();
                    } else {
                        // Si l'URL est une ressource locale
                        int resId = getResources().getIdentifier(url, "raw", getPackageName());
                        mediaItem = new MediaItem.Builder().setUri(Uri.parse("android.resource://" + getPackageName() + "/" + resId)).build();
                    }
                    player.setMediaItem(mediaItem);
                    player.prepare();
                    player.play();
                    isPlaying = true;
                    btnPlayPause.setImageResource(R.drawable.iconpause);
                }*/
            }
        });

        // Ajouter la carte au conteneur
        container.addView(cardView);
    }
    private void creerBoutonsMusic(String like) {
        Button button = new Button(this);
        button.setText(like);
        button.setOnClickListener(v -> Toast.makeText(lib.this, "Button clicked: " + like, Toast.LENGTH_SHORT).show());
        container.addView(button);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Nettoyage des ressources si nécessaire
    }
}
