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
import com.example.filmoteca.R;
import com.example.filmoteca.ViewModel.MediaViewModel;
import com.example.filmoteca.databinding.ViewholdePendienteBinding;

import java.util.ArrayList;
import java.util.List;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.MediaViewHolder>{
    private List<Media> mediaList;
    private MediaViewModel viewModel;
    private final LayoutInflater inflater;
    private Context context;
    private String currentUser;

    public MediaAdapter(Context context, MediaViewModel viewModel, String currentUser) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.viewModel = viewModel;
        this.currentUser = currentUser;
        this.mediaList = new ArrayList<>();
    }

    @NonNull
    @Override
    public MediaAdapter.MediaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.viewholde_pendiente, parent, false);
        return new MediaAdapter.MediaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MediaAdapter.MediaViewHolder holder, int position) {
        Media media = mediaList.get(position);
        SharedPreferences prefs = context.getSharedPreferences("Ajustes_" + currentUser, Context.MODE_PRIVATE);
        boolean soloWifi = prefs.getBoolean("solo_wifi", false);
        if (soloWifi) {
            Glide.with(context).load(R.drawable.television).into(holder.binding.imagen);
        } else {
            if (media.getPoster() != null && !media.getPoster().isEmpty()) {
                Glide.with(context).load(media.getPoster()).into(holder.binding.imagen);
            } else {
                Glide.with(context).load(R.drawable.television).into(holder.binding.imagen);
            }
        }

        holder.binding.titulo.setText(media.getTitle());
        holder.binding.descripcion.setText(media.getOverview());

        holder.itemView.setOnClickListener(view -> {
            viewModel.seleccionarMedia(media);
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.detailSerieFragment);
        });
    }

    @Override
    public int getItemCount() {
        return mediaList != null ? mediaList.size() : 0;
    }

    public void establecerLista(List<Media> nuevasMedias) {
        this.mediaList.clear();
        if (nuevasMedias != null) {
            this.mediaList.addAll(nuevasMedias);
        }
        notifyDataSetChanged();
    }

    public Media getMediaAt(int position) {
        return mediaList.get(position);
    }

    public static class MediaViewHolder extends RecyclerView.ViewHolder {
        ViewholdePendienteBinding binding;

        public MediaViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ViewholdePendienteBinding.bind(itemView);
        }
    }
}