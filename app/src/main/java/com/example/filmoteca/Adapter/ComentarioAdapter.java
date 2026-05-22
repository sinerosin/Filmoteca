package com.example.filmoteca.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.filmoteca.Model.Comentario;
import com.example.filmoteca.R;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ComentarioAdapter extends RecyclerView.Adapter<ComentarioAdapter.ComentarioViewHolder> {
    private List<Comentario> listaComentarios = new ArrayList<>();

    @NonNull
    @Override
    public ComentarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comentario, parent, false);
        return new ComentarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ComentarioViewHolder holder, int position) {
        Comentario comentario = listaComentarios.get(position);
        holder.tvUser.setText(comentario.getUserName());
        holder.tvMensaje.setText(comentario.getMensaje());

        if (comentario.getTimestamp() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            String fechaFormateada = sdf.format(comentario.getTimestamp().toDate());
            holder.tvFecha.setText(fechaFormateada);
        }
    }

    @Override
    public int getItemCount() { return listaComentarios.size(); }

    public void establecerComentarios(List<Comentario> comentarios) {
        this.listaComentarios.clear();
        if (comentarios != null) {
            this.listaComentarios.addAll(comentarios);
        }
        notifyDataSetChanged();
    }

    static class ComentarioViewHolder extends RecyclerView.ViewHolder {
        TextView tvUser, tvMensaje, tvFecha;

        public ComentarioViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUser = itemView.findViewById(R.id.tvComentarioUser);
            tvMensaje = itemView.findViewById(R.id.tvComentarioMensaje);
            tvFecha = itemView.findViewById(R.id.tvComentarioFecha);
        }
    }
}