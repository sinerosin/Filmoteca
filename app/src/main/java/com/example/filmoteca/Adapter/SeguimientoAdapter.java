package com.example.filmoteca.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.filmoteca.Model.Media;
import com.example.filmoteca.Model.Seguimiento;
import com.example.filmoteca.R;
import com.example.filmoteca.ViewModel.MediaViewModel;
import com.example.filmoteca.databinding.ViewholdePendienteBinding;

import java.util.ArrayList;
import java.util.List;

public class SeguimientoAdapter extends RecyclerView.Adapter<SeguimientoAdapter.SeguimientoViewHolder> {
    private List<Seguimiento> seguimientoList;
    private MediaViewModel viewModel;
    private final LayoutInflater inflater;

    public SeguimientoAdapter(Context context, MediaViewModel viewModel) {
        this.inflater = LayoutInflater.from(context);
        this.viewModel = viewModel;
        this.seguimientoList = new ArrayList<>();
    }

    @NonNull
    @Override
    public SeguimientoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.viewholde_pendiente, parent, false);
        return new SeguimientoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SeguimientoViewHolder holder, int position) {
        Seguimiento seguimiento = seguimientoList.get(position);

        Glide.with(holder.itemView.getContext())
                .load(seguimiento.getPosterPath()).into(holder.binding.imagen);
        holder.binding.descripcion.setText(seguimiento.getPosterPath());
        holder.binding.titulo.setText(seguimiento.getTitulo());
        holder.itemView.setOnClickListener(view -> {

            viewModel.seleccionarSeguimiento(seguimiento);

            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.detalleSeguimientoFragment);
        });
    }

    @Override
    public int getItemCount() {
        return seguimientoList != null ? seguimientoList.size() : 0;
    }

    public void establecerLista(List<Seguimiento> lista) {
        this.seguimientoList = lista;
        notifyDataSetChanged();
    }
    public void addSeguimientoList(List<Seguimiento> lista) {
        int inicio = this.seguimientoList.size();
        this.seguimientoList.addAll(lista);
        notifyItemRangeInserted(inicio, lista.size());
    }
    public static class SeguimientoViewHolder extends RecyclerView.ViewHolder {
        ViewholdePendienteBinding binding;

        public SeguimientoViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ViewholdePendienteBinding.bind(itemView);
        }
    }
}