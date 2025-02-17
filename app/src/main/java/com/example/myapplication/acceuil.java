package com.example.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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

public class acceuil extends AppCompatActivity {

    private LinearLayout container;
    private FirebaseFirestore db;
    private ExoPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acceuil);

        // Récupérer données utilisateur
        Intent old_intent = getIntent();
        String getName = old_intent.getStringExtra("name");
        String getId = old_intent.getStringExtra("id");
        Toast.makeText(acceuil.this, getId, Toast.LENGTH_SHORT).show();

        container = findViewById(R.id.linearMusiques);
        db = FirebaseFirestore.getInstance();

        // Initialiser ExoPlayer
        player = new ExoPlayer.Builder(this).build();

        // Récupérer les documents de la collection "music"
        getSongs();

        /*
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.spotify.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        SpotifyApiService service = retrofit.create(SpotifyApiService.class);
        Call<List<Music>> call = service.getTopTracks("Bearer 62ba6a7a8fd54dc1a9b37e987632936a");

        call.enqueue(new Callback<List<Music>>() {
            @Override
            public void onResponse(Call<List<Music>> call, Response<List<Music>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Music> musicList = response.body();
                    // Vous pouvez ajouter les musiques à une RecyclerView ici si nécessaire
                }
            }

            @Override
            public void onFailure(Call<List<Music>> call, Throwable t) {
                Toast.makeText(acceuil.this, "Erreur lors de la récupération des données", Toast.LENGTH_SHORT).show();
            }
        });
        */

        // Initialize and assign variable
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set Home selected
        bottomNavigationView.setSelectedItemId(R.id.home);

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

    private void getSongs() {
        db.collection("music")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String musique = document.getString("title");
                                String url = document.getString("url");
                                creerBoutonsMusic(musique, url);
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
                //Toast.makeText(acceuil.this, "Button clicked: " + musique, Toast.LENGTH_SHORT).show();
                playMusic(url);
            }
        });

        // Ajouter le bouton au conteneur
        container.addView(button);
    }

    private void playMusic(String url) {
        MediaItem mediaItem = new MediaItem.Builder().setUri(url).build();
        player.setMediaItem(mediaItem);
        player.prepare();
        player.play();
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
