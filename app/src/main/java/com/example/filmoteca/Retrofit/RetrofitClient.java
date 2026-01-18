package com.example.filmoteca.Retrofit;

import com.example.filmoteca.Api.MovieApi;
import com.example.filmoteca.Api.SeriesApi;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String BASE_URL = "https://api.themoviedb.org/3/";
    private static final String TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI0NmZlOWQwYzIxMjBkY2I2N2NmZDQ1ZTU1NmMzNzEyZCIsIm5iZiI6MTc2Nzk3NTQ2Ni41MjMsInN1YiI6IjY5NjEyYTJhN2RiN2E1ZWI3MDE3MTIxNiIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.wBiOo7MAs49sMvF_G1g7SuVktVtsOl7yNVk9IcqhmOQ";
    private static Retrofit retrofit;

    public static Retrofit getInstance() {
        if (retrofit == null) {

            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(chain -> {
                Request newRequest = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer " + TOKEN)
                        .addHeader("accept", "application/json")
                        .build();
                return chain.proceed(newRequest);
            }).build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static SeriesApi getSeriesApi() {
        return getInstance().create(SeriesApi.class);
    }
    public static MovieApi getMovieApi(){
        return getInstance().create(MovieApi.class);
    }
}