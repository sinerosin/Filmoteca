package com.example.filmoteca.Adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.filmoteca.Model.Actor;
import com.example.filmoteca.databinding.ItemActorBinding;
import java.util.ArrayList;
import java.util.List;

public class ActorAdapter extends RecyclerView.Adapter<ActorAdapter.ActorViewHolder> {

    private List<Actor> listaActores = new ArrayList<>();
    private List<String> listaFavoritosIds = new ArrayList<>();
    private final OnActorClickListener listener;
    public interface OnActorClickListener {
        void onFavToggle(Actor actor, boolean esFavorito);
        void onActorClick(Actor actor);
    }

    public ActorAdapter(OnActorClickListener listener) {
        this.listener = listener;
    }

    public void establecerActores(List<Actor> actores) {
        this.listaActores = actores;
        notifyDataSetChanged();
    }

    public void establecerFavoritos(List<String> favoritosIds) {
        this.listaFavoritosIds = favoritosIds;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ActorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemActorBinding b = ItemActorBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ActorViewHolder(b);
    }

    @Override
    public void onBindViewHolder(@NonNull ActorViewHolder holder, int position) {
        Actor actor = listaActores.get(position);
        holder.binding.tvNombreActor.setText(actor.getNombre());

        Glide.with(holder.itemView.getContext())
                .load(actor.getFotoUrl())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_report_image)
                .into(holder.binding.ivFotoActor);

        boolean fav = listaFavoritosIds.contains(actor.getId());
        if (fav) {
            holder.binding.ivFavorito.setImageResource(android.R.drawable.btn_star_big_on);
        } else {
            holder.binding.ivFavorito.setImageResource(android.R.drawable.btn_star_big_off);
        }

        holder.binding.ivFavorito.setOnClickListener(v -> {
            if (listener != null) {
                listener.onFavToggle(actor, fav);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onActorClick(actor);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaActores != null ? listaActores.size() : 0;
    }

    static class ActorViewHolder extends RecyclerView.ViewHolder {
        ItemActorBinding binding;
        public ActorViewHolder(ItemActorBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}