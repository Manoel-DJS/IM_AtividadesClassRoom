package org.atividade.entities;

import org.atividade.exceptions.RegraNegocioException;
import org.atividade.utilities.Identificavel;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Representa a participação (copropriedade) de um proprietário em um lote.
 *
 * Regras do domínio (aplicadas pelo SistemaCarbono):
 * - Um lote possui 1..3 proprietários simultâneos (participações ativas).
 * - A soma das participações ativas deve ser exatamente 1000 créditos.
 */
public final class ParticipacaoLote implements Identificavel {
    private final UUID id;
    private final UUID idLote;
    private final UUID idProprietario;
    private final int quantidadeCreditos;
    private final LocalDateTime dataInicio;
    private LocalDateTime dataFim; // null = atual

    public ParticipacaoLote(UUID idLote, UUID idProprietario, int quantidadeCreditos, LocalDateTime dataInicio) {
        this.id = UUID.randomUUID();
        this.idLote = Objects.requireNonNull(idLote, "idLote");
        this.idProprietario = Objects.requireNonNull(idProprietario, "idProprietario");

        if (quantidadeCreditos <= 0) {
            throw new IllegalArgumentException("quantidadeCreditos deve ser > 0.");
        }
        this.quantidadeCreditos = quantidadeCreditos;
        this.dataInicio = Objects.requireNonNull(dataInicio, "dataInicio");
    }

    @Override
    public UUID getId() {
        return id;
    }

    public UUID getIdLote() {
        return idLote;
    }

    public UUID getIdProprietario() {
        return idProprietario;
    }

    public int getQuantidadeCreditos() {
        return quantidadeCreditos;
    }

    public LocalDateTime getDataInicio() {
        return dataInicio;
    }

    public LocalDateTime getDataFim() {
        return dataFim;
    }

    public boolean isAtual() {
        return dataFim == null;
    }

    public void encerrar(LocalDateTime dataFim) {
        if (this.dataFim != null) {
            throw new RegraNegocioException("Participação já encerrada.");
        }
        this.dataFim = Objects.requireNonNull(dataFim, "dataFim");
    }
}
