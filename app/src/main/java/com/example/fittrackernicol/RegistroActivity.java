package com.example.fittrackernicol;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

// Formulario de registro: captura Spinner, RadioGroup, CheckBox, minutos (EditText), RatingBar,
// actualiza la ProgressBar (meta de 60 minutos) y agrega cada sesión al RecyclerView.
public class RegistroActivity extends AppCompatActivity {

    // Meta diaria: 60 minutos = 100%
    final int META_MINUTOS = 60;

    Spinner spinnerTipo;
    RadioGroup rgIntensidad;
    CheckBox chkCalentamiento, chkHidratacion, chkEstiramiento;
    EditText edtMinutos;
    RatingBar ratingEsfuerzo;
    ProgressBar progressMeta;
    TextView txtProgreso, txtResumenTotal, txtResumenUltima;
    Button btnRegistrar;
    RecyclerView recyclerSesiones;

    ArrayList<String> datos;
    ArrayList<SesionModel> listaSesiones;
    SesionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 1. Conectar vistas con findViewById
        spinnerTipo = findViewById(R.id.spinnerTipo);
        rgIntensidad = findViewById(R.id.rgIntensidad);
        chkCalentamiento = findViewById(R.id.chkCalentamiento);
        chkHidratacion = findViewById(R.id.chkHidratacion);
        chkEstiramiento = findViewById(R.id.chkEstiramiento);
        edtMinutos = findViewById(R.id.edtMinutos);
        ratingEsfuerzo = findViewById(R.id.ratingEsfuerzo);
        progressMeta = findViewById(R.id.progressMeta);
        txtProgreso = findViewById(R.id.txtProgreso);
        txtResumenTotal = findViewById(R.id.txtResumenTotal);
        txtResumenUltima = findViewById(R.id.txtResumenUltima);
        btnRegistrar = findViewById(R.id.btnRegistrar);
        recyclerSesiones = findViewById(R.id.recyclerSesiones);

        // 2. Spinner con ArrayList + ArrayAdapter
        datos = new ArrayList<>();
        datos.add("Fuerza");
        datos.add("Cardio");
        datos.add("Yoga");
        datos.add("Calistenia");

        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, datos);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipo.setAdapter(adapterSpinner);

        // 3. Lista de sesiones (se recupera si la pantalla se rotó: ciclo de vida)
        if (savedInstanceState != null) {
            listaSesiones = (ArrayList<SesionModel>) savedInstanceState.getSerializable("sesiones");
        }
        if (listaSesiones == null) {
            listaSesiones = new ArrayList<>();
        }

        // 4. RecyclerView con LinearLayoutManager y Adapter
        adapter = new SesionAdapter(listaSesiones);
        recyclerSesiones.setLayoutManager(new LinearLayoutManager(this));
        recyclerSesiones.setAdapter(adapter);
        recyclerSesiones.setNestedScrollingEnabled(false); // scroll lo maneja el ScrollView

        actualizarProgresoYResumen();

        // 5. Botón registrar
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarSesion();
            }
        });
    }

    private void registrarSesion() {
        // Spinner
        String tipo = spinnerTipo.getSelectedItem().toString();

        // RadioGroup
        int idSeleccionado = rgIntensidad.getCheckedRadioButtonId();
        if (idSeleccionado == -1) {
            Toast.makeText(this, "Selecciona la intensidad", Toast.LENGTH_SHORT).show();
            return;
        }
        RadioButton rbSeleccionado = findViewById(idSeleccionado);
        String intensidad = rbSeleccionado.getText().toString();

        // EditText: duración en minutos (validar vacío, 0 o inválido)
        String textoMinutos = edtMinutos.getText().toString().trim();
        int minutos;
        try {
            minutos = Integer.parseInt(textoMinutos);
        } catch (NumberFormatException e) {
            minutos = 0;
        }
        if (minutos <= 0) {
            Toast.makeText(this, "Ingresa una duración válida en minutos (mayor a 0)", Toast.LENGTH_SHORT).show();
            return;
        }

        // RatingBar
        int esfuerzo = (int) ratingEsfuerzo.getRating();
        if (esfuerzo < 1) {
            Toast.makeText(this, "Califica el esfuerzo (1 a 5 estrellas)", Toast.LENGTH_SHORT).show();
            return;
        }

        // CheckBox
        String aspectos = "";
        if (chkCalentamiento.isChecked()) {
            aspectos = aspectos + "Calentamiento ";
        }
        if (chkHidratacion.isChecked()) {
            aspectos = aspectos + "Hidratación ";
        }
        if (chkEstiramiento.isChecked()) {
            aspectos = aspectos + "Estiramiento ";
        }
        if (aspectos.isEmpty()) {
            aspectos = "Ninguno";
        }

        // Agregar al RecyclerView
        SesionModel sesion = new SesionModel(tipo, intensidad, minutos, esfuerzo, aspectos.trim());
        listaSesiones.add(sesion);
        adapter.notifyItemInserted(listaSesiones.size() - 1);

        actualizarProgresoYResumen();

        Toast.makeText(this, "Sesión registrada: " + tipo, Toast.LENGTH_SHORT).show();
    }

    // ProgressBar (minutos acumulados vs meta de 60) + TableLayout de resumen
    private void actualizarProgresoYResumen() {
        int total = listaSesiones.size();

        // Sumar los minutos de todas las sesiones registradas
        int minutosAcumulados = 0;
        for (SesionModel s : listaSesiones) {
            minutosAcumulados = minutosAcumulados + s.getMinutos();
        }

        int porcentaje = minutosAcumulados * 100 / META_MINUTOS;
        if (porcentaje > 100) {
            porcentaje = 100;
        }
        progressMeta.setProgress(porcentaje);
        txtProgreso.setText(minutosAcumulados + " de " + META_MINUTOS + " minutos (" + porcentaje + "%)");

        txtResumenTotal.setText(String.valueOf(total));
        if (total > 0) {
            SesionModel ultima = listaSesiones.get(total - 1);
            txtResumenUltima.setText(ultima.getTipo() + " - " + ultima.getIntensidad());
        } else {
            txtResumenUltima.setText("-");
        }
    }

    // Ciclo de vida: guarda la lista antes de que la Activity se destruya (ej. al rotar)
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("sesiones", listaSesiones);
    }
}
