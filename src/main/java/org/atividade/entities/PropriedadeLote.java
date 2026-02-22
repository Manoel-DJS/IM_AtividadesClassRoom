package org.atividade.entities;

import org.atividade.exceptions.RegraNegocioException;
import org.atividade.utilities.Identificavel;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public final class PropriedadeLote implements Identificavel {
    private final UUID id;
    private final UUID idLote;
    private final UUID idProprietario;
    private final int ordemProprietario; // 1..3
    private final LocalDateTime dataInicio;
    private LocalDateTime dataFim; // null = atual

    public PropriedadeLote(UUID idLote, UUID idProprietario, int ordemProprietario, LocalDateTime dataInicio) {
        this.id = UUID.randomUUID();
        this.idLote = Objects.requireNonNull(idLote);
        this.idProprietario = Objects.requireNonNull(idProprietario);

        if (ordemProprietario < 1 || ordemProprietario > 3) {
            throw new IllegalArgumentException("ordemProprietario deve ser entre 1 e 3.");
        }
        this.ordemProprietario = ordemProprietario;
        this.dataInicio = Objects.requireNonNull(dataInicio);
    }

    @Override
    public UUID getId() { return id; }

    public UUID getIdLote() { return idLote; }
    public UUID getIdProprietario() { return idProprietario; }
    public int getOrdemProprietario() { return ordemProprietario; }
    public LocalDateTime getDataInicio() { return dataInicio; }
    public LocalDateTime getDataFim() { return dataFim; }

    public boolean isAtual() { return dataFim == null; }

    public void encerrar(LocalDateTime dataFim) {
        if (this.dataFim != null) {
            throw new RegraNegocioException("Propriedade já encerrada.");
        }
        this.dataFim = Objects.requireNonNull(dataFim);
    }
}