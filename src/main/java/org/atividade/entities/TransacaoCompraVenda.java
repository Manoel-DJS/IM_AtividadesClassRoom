package org.atividade.entities;

import org.atividade.exceptions.RegraNegocioException;
import org.atividade.utilities.Identificavel;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public final class TransacaoCompraVenda implements Identificavel {
    private final UUID id;
    private final UUID idLote;
    private final List<UUID> idsVendedores; // 1..3 (proprietários simultâneos)
    private final UUID idComprador;
    private final BigDecimal valor;
    private final LocalDateTime dataTransacao;

    public TransacaoCompraVenda(UUID idLote,
                                List<UUID> idsVendedores,
                                UUID idComprador,
                                BigDecimal valor,
                                LocalDateTime dataTransacao) {
        this.id = UUID.randomUUID();
        this.idLote = Objects.requireNonNull(idLote);
        this.idComprador = Objects.requireNonNull(idComprador);

        if (idsVendedores == null || idsVendedores.isEmpty()) {
            throw new IllegalArgumentException("Deve existir ao menos 1 vendedor.");
        }
        if (idsVendedores.size() > 3) {
            throw new RegraNegocioException("No máximo 3 vendedores (proprietários simultâneos).");
        }

        Set<UUID> set = new HashSet<>(idsVendedores);
        if (set.size() != idsVendedores.size()) {
            throw new RegraNegocioException("Vendedores duplicados não são permitidos.");
        }

        if (set.contains(idComprador)) {
            throw new RegraNegocioException("Comprador não pode ser um dos vendedores.");
        }
        if (valor == null || valor.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Valor inválido.");
        }

        this.idsVendedores = List.copyOf(new ArrayList<>(idsVendedores));
        this.valor = valor;
        this.dataTransacao = Objects.requireNonNull(dataTransacao);
    }

    @Override
    public UUID getId() { return id; }

    public UUID getIdLote() { return idLote; }
    public List<UUID> getIdsVendedores() { return idsVendedores; }
    public UUID getIdComprador() { return idComprador; }
    public BigDecimal getValor() { return valor; }
    public LocalDateTime getDataTransacao() { return dataTransacao; }
}