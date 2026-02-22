package org.atividade.entities;

import org.atividade.exceptions.RegraNegocioException;
import org.atividade.utilities.Identificavel;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public final class TransacaoCompraVenda implements Identificavel {
    private final UUID id;
    private final UUID idLote;
    private final UUID idVendedor;
    private final UUID idComprador;
    private final BigDecimal valor;
    private final LocalDateTime dataTransacao;

    public TransacaoCompraVenda(UUID idLote, UUID idVendedor, UUID idComprador, BigDecimal valor, LocalDateTime dataTransacao) {
        this.id = UUID.randomUUID();
        this.idLote = Objects.requireNonNull(idLote);
        this.idVendedor = Objects.requireNonNull(idVendedor);
        this.idComprador = Objects.requireNonNull(idComprador);

        if (idVendedor.equals(idComprador)) {
            throw new RegraNegocioException("Vendedor e comprador não podem ser o mesmo.");
        }
        if (valor == null || valor.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Valor inválido.");
        }

        this.valor = valor;
        this.dataTransacao = Objects.requireNonNull(dataTransacao);
    }

    @Override
    public UUID getId() { return id; }

    public UUID getIdLote() { return idLote; }
    public UUID getIdVendedor() { return idVendedor; }
    public UUID getIdComprador() { return idComprador; }
    public BigDecimal getValor() { return valor; }
    public LocalDateTime getDataTransacao() { return dataTransacao; }
}