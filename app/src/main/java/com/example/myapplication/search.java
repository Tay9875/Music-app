package com.example.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;

public class search extends AppCompatActivity {

    private LinearLayout container;
    private FirebaseFirestore db;
    private ExoPlayer player;
    private String currentUrl;
    private boolean isPlaying = false;
    private TextInputEditText searchEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Récupérer données utilisateur
        Intent old_intent = getIntent();
        String getName = old_intent.getStringExtra("name");
        String getId = old_intent.getStringExtra("id");
        //Toast.makeText(search.this, getId, Toast.LENGTH_SHORT).show();

        container = findViewById(R.id.songResearch);
        db = FirebaseFirestore.getInstance();
        searchEditText = findViewById(R.id.editText5);

        // Initialiser ExoPlayer
        player = new ExoPlayer.Builder(this).build();

        // Récupérer les documents de la collection "music"
        Button searchButton = findViewById(R.id.button4);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = searchEditText.getText().toString();
                if (!query.isEmpty()) {
                    searchMusic(query);
                } else {
                    Toast.makeText(search.this, "Oops!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Initialize and assign variable
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set Home selected
        bottomNavigationView.setSelectedItemId(R.id.search);

        // Perform item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.home) {
                    Intent intent = new Intent(getApplicationContext(), acceuil.class);
                    intent.putExtra("name", getName);
                    intent.putExtra("id", getId);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    return true;
                } else if (item.getItemId() == R.id.search) {
                    Intent intent = new Intent(getApplicationContext(), search.class);
                    intent.putExtra("name", getName);
                    intent.putExtra("id", getId);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    return true;
                } else if (item.getItemId() == R.id.library) {
                    Intent intent = new Intent(getApplicationContext(), lib.class);
                    intent.putExtra("name", getName);
                    intent.putExtra("id", getId);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    private void searchMusic(String query) {
        db.collection("music")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            container.removeAllViews(); // Clear previous results
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String musique = document.getString("title");
                                String url = document.getString("url");
                                String artiste = document.getString("artiste");
                                String genre = document.getString("genre");
                                if (musique.toLowerCase().contains(query.toLowerCase())) {
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
                Toast.makeText(search.this, "Card clicked: " + musique, Toast.LENGTH_SHORT).show();
                if (isPlaying && currentUrl != null && currentUrl.equals(url)) {
                    // Si la musique est en cours de lecture, la mettre en pause
                    player.pause();
                    isPlaying = false;
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
                }
            }
        });

        // Ajouter la carte au conteneur
        container.addView(cardView);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (player != null) {
            player.release();
            player = null;
        }
    }
}
