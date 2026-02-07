package com.example.filmoteca.Fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.filmoteca.Model.Seguimiento;
import com.example.filmoteca.R;
import com.example.filmoteca.ViewModel.MediaViewModel;
import com.example.filmoteca.databinding.FragmentFormBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class FormFragment extends Fragment {
FragmentFormBinding binding;
private MediaViewModel mediaViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return (binding = FragmentFormBinding.inflate(inflater, container, false)).getRoot();
        }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mediaViewModel = new ViewModelProvider(requireActivity()).get(MediaViewModel.class);
        binding.dateInputLayout.getEditText().setOnClickListener(v -> mostrarDatePicker());


        binding.btnGuardar.setOnClickListener(v -> guardarSeguimiento());

    }
    private void mostrarDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(getContext(), (d, y, m, day) -> {
            binding.dateInputLayout.getEditText().setText(day + "/" + (m + 1) + "/" + y);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        dialog.show();
    }
    private void guardarSeguimiento() {
        Seguimiento s = new Seguimiento(
                binding.labelTitulo.getText().toString(),
                binding.btnPelicula.isChecked() ? "Película" : "Serie",
                binding.dateInputLayout.getEditText().getText().toString(),
                binding.ratingBar.getRating(),
                null,
                null,
                null
        );
        mediaViewModel.insertarSeguimiento(s);
        Navigation.findNavController(requireView()).popBackStack();
    }
}