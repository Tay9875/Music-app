package com.example.myapplication;

import com.example.myapplication.modele.Music;

import retrofit2.Call;
import retrofit2.http.GET;
import java.util.List;

public interface MusicApiService {
    @GET("popular-music") // Remplace par le bon endpoint de ton API
    Call<List<Music>> getPopularMusic();
}
