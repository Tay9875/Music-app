package com.example.myapplication;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.modele.Music;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.List;

public class acceuil extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MusicAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acceuil);

        recyclerView = findViewById(R.id.recyclerView_popular);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

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
                    adapter = new MusicAdapter(musicList);
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(acceuil.this, "Aucune donnée trouvée", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Music>> call, Throwable t) {
                Toast.makeText(acceuil.this, "Erreur lors de la récupération des données", Toast.LENGTH_SHORT).show();
            }
        });
    }
}