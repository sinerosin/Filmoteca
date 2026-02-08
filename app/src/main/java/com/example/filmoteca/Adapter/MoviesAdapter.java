package com.example.filmoteca.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.filmoteca.Model.Media;
import com.example.filmoteca.Model.Movie;

import com.example.filmoteca.R;
import com.example.filmoteca.ViewModel.MediaViewModel;
import com.example.filmoteca.ViewModel.MovieViewModel;

import com.example.filmoteca.databinding.ViewholderSerieBinding;


import java.util.ArrayList;
import java.util.List;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {
    List<Movie> MovieList;
    private MovieViewModel viewModel;
    public MediaViewModel mediaViewModel ;
    private final LayoutInflater inflater;
    private Context context;
    public MoviesAdapter(Context context,MovieViewModel viewModel,MediaViewModel mediaViewModel) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.viewModel = viewModel;
        this.mediaViewModel = mediaViewModel;
        this.MovieList=new ArrayList<>();
    }
    @NonNull
    @Override
    public MoviesAdapter.MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.viewholder_serie, parent, false);
        return new MoviesAdapter.MovieViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull MoviesAdapter.MovieViewHolder holder, int position) {
        Movie Movie =MovieList.get(position);
        SharedPreferences prefs = context.getSharedPreferences("MisAjustes", Context.MODE_PRIVATE);
        boolean soloWifi = prefs.getBoolean("solo_wifi", false);
        if (!soloWifi) {

            Glide.with(context).load(R.drawable.upload).into(holder.binding.imagen);
        } else {

            Glide.with(context).load(Movie.getPoster_path()).into(holder.binding.imagen);
        }

        holder.binding.titulo.setText(Movie.getTitulo());
        holder.binding.descripcion.setText(Movie.getOverwiew());
        holder.itemView.setOnClickListener(view -> {

            viewModel.seleccionarMovie(Movie);

            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.detailSerieFragment);
        });
        holder.binding.btnAniadir.setOnClickListener(view -> {
            Media media = new Media(Movie.getId(),Movie.getTitulo(),Movie.getOverwiew(),Movie.getFecha(),Movie.getPoster_path());
            mediaViewModel.insertarMedia(media);
        });



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


        ViewholderSerieBinding binding;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);

            binding = ViewholderSerieBinding.bind(itemView);
        }
    }

}
