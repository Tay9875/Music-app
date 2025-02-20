package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;

public class acceuil extends AppCompatActivity {

    private LinearLayout popContainer;
    private LinearLayout rnbContainer;
    private LinearLayout hiphopContainer;
    private LinearLayout electroContainer;

    private FirebaseFirestore db;
    private ExoPlayer player;
    private String currentUrl;
    private boolean isPlaying = false;
    private ImageButton btnPlayPause;
    private SeekBar seekBar;
    private Handler handler;
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acceuil);

        // Récupérer données utilisateur
        Intent old_intent = getIntent();
        String getName = old_intent.getStringExtra("name");
        String getId = old_intent.getStringExtra("id");
        Toast.makeText(acceuil.this, getId, Toast.LENGTH_SHORT).show();

        popContainer = findViewById(R.id.linearPop);
        rnbContainer = findViewById(R.id.linearRnb);
        hiphopContainer = findViewById(R.id.linearHiphop);
        electroContainer = findViewById(R.id.linearElectro);
        db = FirebaseFirestore.getInstance();

        // Initialiser ExoPlayer
        player = new ExoPlayer.Builder(this).build();

        // Récupérer les documents de la collection "music"
        getSongs("hiphop",hiphopContainer);
        getSongs("rnb",rnbContainer);
        getSongs("pop",popContainer);
        getSongs("electronic",electroContainer);

        // Initialiser le bouton play/pause
        btnPlayPause = findViewById(R.id.btn_play_pause);
        btnPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying) {
                    // Mettre en pause la musique
                    player.pause();
                    isPlaying = false;
                    btnPlayPause.setImageResource(R.drawable.iconplay); // Changer l'icône en play
                } else {
                    // Lire la musique
                    if (currentUrl != null) {
                        player.play();
                        isPlaying = true;
                        btnPlayPause.setImageResource(R.drawable.iconpause); // Changer l'icône en pause
                    }
                }
            }
        });

        // Initialise la SeekBar
        seekBar = findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    long seekPosition = (long) (progress / 100.0 * player.getDuration());
                    player.seekTo(seekPosition);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Mettre à jour la SeekBar en fonction de la progression de la musique
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                if (isPlaying && player != null) {
                    long currentPosition = player.getCurrentPosition();
                    long duration = player.getDuration();
                    if (duration > 0) {
                        int progress = (int) ((currentPosition / (float) duration) * 100);
                        seekBar.setProgress(progress);
                    }
                }
                handler.postDelayed(this, 1000);
            }
        };
        handler.postDelayed(runnable, 1000);

        // Permet de faire la navigation entre les pages principales
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.home);

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

    private void getSongs(String leGenre, LinearLayout chosenContainer) {
        db.collection("music")
                .whereEqualTo("genre",leGenre)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                LinearLayout contain = chosenContainer;
                                String musique = document.getString("title");
                                String url = document.getString("url");
                                String imageUrl = document.getString("imageUrl");
                                String artiste = document.getString("artiste");
                                String genre = document.getString("genre");
                                creerCarteMusique(contain, musique, url, imageUrl, artiste, genre);
                            }
                        }
                    }
                });
    }

    private void creerCarteMusique(LinearLayout layoutContainer,String musique, String url, String imageUrl, String description, String genre) {
        LayoutInflater inflater = LayoutInflater.from(this);
        CardView cardView = (CardView) inflater.inflate(R.layout.cardview_template, layoutContainer, false);

        ImageView imageView = cardView.findViewById(R.id.imageView3);
        imageView.setImageResource(R.drawable.cover); // Utiliser l'image statique

        TextView titleTextView = cardView.findViewById(R.id.textView5);
        titleTextView.setText(musique);

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(acceuil.this, "Card clicked: " + musique, Toast.LENGTH_SHORT).show();
                LinearLayout mainController = findViewById(R.id.songController);

                TextView infoView = mainController.findViewById(R.id.songTitle);
                infoView.setText(musique+" - "+description);

                LinearLayout controller = findViewById(R.id.mini_controller);

                mainController.setVisibility(View.VISIBLE);
                controller.setVisibility(View.VISIBLE);
                infoView.setVisibility(View.VISIBLE);
                btnPlayPause.setVisibility(View.VISIBLE);
                seekBar.setVisibility(View.VISIBLE);

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
                }
            }
        });

        // Ajouter la carte au conteneur
        layoutContainer.addView(cardView);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (player != null) {
            player.release();
            player = null;
        }
        handler.removeCallbacks(runnable);
    }
}
