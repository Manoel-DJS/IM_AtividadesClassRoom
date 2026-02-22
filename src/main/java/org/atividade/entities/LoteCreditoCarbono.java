package org.atividade.entities;

import org.atividade.utilities.Identificavel;
import org.atividade.utilities.StatusLote;

import java.util.UUID;

public final class LoteCreditoCarbono implements Identificavel {
    private final UUID id;
    private final String codigoLote;
    private final int totalCredito; // regra fixa 1000
    private StatusLote status;

    public LoteCreditoCarbono(String codigoLote) {
        if (codigoLote == null || codigoLote.isBlank()) {
            throw new IllegalArgumentException("Código do lote não pode ser vazio.");
        }
        this.id = UUID.randomUUID();
        this.codigoLote = codigoLote.trim();
        this.totalCredito = 1000;
        this.status = StatusLote.DISPONIVEL;
    }

    @Override
    public UUID getId() { return id; }

    public String getCodigoLote() { return codigoLote; }

    public int getTotalCredito() { return totalCredito; }

    public StatusLote getStatus() { return status; }

    public void setStatus(StatusLote status) {
        if (status == null) throw new IllegalArgumentException("Status não pode ser nulo.");
        this.status = status;
    }
}