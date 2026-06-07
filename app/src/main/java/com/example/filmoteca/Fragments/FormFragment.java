package com.example.filmoteca.Fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

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

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
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
    private String user = null;
    private Uri uriImagenSeleccionada = null;
    private String poster = null;
    private List<Movie> listaPeliculasResultados = new ArrayList<>();
    private List<Serie> listaSeriesResultados = new ArrayList<>();

    private final ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), img -> {
                if (img != null) {
                    uriImagenSeleccionada = img;
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
        String uid = authViewModel.getCurrentUser() != null ? authViewModel.getCurrentUser().getUid() : "";
        SharedPreferences prefs = requireActivity().getSharedPreferences("Ajustes_" + uid, Context.MODE_PRIVATE);
        String idiomaGuardado = prefs.getString("idioma_pref_codigo", "es-ES");

        if (idiomaGuardado == null || idiomaGuardado.trim().isEmpty()) {
            idiomaGuardado = "es-ES";
        }

        boolean esPelicula = binding.btnPelicula.isChecked();
        if (esPelicula) {
            RetrofitClient.getSeriesApi().buscarPeliculas(query, idiomaGuardado)
                    .enqueue(new Callback<MovieResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                listaPeliculasResultados = response.body().getResults();
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
            RetrofitClient.getSeriesApi().buscarSeries(query, idiomaGuardado)
                    .enqueue(new Callback<SerieResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<SerieResponse> call, @NonNull Response<SerieResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                listaSeriesResultados = response.body().getResults();
                                List<String> nombres = new ArrayList<>();
                                for (Serie s : response.body().getResults()){
                                    nombres.add(s.getName());
                                    poster = s.getPoster_path();
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
            new AlertDialog.Builder(requireContext())
                    .setTitle("Campo obligatorio")
                    .setMessage("Por favor, selecciona o introduce un título válido.")
                    .setPositiveButton("Aceptar", null)
                    .show();
            return;
        }

        binding.btnGuardar.setEnabled(false);

        if (uriImagenSeleccionada != null) {
            File archivoImagen = converetirUriAFile(uriImagenSeleccionada);

            if (archivoImagen != null && user != null) {
                mediaViewModel.subirImagenRecuerdo(archivoImagen, user).observe(getViewLifecycleOwner(), urlPublica -> {
                    if (urlPublica != null) {
                        Seguimiento s = new Seguimiento(
                                titulo,
                                binding.btnPelicula.isChecked() ? "Película" : "Serie",
                                binding.dateInputLayout.getEditText().getText().toString(),
                                binding.ratingBar.getRating(),
                                urlPublica,
                                poster,
                                user
                        );

                        mediaViewModel.insertarSeguimiento(s);

                        new AlertDialog.Builder(requireContext())
                                .setTitle("Guardado Exitoso")
                                .setMessage("¡El seguimiento y el recuerdo gráfico se han almacenado con éxito!")
                                .setPositiveButton("Aceptar", (dialog, which) -> {
                                    Navigation.findNavController(requireView()).popBackStack();
                                })
                                .setCancelable(false)
                                .show();
                    } else {
                        binding.btnGuardar.setEnabled(true);
                        new AlertDialog.Builder(requireContext())
                                .setTitle("Error de Conexión")
                                .setMessage("Fallo al subir el archivo multimedia al almacenamiento en la nube.")
                                .setPositiveButton("Aceptar", null)
                                .show();
                    }
                });
            } else {
                binding.btnGuardar.setEnabled(true);
                new AlertDialog.Builder(requireContext())
                        .setTitle("Error de Proceso")
                        .setMessage("No se ha podido preparar la estructura del archivo multimedia.")
                        .setPositiveButton("Aceptar", null)
                        .show();
            }
        } else {
            Seguimiento s = new Seguimiento(
                    titulo,
                    binding.btnPelicula.isChecked() ? "Película" : "Serie",
                    binding.dateInputLayout.getEditText().getText().toString(),
                    binding.ratingBar.getRating(),
                    null,
                    poster,
                    user
            );
            mediaViewModel.insertarSeguimiento(s);

            new AlertDialog.Builder(requireContext())
                    .setTitle("Guardado Exitoso")
                    .setMessage("¡Tu seguimiento se ha registrado de manera correcta!")
                    .setPositiveButton("Aceptar", (dialog, which) -> {
                        Navigation.findNavController(requireView()).popBackStack();
                    })
                    .setCancelable(false)
                    .show();
        }
    }

    private File converetirUriAFile(Uri uri) {
        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
            if (inputStream == null) return null;

            File tempFile = File.createTempFile("recuerdo_", ".jpg", requireContext().getCacheDir());
            FileOutputStream outputStream = new FileOutputStream(tempFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            inputStream.close();
            return tempFile;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}