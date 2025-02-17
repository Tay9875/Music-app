package com.example.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.modele.Music;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.List;

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

        container = findViewById(R.id.searchSongs);
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
                                if (musique.toLowerCase().contains(query.toLowerCase())) {
                                    creerBoutonsMusic(musique, url);
                                }
                            }
                        }
                    }
                });
    }

    private void creerBoutonsMusic(String musique, String url) {
        Button button = new Button(this);
        button.setText(musique);
        button.setTextColor(Color.WHITE);
        button.setBackground(new ColorDrawable(Color.parseColor("#3371E4")));
        button.setGravity(Gravity.LEFT);
        button.setPadding(20, 5, 20, 5);
        button.setBackground(getCustomButtonBackground());

        // Définir les marges
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(16, 16, 16, 16); // Marges de 16dp
        button.setLayoutParams(layoutParams);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(search.this, "Button clicked: " + musique, Toast.LENGTH_SHORT).show();
                if (isPlaying && currentUrl != null && currentUrl.equals(url)) {
                    // Si la musique est en cours de lecture, la mettre en pause
                    player.pause();
                    isPlaying = false;
                } else {
                    // Sinon, lire la musique
                    currentUrl = url;
                    MediaItem mediaItem = new MediaItem.Builder().setUri(url).build();
                    player.setMediaItem(mediaItem);
                    player.prepare();
                    player.play();
                    isPlaying = true;
                }
            }
        });

        // Ajouter le bouton au conteneur
        container.addView(button);
    }

    private ShapeDrawable getCustomButtonBackground() {
        float[] radii = new float[]{16, 16, 16, 16, 16, 16, 16, 16};
        RoundRectShape shape = new RoundRectShape(radii, null, null);
        ShapeDrawable drawable = new ShapeDrawable(shape);
        drawable.getPaint().setColor(Color.parseColor("#3371E4"));
        drawable.getPaint().setStrokeWidth(2);
        return drawable;
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
