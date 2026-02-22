package org.atividade.entities;

import org.atividade.utilities.Identificavel;

import java.util.UUID;

public final class ArvoreGeradoraCredito implements Identificavel {
    private final UUID id;
    private final String especie;
    private final double latitude;
    private final double longitude;

    public ArvoreGeradoraCredito(String especie, double latitude, double longitude) {
        if (especie == null || especie.isBlank()) {
            throw new IllegalArgumentException("Espécie não pode ser vazia.");
        }
        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("Latitude inválida: " + latitude);
        }
        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("Longitude inválida: " + longitude);
        }
        this.id = UUID.randomUUID();
        this.especie = especie.trim();
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public UUID getId() { return id; }

    public String getEspecie() { return especie; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }

    @Override
    public String toString() {
        return "Árvore{" + especie + " @ (" + latitude + ", " + longitude + ")}";
    }
}