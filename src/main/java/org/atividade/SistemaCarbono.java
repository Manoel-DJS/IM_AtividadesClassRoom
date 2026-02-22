package org.atividade;

import org.atividade.entities.*;
import org.atividade.exceptions.RegraNegocioException;
import org.atividade.utilities.StatusLote;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

public final class SistemaCarbono {
    private final Map<UUID, Proprietario> proprietarios = new HashMap<>();
    private final Map<UUID, LoteCreditoCarbono> lotes = new HashMap<>();
    private final Map<UUID, List<ArvoreGeradoraCredito>> arvoresPorLote = new HashMap<>();
    private final Map<UUID, List<PropriedadeLote>> propriedadesPorLote = new HashMap<>();
    private final Map<UUID, List<TransacaoCompraVenda>> transacoesPorLote = new HashMap<>();

    public void cadastrarProprietario(Proprietario p) {
        Objects.requireNonNull(p);

        boolean docDuplicado = proprietarios.values().stream()
                .anyMatch(x -> x.getDocumento().equalsIgnoreCase(p.getDocumento()));

        if (docDuplicado) {
            throw new RegraNegocioException("Já existe proprietário com este documento: " + p.getDocumento());
        }

        proprietarios.put(p.getId(), p);
    }

    public LoteCreditoCarbono criarLote(String codigoLote) {
        boolean codigoDuplicado = lotes.values().stream()
                .anyMatch(l -> l.getCodigoLote().equalsIgnoreCase(codigoLote));

        if (codigoDuplicado) {
            throw new RegraNegocioException("Já existe lote com código: " + codigoLote);
        }

        LoteCreditoCarbono lote = new LoteCreditoCarbono(codigoLote);
        lotes.put(lote.getId(), lote);

        arvoresPorLote.put(lote.getId(), new ArrayList<>());
        propriedadesPorLote.put(lote.getId(), new ArrayList<>());
        transacoesPorLote.put(lote.getId(), new ArrayList<>());

        return lote;
    }

    public void registrarArvore(UUID idLote, ArvoreGeradoraCredito arvore) {
        LoteCreditoCarbono lote = getLoteOrThrow(idLote);
        Objects.requireNonNull(arvore);

        if (lote.getStatus() != StatusLote.DISPONIVEL) {
            throw new RegraNegocioException("Não é possível registrar árvore em lote com status " + lote.getStatus());
        }

        arvoresPorLote.get(idLote).add(arvore);
    }

    public void definirPrimeiroProprietario(UUID idLote, Proprietario proprietario) {
        getLoteOrThrow(idLote);
        getProprietarioOrThrow(proprietario.getId());

        List<PropriedadeLote> props = propriedadesPorLote.get(idLote);
        if (!props.isEmpty()) {
            throw new RegraNegocioException("Lote já possui histórico de proprietários.");
        }

        props.add(new PropriedadeLote(idLote, proprietario.getId(), 1, LocalDateTime.now()));
    }

    public void venderLote(UUID idLote, Proprietario vendedor, Proprietario comprador, BigDecimal valor) {
        LoteCreditoCarbono lote = getLoteOrThrow(idLote);
        getProprietarioOrThrow(vendedor.getId());
        getProprietarioOrThrow(comprador.getId());

        if (lote.getStatus() != StatusLote.DISPONIVEL) {
            throw new RegraNegocioException("Lote não está disponível para venda (status=" + lote.getStatus() + ").");
        }

        PropriedadeLote atual = getPropriedadeAtual(idLote);
        if (atual == null) {
            throw new RegraNegocioException("Lote ainda não tem proprietário inicial definido.");
        }

        if (!atual.getIdProprietario().equals(vendedor.getId())) {
            throw new RegraNegocioException("O vendedor não é o proprietário atual do lote.");
        }

        // Regra: máximo 3 proprietários no histórico do lote
        int ordemAtual = atual.getOrdemProprietario();
        if (ordemAtual >= 3) {
            throw new RegraNegocioException("Este lote já atingiu o limite de 3 proprietários no histórico.");
        }

        LocalDateTime agora = LocalDateTime.now();

        // registra transação
        transacoesPorLote.get(idLote).add(new TransacaoCompraVenda(
                idLote, vendedor.getId(), comprador.getId(), valor, agora
        ));

        // encerra propriedade atual
        atual.encerrar(agora);

        // cria nova propriedade (próxima ordem)
        propriedadesPorLote.get(idLote).add(new PropriedadeLote(
                idLote, comprador.getId(), ordemAtual + 1, agora
        ));
    }

    public void imprimirRelatorioLote(UUID idLote) {
        LoteCreditoCarbono lote = getLoteOrThrow(idLote);

        System.out.println("Lote: " + lote.getCodigoLote());
        System.out.println("Créditos: " + lote.getTotalCredito());
        System.out.println("Status: " + lote.getStatus());

        System.out.println("\n--- Árvores (rastreabilidade) ---");
        List<ArvoreGeradoraCredito> arvores = arvoresPorLote.get(idLote);
        if (arvores.isEmpty()) {
            System.out.println("(nenhuma árvore registrada)");
        } else {
            for (ArvoreGeradoraCredito a : arvores) {
                System.out.println(" - " + a);
            }
        }

        System.out.println("\n--- Histórico de proprietários (máx 3) ---");
        List<PropriedadeLote> props = propriedadesPorLote.get(idLote);
        if (props.isEmpty()) {
            System.out.println("(sem proprietário)");
        } else {
            props.sort(Comparator.comparingInt(PropriedadeLote::getOrdemProprietario));
            for (PropriedadeLote p : props) {
                Proprietario dono = proprietarios.get(p.getIdProprietario());
                String fim = (p.getDataFim() == null) ? "ATUAL" : p.getDataFim().toString();
                System.out.println(" #" + p.getOrdemProprietario()
                        + " -> " + dono
                        + " | Início: " + p.getDataInicio()
                        + " | Fim: " + fim);
            }
        }

        System.out.println("\n--- Transações (histórico de compra/venda) ---");
        List<TransacaoCompraVenda> trans = transacoesPorLote.get(idLote);
        if (trans.isEmpty()) {
            System.out.println("(nenhuma transação)");
        } else {
            for (TransacaoCompraVenda t : trans) {
                Proprietario vend = proprietarios.get(t.getIdVendedor());
                Proprietario comp = proprietarios.get(t.getIdComprador());
                System.out.println(" - " + t.getDataTransacao()
                        + " | Vendedor: " + vend.getNome()
                        + " -> Comprador: " + comp.getNome()
                        + " | Valor: R$ " + t.getValor());
            }
        }
    }

    private LoteCreditoCarbono getLoteOrThrow(UUID id) {
        LoteCreditoCarbono lote = lotes.get(id);
        if (lote == null) throw new RegraNegocioException("Lote não encontrado: " + id);
        return lote;
    }

    private Proprietario getProprietarioOrThrow(UUID id) {
        Proprietario p = proprietarios.get(id);
        if (p == null) throw new RegraNegocioException("Proprietário não encontrado: " + id);
        return p;
    }

    private PropriedadeLote getPropriedadeAtual(UUID idLote) {
        return propriedadesPorLote.get(idLote).stream()
                .filter(PropriedadeLote::isAtual)
                .findFirst()
                .orElse(null);
    }

    public List<Proprietario> listarProprietarios() {
        return proprietarios.values().stream()
                .sorted(Comparator.comparing(Proprietario::getNome))
                .toList();
    }

    public List<LoteCreditoCarbono> listarLotes() {
        return lotes.values().stream()
                .sorted(Comparator.comparing(LoteCreditoCarbono::getCodigoLote))
                .toList();
    }
}