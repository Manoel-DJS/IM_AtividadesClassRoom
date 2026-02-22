package org.atividade;

import org.atividade.entities.*;
import org.atividade.exceptions.RegraNegocioException;

import java.math.BigDecimal;

public class Main {
    public static void main(String[] args) {
        SistemaCarbono sistema = new SistemaCarbono();

        // Proprietários (PF e PJ)
        Proprietario pf = new PessoaFisica("João da Silva", "123.456.789-00");
        Proprietario pj = new PessoaJuridica("Empresa Verde S.A.", "12.345.678/0001-99");
        Proprietario pf2 = new PessoaFisica("Maria Oliveira", "987.654.321-00");
        Proprietario pf3 = new PessoaFisica("Carlos Souza", "111.222.333-44");

        sistema.cadastrarProprietario(pf);
        sistema.cadastrarProprietario(pj);
        sistema.cadastrarProprietario(pf2);
        sistema.cadastrarProprietario(pf3);

        // Lote (sempre 1000 créditos)
        LoteCreditoCarbono lote = sistema.criarLote("LOTE-OLIMPIADAS-0001");

        // Árvores (rastreabilidade com geolocalização)
        sistema.registrarArvore(lote.getId(), new ArvoreGeradoraCredito("Ipê Amarelo", -10.916200, -37.668300));
        sistema.registrarArvore(lote.getId(), new ArvoreGeradoraCredito("Aroeira", -10.916210, -37.668310));
        sistema.registrarArvore(lote.getId(), new ArvoreGeradoraCredito("Pau Brasil", -10.916220, -37.668320));

        // Primeiro proprietário (produção/entrada no sistema)
        sistema.definirPrimeiroProprietario(lote.getId(), pf);

        // Vendas (histórico). Máximo 3 proprietários no histórico.
        sistema.venderLote(lote.getId(), pf, pj, new BigDecimal("1500.00"));
        sistema.venderLote(lote.getId(), pj, pf2, new BigDecimal("1700.50"));

        // Tentativa de 3ª venda (viraria 4º dono) -> deve falhar
        try {
            sistema.venderLote(lote.getId(), pf2, pf3, new BigDecimal("1800.00"));
        } catch (RegraNegocioException ex) {
            System.out.println("\n[ERRO ESPERADO] " + ex.getMessage());
        }

        // Relatório no console
        System.out.println("\n==================== RELATÓRIO DO LOTE ====================");
        sistema.imprimirRelatorioLote(lote.getId());
    }
}