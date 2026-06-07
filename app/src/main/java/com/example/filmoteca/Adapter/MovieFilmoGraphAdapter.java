package com.example.filmoteca.Adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.filmoteca.Model.Movie;
import com.example.filmoteca.databinding.ItemFilmographBinding;

import java.util.ArrayList;
import java.util.List;

public class MovieFilmoGraphAdapter extends RecyclerView.Adapter<MovieFilmoGraphAdapter.MovieViewHolder> {

    private List<Movie> listaObras = new ArrayList<>();
    private final OnMovieClickListener listener;
    public interface OnMovieClickListener {
        void onMovieClick(Movie movie);
    }
    public MovieFilmoGraphAdapter(OnMovieClickListener listener) {
        this.listener = listener;
    }
    public void establecerMovies(List<Movie> movie) {
        this.listaObras = movie;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFilmographBinding b = ItemFilmographBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new MovieViewHolder(b);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = listaObras.get(position);
        holder.binding.tvTituloObra.setText(movie.getTitulo());

        Glide.with(holder.itemView.getContext())
                .load(movie.getPoster_path())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_report_image)
                .into(holder.binding.ivPosterObra);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onMovieClick(movie);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaObras != null ? listaObras.size() : 0;
    }

    static class MovieViewHolder extends RecyclerView.ViewHolder {
        ItemFilmographBinding binding;

        public MovieViewHolder(ItemFilmographBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}