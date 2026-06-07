package com.example.filmoteca.Adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.filmoteca.Model.Serie;
import com.example.filmoteca.databinding.ItemFilmographBinding;

import java.util.ArrayList;
import java.util.List;

public class SerieFilmoGraphAdapter extends RecyclerView.Adapter<SerieFilmoGraphAdapter.SerieViewHolder> {

    private List<Serie> listaSerie = new ArrayList<>();
    private final OnSerieClickListener listener;
    public interface OnSerieClickListener {
        void onSerieClick(Serie serie);
    }
    public SerieFilmoGraphAdapter(OnSerieClickListener listener) {
        this.listener = listener;
    }

    public void establecerSerie(List<Serie> serie) {
        this.listaSerie = serie;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SerieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFilmographBinding b = ItemFilmographBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new SerieViewHolder(b);
    }

    @Override
    public void onBindViewHolder(@NonNull SerieViewHolder holder, int position) {
        Serie serie = listaSerie.get(position);
        holder.binding.tvTituloObra.setText(serie.getName());
        Glide.with(holder.itemView.getContext())
                .load(serie.getPoster_path())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_report_image)
                .into(holder.binding.ivPosterObra);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSerieClick(serie);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaSerie != null ? listaSerie.size() : 0;
    }

    static class SerieViewHolder extends RecyclerView.ViewHolder {
        ItemFilmographBinding binding;

        public SerieViewHolder(ItemFilmographBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}