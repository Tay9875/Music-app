package com.example.myapplication;

import com.example.myapplication.modele.Music;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import java.util.List;

public interface SpotifyApiService {
    @GET("v1/me/top/tracks")
    Call<List<Music>> getTopTracks(@Header("Authorization") String authorization);
}
