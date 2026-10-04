package com.example.fittrackernicol;

import java.io.Serializable;

// Clase Model: representa UNA sesión de entrenamiento registrada.
// Implementa Serializable para poder guardar la lista al rotar la pantalla (ciclo de vida).
public class SesionModel implements Serializable {

    private String tipo;
    private String intensidad;
    private int minutos;
    private int esfuerzo;
    private String aspectos;

    public SesionModel(String tipo, String intensidad, int minutos, int esfuerzo, String aspectos) {
        this.tipo = tipo;
        this.intensidad = intensidad;
        this.minutos = minutos;
        this.esfuerzo = esfuerzo;
        this.aspectos = aspectos;
    }

    public String getTipo() {
        return tipo;
    }

    public String getIntensidad() {
        return intensidad;
    }

    public int getMinutos() {
        return minutos;
    }

    public int getEsfuerzo() {
        return esfuerzo;
    }

    public String getAspectos() {
        return aspectos;
    }
}
