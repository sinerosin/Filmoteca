package com.example.filmoteca.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.filmoteca.Model.Serie;
import com.example.filmoteca.R;
import com.example.filmoteca.Response.SerieResponse;
import com.example.filmoteca.ViewModel.SerieViewModel;
import com.example.filmoteca.databinding.ViewholderSerieBinding;

import java.util.ArrayList;
import java.util.List;

public class SeriesAdapter extends RecyclerView.Adapter<SeriesAdapter.SerieViewHolder> {
    List<Serie> serieList;
    private final LayoutInflater inflater;
    public SeriesAdapter(Context context) {
        this.inflater = LayoutInflater.from(context);
        this.serieList=new ArrayList<>();
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

       Glide.with(holder.itemView.getContext())
               .load(serie.getPoster_path()).into(holder.binding.imagenSerie);

       holder.binding.tituloSerie.setText(serie.getTitulo());
       holder.binding.descripcionSerie.setText(serie.getOverwiew());



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
