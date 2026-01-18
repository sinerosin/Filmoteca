package com.example.filmoteca.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.filmoteca.Model.Movie;

import com.example.filmoteca.R;
import com.example.filmoteca.databinding.ViewholderMovieBinding;


import java.util.ArrayList;
import java.util.List;

public class MoviesAdapter {
    List<Movie> MovieList;
    private final LayoutInflater inflater;
    public MoviesAdapter(Context context) {
        this.inflater = LayoutInflater.from(context);
        this.MovieList=new ArrayList<>();
    }
    @NonNull
    @Override
    public MoviesAdapter.MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.viewholder_movie, parent, false);
        return new MoviesAdapter.MovieViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull MoviesAdapter.MovieViewHolder holder, int position) {
        Movie Movie =MovieList.get(position);

        Glide.with(holder.itemView.getContext())
                .load(Movie.getPoster_path()).into(holder.binding.imagenMovie);

        holder.binding.tituloMovie.setText(Movie.getTitulo());
        holder.binding.descripcionMovie.setText(Movie.getOverwiew());



    }
    @Override
    public int getItemCount() {
        return MovieList != null ? MovieList.size() : 0;
    }

    public void establecerLista(List<Movie> Movies) {
        this.MovieList = Movies;
        notifyDataSetChanged();
    }
    public void addMovieList(List<Movie> Movies) {
        int inicio = this.MovieList.size();
        this.MovieList.addAll(Movies);
        notifyItemRangeInserted(inicio, Movies.size());
    }



    public static class MovieViewHolder extends RecyclerView.ViewHolder {


        ViewholderMovieBinding binding;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);

            binding = ViewholderMovieBinding.bind(itemView);
        }
    }
}
