package com.example.filmoteca.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.filmoteca.R;
import com.example.filmoteca.databinding.FragmentSettingsBinding;


public class SettingsFragment extends Fragment {
    private FragmentSettingsBinding binding;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "MisAjustes";
    private static final String KEY_USER = "nombre_usuario";
    private static final String KEY_LANG = "idioma_pref";
    private static final String KEY_WIFI = "solo_wifi";
    private static final String KEY_THEME = "tema_oscuro";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view,@Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        cargarPreferencias();

        binding.btnGuardarAjustes.setOnClickListener(v -> guardarPreferencias());
        binding.btnResetear.setOnClickListener(v -> resetearPreferencias());
    }
    private void configurarSelectorIdioma() {

        String[] idiomas = {"Español (España)", "English (USA)", "Français (France)", "Deutsch (Deutschland)"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, idiomas);
        binding.spinnerIdioma.setAdapter(adapter);
    }
    private void cargarPreferencias() {

        binding.etUsername.setText(sharedPreferences.getString("nombre_usuario", ""));
        binding.spinnerIdioma.setText(sharedPreferences.getString("idioma_pref", "Español (España)"), false);
        binding.switchWifi.setChecked(sharedPreferences.getBoolean("solo_wifi", false));

        boolean esOscuro = sharedPreferences.getBoolean("tema_oscuro", false);
        if (esOscuro) {
            binding.toggleGroupTema.check(R.id.btnTemaOscuro);
        } else {
            binding.toggleGroupTema.check(R.id.btnTemaClaro);
        }
    }
    private void guardarPreferencias() {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("nombre_usuario", binding.etUsername.getText().toString());
        editor.putString("idioma_pref", binding.spinnerIdioma.getText().toString());
        editor.putBoolean("solo_wifi", binding.switchWifi.isChecked());

        boolean temaOscuro = binding.toggleGroupTema.getCheckedButtonId() == R.id.btnTemaOscuro;
        editor.putBoolean("tema_oscuro", temaOscuro);

        editor.apply();

        if (temaOscuro) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        Toast.makeText(getContext(), "Ajustes guardados correctamente", Toast.LENGTH_SHORT).show();
    }

    private void resetearPreferencias() {

        sharedPreferences.edit().clear().apply();
        cargarPreferencias();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }

}