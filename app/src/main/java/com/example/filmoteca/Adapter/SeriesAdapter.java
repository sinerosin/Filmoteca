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

import com.example.filmoteca.Model.Serie;
import com.example.filmoteca.R;
import com.example.filmoteca.ViewModel.SerieViewModel;
import com.example.filmoteca.databinding.ViewholderSerieBinding;

import java.util.List;

public class SeriesAdapter extends RecyclerView.Adapter<SeriesAdapter.SerieViewHolder> {
    private List<Serie> series;
    private SerieViewModel viewModel;
    private final LayoutInflater inflater;
    public SeriesAdapter(Context context, List<Serie> series, SerieViewModel viewModel) {
        this.series = series;
        this.viewModel = viewModel;
        this.inflater = LayoutInflater.from(context);
    }
    @NonNull
    @Override
    public SerieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.viewholder_serie, parent, false);
        return new SerieViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull SerieViewHolder holder, int position) {
        Serie serie = series.get(position);
        holder.binding.tituloSerie.setText(serie.getTitulo());

        holder.itemView.setOnClickListener(v -> {

           viewModel.seleccionarSerie(serie);


            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.detailSerieFragment);
        });

    }
    @Override
    public int getItemCount() {
        return series != null ? series.size() : 0;
    }

    public void establecerLista(List<Serie> series) {
        this.series = series;
        notifyDataSetChanged();
    }


    public static class SerieViewHolder extends RecyclerView.ViewHolder {


        ViewholderSerieBinding binding;

        public SerieViewHolder(@NonNull View itemView) {
            super(itemView);

            binding = ViewholderSerieBinding.bind(itemView);
        }
    }

}
