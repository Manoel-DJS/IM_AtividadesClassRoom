package org.atividade;

import org.atividade.entities.*;
import org.atividade.exceptions.RegraNegocioException;
import org.atividade.utilities.StatusLote;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public final class SistemaCarbono {
    private final Map<UUID, Proprietario> proprietarios = new HashMap<>();
    private final Map<UUID, LoteCreditoCarbono> lotes = new HashMap<>();
    private final Map<UUID, List<ArvoreGeradoraCredito>> arvoresPorLote = new HashMap<>();
    // Copropriedade simultânea (até 3). Histórico via dataFim.
    private final Map<UUID, List<ParticipacaoLote>> participacoesPorLote = new HashMap<>();
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
        participacoesPorLote.put(lote.getId(), new ArrayList<>());
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

    /**
     * Define os proprietários atuais do lote (copropriedade simultânea).
     * Regras:
     * - 1 a 3 proprietários
     * - soma das quantidades = 1000
     * - não permite duplicar proprietário
     * - só pode ser feito se não houver participações atuais ainda
     */
    public void definirParticipacoesIniciais(UUID idLote, Map<UUID, Integer> proprietarioParaCreditos) {
        getLoteOrThrow(idLote);

        if (proprietarioParaCreditos == null || proprietarioParaCreditos.isEmpty()) {
            throw new RegraNegocioException("Informe 1 a 3 proprietários.");
        }
        if (proprietarioParaCreditos.size() > 3) {
            throw new RegraNegocioException("No máximo 3 proprietários simultâneos.");
        }
        if (!getParticipacoesAtuais(idLote).isEmpty()) {
            throw new RegraNegocioException("O lote já possui proprietários atuais definidos.");
        }

        int soma = 0;
        for (Map.Entry<UUID, Integer> e : proprietarioParaCreditos.entrySet()) {
            UUID idProp = e.getKey();
            Integer qtd = e.getValue();

            getProprietarioOrThrow(idProp);
            if (qtd == null || qtd <= 0) {
                throw new RegraNegocioException("Quantidade de créditos deve ser > 0.");
            }
            soma += qtd;
        }

        if (soma != 1000) {
            throw new RegraNegocioException("A soma das participações deve ser exatamente 1000. Soma atual = " + soma);
        }

        LocalDateTime agora = LocalDateTime.now();
        List<ParticipacaoLote> lista = participacoesPorLote.get(idLote);
        for (Map.Entry<UUID, Integer> e : proprietarioParaCreditos.entrySet()) {
            lista.add(new ParticipacaoLote(idLote, e.getKey(), e.getValue(), agora));
        }
    }

    /**
     * Venda do lote inteiro (1000 créditos).
     * Regras:
     * - vendedores informados devem ser exatamente os proprietários atuais (1..3)
     * - comprador não pode ser um dos vendedores
     * - após venda: encerra participações atuais e cria 1 participação nova (comprador = 1000)
     */
    public void venderLote(UUID idLote, List<UUID> idsVendedores, UUID idComprador, BigDecimal valor) {
        LoteCreditoCarbono lote = getLoteOrThrow(idLote);

        if (lote.getStatus() != StatusLote.DISPONIVEL) {
            throw new RegraNegocioException("Lote não está disponível para venda (status=" + lote.getStatus() + ").");
        }

        if (idsVendedores == null || idsVendedores.isEmpty()) {
            throw new RegraNegocioException("Informe ao menos 1 vendedor.");
        }
        if (idsVendedores.size() > 3) {
            throw new RegraNegocioException("No máximo 3 vendedores.");
        }

        getProprietarioOrThrow(idComprador);
        for (UUID v : idsVendedores) getProprietarioOrThrow(v);

        List<ParticipacaoLote> atuais = getParticipacoesAtuais(idLote);
        if (atuais.isEmpty()) {
            throw new RegraNegocioException("O lote não possui proprietários atuais definidos.");
        }
        if (atuais.size() > 3) {
            throw new RegraNegocioException("Estado inválido: mais de 3 proprietários atuais.");
        }

        int somaAtual = atuais.stream().mapToInt(ParticipacaoLote::getQuantidadeCreditos).sum();
        if (somaAtual != 1000) {
            throw new RegraNegocioException("Estado inválido: soma atual das participações != 1000 (soma=" + somaAtual + ")");
        }

        Set<UUID> setAtuais = atuais.stream().map(ParticipacaoLote::getIdProprietario).collect(Collectors.toSet());
        Set<UUID> setInformados = new HashSet<>(idsVendedores);

        if (!setAtuais.equals(setInformados)) {
            throw new RegraNegocioException("Para vender, os vendedores informados devem ser exatamente os proprietários atuais do lote.");
        }
        if (setInformados.contains(idComprador)) {
            throw new RegraNegocioException("O comprador não pode ser um dos proprietários atuais (vendedores).");
        }

        LocalDateTime agora = LocalDateTime.now();

        transacoesPorLote.get(idLote).add(new TransacaoCompraVenda(
                idLote, new ArrayList<>(setInformados), idComprador, valor, agora
        ));

        for (ParticipacaoLote p : atuais) {
            p.encerrar(agora);
        }

        participacoesPorLote.get(idLote).add(new ParticipacaoLote(idLote, idComprador, 1000, agora));
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

        System.out.println("\n--- Proprietários atuais (copropriedade) ---");
        List<ParticipacaoLote> atuais = getParticipacoesAtuais(idLote);
        if (atuais.isEmpty()) {
            System.out.println("(nenhum proprietário atual definido)");
        } else {
            for (ParticipacaoLote p : atuais) {
                Proprietario dono = proprietarios.get(p.getIdProprietario());
                System.out.println(" - " + dono + " | Créditos: " + p.getQuantidadeCreditos()
                        + " | Início: " + p.getDataInicio());
            }
            int soma = atuais.stream().mapToInt(ParticipacaoLote::getQuantidadeCreditos).sum();
            System.out.println("Soma atual: " + soma + " (deve ser 1000)");
        }

        System.out.println("\n--- Histórico de participações ---");
        List<ParticipacaoLote> hist = participacoesPorLote.get(idLote);
        if (hist.isEmpty()) {
            System.out.println("(sem histórico)");
        } else {
            hist.stream()
                    .sorted(Comparator.comparing(ParticipacaoLote::getDataInicio))
                    .forEach(p -> {
                        Proprietario dono = proprietarios.get(p.getIdProprietario());
                        String fim = (p.getDataFim() == null) ? "ATUAL" : p.getDataFim().toString();
                        System.out.println(" - " + dono.getNome()
                                + " | Créditos: " + p.getQuantidadeCreditos()
                                + " | Início: " + p.getDataInicio()
                                + " | Fim: " + fim);
                    });
        }

        System.out.println("\n--- Transações (histórico de compra/venda) ---");
        List<TransacaoCompraVenda> trans = transacoesPorLote.get(idLote);
        if (trans.isEmpty()) {
            System.out.println("(nenhuma transação)");
        } else {
            for (TransacaoCompraVenda t : trans) {
                String vendedores = t.getIdsVendedores().stream()
                        .map(id -> proprietarios.get(id).getNome())
                        .collect(Collectors.joining(", "));
                Proprietario comp = proprietarios.get(t.getIdComprador());
                System.out.println(" - " + t.getDataTransacao()
                        + " | Vendedores: " + vendedores
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

    private List<ParticipacaoLote> getParticipacoesAtuais(UUID idLote) {
        return participacoesPorLote.get(idLote).stream()
                .filter(ParticipacaoLote::isAtual)
                .toList();
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

    public List<ParticipacaoLote> listarParticipacoesAtuais(UUID idLote) {
        getLoteOrThrow(idLote);
        return List.copyOf(getParticipacoesAtuais(idLote));
    }
}