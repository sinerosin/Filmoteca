package com.example.filmoteca.Fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.filmoteca.Adapter.SeguimientoAdapter;
import com.example.filmoteca.Model.Seguimiento;
import com.example.filmoteca.R;
import com.example.filmoteca.ViewModel.AuthViewModel;
import com.example.filmoteca.ViewModel.MediaViewModel;
import com.example.filmoteca.databinding.FragmentSeguimientoBinding;
import com.google.firebase.auth.FirebaseUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SeguimientoFragment extends Fragment {
    private NavController navController;
    private FragmentSeguimientoBinding binding;
    private MediaViewModel viewModel;
    private AuthViewModel authViewModel;
    private SeguimientoAdapter adapter;

    private final SimpleDateFormat formatoFecha = new SimpleDateFormat("d/M/yyyy", Locale.getDefault());
    private String filtroTextoTitulo = "";
    private int criterioOrdenacionActivo = 1;
    private String filtroFechaDesde = "";
    private String filtroFechaHasta = "";
    private float filtroPuntMinima = 0f;
    private float filtroPuntMaxima = 5f;

    private LiveData<List<Seguimiento>> queryLiveDataRealtime;
    private final Observer<List<Seguimiento>> observadorSeguimientosDB = listaFiltrada -> {
        if (listaFiltrada != null) {
            if (listaFiltrada.isEmpty()) {
                binding.recyclerViewSeguimiento.setVisibility(View.GONE);
                binding.tvNoResultados.setVisibility(View.VISIBLE);
            } else {
                binding.recyclerViewSeguimiento.setVisibility(View.VISIBLE);
                binding.tvNoResultados.setVisibility(View.GONE);
            }
            adapter.establecerLista(listaFiltrada);
        }
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSeguimientoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        viewModel = new ViewModelProvider(requireActivity()).get(MediaViewModel.class);
        navController = Navigation.findNavController(view);

        configurarRecyclerView();
        inicializarFiltros();
        observarSeguimientos();

        binding.aAdirSeguimiento.setOnClickListener(v -> navController.navigate(R.id.formFragment));
    }

    private void configurarRecyclerView() {
        adapter = new SeguimientoAdapter(requireContext(), viewModel);
        binding.recyclerViewSeguimiento.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewSeguimiento.setAdapter(adapter);

        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView rv, @NonNull RecyclerView.ViewHolder vh, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Seguimiento s = adapter.getSeguimientoAt(position);

                viewModel.eliminarSeguimiento(s);

                new AlertDialog.Builder(requireContext())
                        .setTitle("Elemento Eliminado")
                        .setMessage("Se ha eliminado correctamente: " + s.getTitulo())
                        .setPositiveButton("Aceptar", null)
                        .show();
            }
        });
        helper.attachToRecyclerView(binding.recyclerViewSeguimiento);
    }

    private void inicializarFiltros() {
        binding.btnToggleOrdenar.setOnClickListener(v -> {
            binding.layoutOrdenarPanel.setVisibility(binding.layoutOrdenarPanel.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            binding.layoutFiltrosPanel.setVisibility(View.GONE);
        });

        binding.btnToggleFiltros.setOnClickListener(v -> {
            binding.layoutFiltrosPanel.setVisibility(binding.layoutFiltrosPanel.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            binding.layoutOrdenarPanel.setVisibility(View.GONE);
        });

        binding.etBuscarTitulo.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                filtroTextoTitulo = s.toString().trim();
                consultaBaseDatos();
            }
        });

        binding.tvOptFechaReciente.setOnClickListener(v -> actualizarOrden(1, binding.tvOptFechaReciente));
        binding.tvOptFechaAntigua.setOnClickListener(v -> actualizarOrden(2, binding.tvOptFechaAntigua));
        binding.tvOptPuntMayor.setOnClickListener(v -> actualizarOrden(3, binding.tvOptPuntMayor));
        binding.tvOptPuntMenor.setOnClickListener(v -> actualizarOrden(4, binding.tvOptPuntMenor));

        binding.etFiltroDesde.setOnClickListener(v -> abrirDatePicker(binding.etFiltroDesde, true));
        binding.etFiltroHasta.setOnClickListener(v -> abrirDatePicker(binding.etFiltroHasta, false));

        String[] opcionesPunt = {"0", "1", "2", "3", "4", "5"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, opcionesPunt);
        binding.spinnerMinPunt.setAdapter(spinnerAdapter);
        binding.spinnerMaxPunt.setAdapter(spinnerAdapter);
        binding.spinnerMaxPunt.setSelection(5);

        binding.btnAplicarFiltros.setOnClickListener(v -> {
            filtroFechaDesde = binding.etFiltroDesde.getText().toString().trim();
            filtroFechaHasta = binding.etFiltroHasta.getText().toString().trim();

            String itemMin = binding.spinnerMinPunt.getSelectedItem().toString().replaceAll("[^0-9.]", "").trim();
            String itemMax = binding.spinnerMaxPunt.getSelectedItem().toString().replaceAll("[^0-9.]", "").trim();

            filtroPuntMinima = itemMin.isEmpty() ? 0f : Float.parseFloat(itemMin);
            filtroPuntMaxima = itemMax.isEmpty() ? 5f : Float.parseFloat(itemMax);

            if (validarFiltro()) {
                binding.layoutFiltrosPanel.setVisibility(View.GONE);
                binding.recyclerViewSeguimiento.post(this::consultaBaseDatos);
            }
        });
    }

    private void abrirDatePicker(TextView textView, boolean esDesde) {
        Calendar cal = Calendar.getInstance();
        DatePickerDialog picker = new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
            String fechaSeleccionada = dayOfMonth + "/" + (month + 1) + "/" + year;
            textView.setText(fechaSeleccionada);
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        picker.show();
    }

    private void actualizarOrden(int nuevoCriterio, TextView viewSeleccionada) {
        criterioOrdenacionActivo = nuevoCriterio;

        binding.tvOptFechaReciente.setTypeface(null, android.graphics.Typeface.NORMAL);
        binding.tvOptFechaAntigua.setTypeface(null, android.graphics.Typeface.NORMAL);
        binding.tvOptPuntMayor.setTypeface(null, android.graphics.Typeface.NORMAL);
        binding.tvOptPuntMenor.setTypeface(null, android.graphics.Typeface.NORMAL);

        viewSeleccionada.setTypeface(null, android.graphics.Typeface.BOLD);
        consultaBaseDatos();
        binding.layoutOrdenarPanel.setVisibility(View.GONE);
    }

    private boolean validarFiltro() {
        if (filtroPuntMinima > filtroPuntMaxima) {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Error de Validación")
                    .setMessage("La puntuación mínima no puede superar a la máxima.")
                    .setPositiveButton("Aceptar", null)
                    .show();
            return false;
        }

        if (!filtroFechaDesde.isEmpty() && !filtroFechaHasta.isEmpty()) {
            try {
                Date dateDesde = formatoFecha.parse(filtroFechaDesde);
                Date dateHasta = formatoFecha.parse(filtroFechaHasta);
                if (dateDesde != null && dateDesde.after(dateHasta)) {
                    new AlertDialog.Builder(requireContext())
                            .setTitle("Error de Validación")
                            .setMessage("La fecha 'Desde' no puede ser posterior a 'Hasta'.")
                            .setPositiveButton("Aceptar", null)
                            .show();
                    return false;
                }
            } catch (ParseException e) {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Error de Formato")
                        .setMessage("Ha ocurrido un error en el formato de las fechas.")
                        .setPositiveButton("Aceptar", null)
                        .show();
                return false;
            }
        }
        return true;
    }

    private void observarSeguimientos() {
        consultaBaseDatos();
    }

    private void consultaBaseDatos() {
        FirebaseUser user = authViewModel.getCurrentUser();
        if (user != null) {
            if (queryLiveDataRealtime != null) {
                queryLiveDataRealtime.removeObserver(observadorSeguimientosDB);
            }

            queryLiveDataRealtime = viewModel.obtenerSeguimientosFiltradosDB(
                    user.getUid(),
                    filtroTextoTitulo,
                    criterioOrdenacionActivo,
                    filtroFechaDesde,
                    filtroFechaHasta,
                    filtroPuntMinima,
                    filtroPuntMaxima
            );

            queryLiveDataRealtime.observe(getViewLifecycleOwner(), observadorSeguimientosDB);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}