package com.example.filmoteca.Fragments;

import android.app.DatePickerDialog;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.filmoteca.Api.RetrofitClient;
import com.example.filmoteca.Model.Movie;
import com.example.filmoteca.Model.Seguimiento;
import com.example.filmoteca.Model.Serie;
import com.example.filmoteca.R;
import com.example.filmoteca.Response.MovieResponse;
import com.example.filmoteca.Response.SerieResponse;
import com.example.filmoteca.ViewModel.AuthViewModel;
import com.example.filmoteca.ViewModel.MediaViewModel;
import com.example.filmoteca.databinding.FragmentFormBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FormFragment extends Fragment {
    FragmentFormBinding binding;
    private MediaViewModel mediaViewModel;
    private AuthViewModel authViewModel;
    private String user=null;

    private String imagen= null;
    private String poster=null;
    private List<Movie> listaPeliculasResultados = new ArrayList<>();
    private List<Serie> listaSeriesResultados = new ArrayList<>();

    private final ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), img -> {
                if (img != null) {
                    imagen = img.toString();

                    binding.imgPrevisualizacion.setImageURI(img);
                    binding.imgPrevisualizacion.setVisibility(View.VISIBLE);
                    binding.layoutUploadPlaceholder.setVisibility(View.GONE);
                }
            });

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return (binding = FragmentFormBinding.inflate(inflater, container, false)).getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mediaViewModel = new ViewModelProvider(requireActivity()).get(MediaViewModel.class);
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        if (authViewModel.getCurrentUser() != null) {
            user = authViewModel.getCurrentUser().getUid();
        }
        binding.toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.btnPelicula) {
                    actualizarColores(true);
                } else if (checkedId == R.id.btnSerie) {
                    actualizarColores(false);
                }
                binding.autoCompleteTitulo.setText("");
                poster = null;
            }
        });
        actualizarColores(binding.btnPelicula.isChecked());
        binding.autoCompleteTitulo.setOnItemClickListener((parent, v, position, id) -> {
            if (binding.btnPelicula.isChecked()) {
                poster = listaPeliculasResultados.get(position).getPoster_path();
            } else {
                poster = listaSeriesResultados.get(position).getPoster_path();
            }
        });
        binding.dateInputLayout.getEditText().setOnClickListener(v -> mostrarDatePicker());

        binding.btnBuscarLupa.setOnClickListener(v -> {
            String texto = binding.autoCompleteTitulo.getText().toString();
            if (!texto.isEmpty()) {
                busqueda(texto);
            }
        });

        binding.cardUpload.setOnClickListener(v -> {
            pickMedia.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });




        binding.btnGuardar.setOnClickListener(v -> guardarSeguimiento());
    }
    private void actualizarColores(boolean peliculaSeleccionada) {
        int colorDorado = ContextCompat.getColor(requireContext(), R.color.dorado);
        int colorMorado = ContextCompat.getColor(requireContext(), R.color.morado);

        if (peliculaSeleccionada) {
            binding.btnPelicula.setBackgroundTintList(ColorStateList.valueOf(colorDorado));
            binding.btnSerie.setBackgroundTintList(ColorStateList.valueOf(colorMorado));
        } else {
            binding.btnPelicula.setBackgroundTintList(ColorStateList.valueOf(colorMorado));
            binding.btnSerie.setBackgroundTintList(ColorStateList.valueOf(colorDorado));
        }
    }
    private void mostrarDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(requireContext(), (d, y, m, day) -> {
            binding.dateInputLayout.getEditText().setText(day + "/" + (m + 1) + "/" + y);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        dialog.show();
    }

    private void busqueda(String query) {
        boolean esPelicula = binding.btnPelicula.isChecked();
        if (esPelicula) {
            RetrofitClient.getSeriesApi().buscarPeliculas(query, "es-ES")
                    .enqueue(new Callback<MovieResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                listaPeliculasResultados=response.body().getResults();
                                List<String> titulos = new ArrayList<>();
                                for (Movie m : response.body().getResults()){
                                    titulos.add(m.getTitulo());
                                }
                                sugerencias(titulos);
                            }
                        }
                        @Override public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {}
                    });
        } else {
            RetrofitClient.getSeriesApi().buscarSeries(query, "es-ES")
                    .enqueue(new Callback<SerieResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<SerieResponse> call, @NonNull Response<SerieResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                listaSeriesResultados=response.body().getResults();
                                List<String> nombres = new ArrayList<>();
                                for (Serie s : response.body().getResults()){
                                    nombres.add(s.getName());
                                    poster=s.getPoster_path();
                                }
                                sugerencias(nombres);
                            }
                        }
                        @Override public void onFailure(@NonNull Call<SerieResponse> call, @NonNull Throwable t) {}
                    });
        }
    }

    private void sugerencias(List<String> lista) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, lista);
        binding.autoCompleteTitulo.setAdapter(adapter);
        binding.autoCompleteTitulo.showDropDown();
    }

    private void guardarSeguimiento() {
        String titulo = binding.autoCompleteTitulo.getText().toString();

        if (titulo.isEmpty()) {
            Toast.makeText(requireContext(), "Selecciona un título real", Toast.LENGTH_SHORT).show();
            return;
        }

        Seguimiento s = new Seguimiento(
                titulo,
                binding.btnPelicula.isChecked() ? "Película" : "Serie",
                binding.dateInputLayout.getEditText().getText().toString(),
                binding.ratingBar.getRating(),
                imagen,
                poster,
                user
        );

        mediaViewModel.insertarSeguimiento(s);
        Navigation.findNavController(requireView()).popBackStack();
    }
}