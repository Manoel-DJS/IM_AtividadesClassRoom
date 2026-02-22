package org.atividade.entities;

import org.atividade.utilities.Identificavel;

import java.util.Objects;
import java.util.UUID;

public abstract class Proprietario implements Identificavel {
    private final UUID id;
    private final String nome;

    protected Proprietario(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome do proprietário não pode ser vazio.");
        }
        this.id = UUID.randomUUID();
        this.nome = nome.trim();
    }

    @Override
    public UUID getId() { return id; }

    public String getNome() { return nome; }

    public abstract String getDocumento();
    public abstract String getTipo(); // PF ou PJ

    @Override
    public String toString() {
        return getTipo() + " | " + nome + " | Doc: " + getDocumento();
    }
}