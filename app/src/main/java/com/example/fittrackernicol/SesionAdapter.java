package com.example.fittrackernicol;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

// Adapter: conecta el ArrayList de sesiones con el RecyclerView usando sesion_item.xml.
public class SesionAdapter extends RecyclerView.Adapter<SesionAdapter.SesionViewHolder> {

    ArrayList<SesionModel> listaSesiones;

    public SesionAdapter(ArrayList<SesionModel> listaSesiones) {
        this.listaSesiones = listaSesiones;
    }

    // Crea la vista de cada ítem a partir del XML personalizado
    @NonNull
    @Override
    public SesionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sesion_item, parent, false);
        return new SesionViewHolder(vista);
    }

    // Llena cada ítem con los datos de la sesión en esa posición
    @Override
    public void onBindViewHolder(@NonNull SesionViewHolder holder, int position) {
        SesionModel sesion = listaSesiones.get(position);
        holder.txtItemTipo.setText(sesion.getTipo());
        holder.txtItemIntensidad.setText("Intensidad: " + sesion.getIntensidad());
        holder.txtItemDuracion.setText("Duración: " + sesion.getMinutos() + " min");
        holder.txtItemEsfuerzo.setText("Esfuerzo: " + sesion.getEsfuerzo() + "/5");
        holder.txtItemAspectos.setText("Completado: " + sesion.getAspectos());
    }

    @Override
    public int getItemCount() {
        return listaSesiones.size();
    }

    // ViewHolder: guarda las referencias a los TextView de cada ítem
    public static class SesionViewHolder extends RecyclerView.ViewHolder {

        TextView txtItemTipo, txtItemIntensidad, txtItemDuracion, txtItemEsfuerzo, txtItemAspectos;

        public SesionViewHolder(@NonNull View itemView) {
            super(itemView);
            txtItemTipo = itemView.findViewById(R.id.txtItemTipo);
            txtItemIntensidad = itemView.findViewById(R.id.txtItemIntensidad);
            txtItemDuracion = itemView.findViewById(R.id.txtItemDuracion);
            txtItemEsfuerzo = itemView.findViewById(R.id.txtItemEsfuerzo);
            txtItemAspectos = itemView.findViewById(R.id.txtItemAspectos);
        }
    }
}
