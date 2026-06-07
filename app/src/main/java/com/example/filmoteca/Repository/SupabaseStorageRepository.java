package com.example.filmoteca.Repository;

import android.net.Uri;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.filmoteca.Api.SupabaseClient;
import com.example.filmoteca.Api.SupabaseStorageApi;
import java.io.File;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SupabaseStorageRepository {

    private final SupabaseStorageApi storageApi;
    private static final String API_KEY = "sb_publishable_V0Qj9fZ-y1vajmNqSIT7QA_n6pguAFX";
    private static final String BUCKET_NAME = "imgSeg";

    public SupabaseStorageRepository() {
        this.storageApi = SupabaseClient.getClient().create(SupabaseStorageApi.class);
    }

    public LiveData<String> uploadImage(File imageFile, String userId) {
        MutableLiveData<String> liveDataUrl = new MutableLiveData<>();

        String customFileName = userId + "/" + System.currentTimeMillis() + "_" + imageFile.getName();

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imageFile);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", imageFile.getName(), requestFile);

        Call<Void> call = storageApi.uploadImage("Bearer " + API_KEY, API_KEY, BUCKET_NAME, customFileName, body);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    String baseUrlStr = SupabaseClient.getClient().baseUrl().toString();
                    String publicUrl = baseUrlStr + "storage/v1/object/public/" + BUCKET_NAME + "/" + customFileName;

                    Log.d("SUPABASE_OK", "¡Subida exitosa! -> " + publicUrl);
                    liveDataUrl.postValue(publicUrl);
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Sin detalles";
                        Log.e("SUPABASE_ERR", "Código: " + response.code() + " | Servidor dice: " + errorBody);
                    } catch (Exception e) {
                        Log.e("SUPABASE_ERR", "Error al leer respuesta");
                    }
                    liveDataUrl.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("SUPABASE_CRIT", "Fallo de conexión", t);
                liveDataUrl.postValue(null);
            }
        });

        return liveDataUrl;
    }

    public LiveData<Boolean> deleteImage(String fileUrl) {
        MutableLiveData<Boolean> resultLiveData = new MutableLiveData<>();
        String fileName = obtenerNombreArchivo(fileUrl);
        if (fileName == null) {
            resultLiveData.postValue(false);
            return resultLiveData;
        }

        Call<Void> call = storageApi.deleteImage("Bearer " + API_KEY, API_KEY, BUCKET_NAME, fileName);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                resultLiveData.postValue(response.isSuccessful());
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                resultLiveData.postValue(false);
            }
        });

        return resultLiveData;
    }

    private String obtenerNombreArchivo(String fileUrl) {
        try {
            String patron = "/object/public/" + BUCKET_NAME + "/";
            if (fileUrl.contains(patron)) {
                return fileUrl.substring(fileUrl.indexOf(patron) + patron.length());
            }
            Uri uri = Uri.parse(fileUrl);
            return uri.getLastPathSegment();
        } catch (Exception e) {
            return null;
        }
    }
}