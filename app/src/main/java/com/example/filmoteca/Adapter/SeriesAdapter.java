package com.example.filmoteca.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.filmoteca.Model.Media;
import com.example.filmoteca.Model.Serie;
import com.example.filmoteca.R;
import com.example.filmoteca.Response.SerieResponse;
import com.example.filmoteca.ViewModel.AuthViewModel;
import com.example.filmoteca.ViewModel.MediaViewModel;
import com.example.filmoteca.ViewModel.SerieViewModel;
import com.example.filmoteca.databinding.ViewholderSerieBinding;

import java.util.ArrayList;
import java.util.List;

public class SeriesAdapter extends RecyclerView.Adapter<SeriesAdapter.SerieViewHolder> {
    List<Serie> serieList;
    private SerieViewModel viewModel;
    public MediaViewModel mediaViewModel ;
    private final LayoutInflater inflater;
    private Context context;
    private String currentUser;
public SeriesAdapter(Context context,SerieViewModel viewModel,MediaViewModel mediaViewModel,String currentUser) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.viewModel = viewModel;
        this.mediaViewModel = mediaViewModel;
        this.serieList=new ArrayList<>();
        this.currentUser=currentUser;
    }
    @NonNull
    @Override
    public SerieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.viewholder_serie, parent, false);
        return new SerieViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull SerieViewHolder holder, int position) {
       Serie serie =serieList.get(position);

        SharedPreferences prefs = context.getSharedPreferences("MisAjustes"+ currentUser, Context.MODE_PRIVATE);
        boolean soloWifi = prefs.getBoolean("solo_wifi", false);
        if (!soloWifi) {

            Glide.with(context).load(R.drawable.upload).into(holder.binding.imagen);
        } else {

            Glide.with(context).load(serie.getPoster_path()).into(holder.binding.imagen);
        }

       holder.binding.titulo.setText(serie.getName());
       holder.binding.descripcion.setText(serie.getOverwiew());
       holder.itemView.setOnClickListener(view -> {

            viewModel.seleccionarSerie(serie);

           NavController navController = Navigation.findNavController(view);
           navController.navigate(R.id.detailSerieFragment);
       });
       holder.binding.btnAniadir.setOnClickListener(view -> {

           Media media = new Media(serie.getId(),serie.getName(),serie.getOverwiew(),serie.estreno(),serie.getPoster_path(), currentUser);
          mediaViewModel.insertarMedia(media);
       });



    }
    @Override
    public int getItemCount() {
        return serieList != null ? serieList.size() : 0;
    }

    public void establecerLista(List<Serie> series) {
        this.serieList = series;
        notifyDataSetChanged();
    }
    public void addSerieList(List<Serie> series) {
     int inicio = this.serieList.size();
     this.serieList.addAll(series);
     notifyItemRangeInserted(inicio, series.size());
    }



    public static class SerieViewHolder extends RecyclerView.ViewHolder {


        ViewholderSerieBinding binding;

        public SerieViewHolder(@NonNull View itemView) {
            super(itemView);

            binding = ViewholderSerieBinding.bind(itemView);
        }
    }


}
