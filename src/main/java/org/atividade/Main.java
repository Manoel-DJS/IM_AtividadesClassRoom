package org.atividade;

import org.atividade.entities.*;
import org.atividade.exceptions.RegraNegocioException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class Main {
    public static void main(String[] args) {
        SistemaCarbono sistema = new SistemaCarbono();
        Scanner sc = new Scanner(System.in);

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


        System.out.println("==============================================");
        System.out.println("  Sistema de Créditos de Carbono (Console)    ");
        System.out.println("==============================================");
        System.out.println("Dados de exemplo já carregados (PF/PJ + 1 lote).");

        // ===== LOOP DO MENU =====
        while (true) {
            try {
                printMenu();
                int opcao = lerInt(sc, "Escolha uma opção: ");

                switch (opcao) {
                    case 1 -> listarProprietarios(sistema);
                    case 2 -> cadastrarPF(sc, sistema);
                    case 3 -> cadastrarPJ(sc, sistema);
                    case 4 -> criarLote(sc, sistema);
                    case 5 -> listarLotes(sistema);
                    case 6 -> registrarArvore(sc, sistema);
                    case 7 -> definirPrimeiroProprietario(sc, sistema);
                    case 8 -> venderLote(sc, sistema);
                    case 9 -> imprimirRelatorio(sc, sistema);
                    case 0 -> {
                        System.out.println("Saindo... ✅");
                        sc.close();
                        return;
                    }
                    default -> System.out.println("Opção inválida.");
                }
            } catch (RegraNegocioException ex) {
                System.out.println("[REGRA DE NEGÓCIO] " + ex.getMessage());
            } catch (Exception ex) {
                System.out.println("[ERRO] " + ex.getMessage());
            }

            System.out.println();
        }
    }

    // =========================
    // ===== MENU / TELAS ======
    // =========================

    private static void printMenu() {
        System.out.println("----------- MENU -----------");
        System.out.println("1) Listar proprietários");
        System.out.println("2) Cadastrar proprietário PF");
        System.out.println("3) Cadastrar proprietário PJ");
        System.out.println("4) Criar lote (1000 créditos)");
        System.out.println("5) Listar lotes");
        System.out.println("6) Registrar árvore em lote");
        System.out.println("7) Definir primeiro proprietário do lote");
        System.out.println("8) Vender lote");
        System.out.println("9) Imprimir relatório do lote");
        System.out.println("0) Sair");
        System.out.println("----------------------------");
    }

    private static void listarProprietarios(SistemaCarbono sistema) {
        List<Proprietario> lista = sistema.listarProprietarios();

        if (lista.isEmpty()) {
            System.out.println("(nenhum proprietário cadastrado)");
            return;
        }

        System.out.println("=== PROPRIETÁRIOS ===");
        for (int i = 0; i < lista.size(); i++) {
            Proprietario p = lista.get(i);
            System.out.println((i + 1) + ") " + p + " | ID=" + p.getId());
        }
    }

    private static void cadastrarPF(Scanner sc, SistemaCarbono sistema) {
        System.out.println("=== CADASTRAR PF ===");
        String nome = lerLinha(sc, "Nome: ");
        String cpf = lerLinha(sc, "CPF: ");

        Proprietario pf = new PessoaFisica(nome, cpf);
        sistema.cadastrarProprietario(pf);

        System.out.println("PF cadastrado: " + pf);
    }

    private static void cadastrarPJ(Scanner sc, SistemaCarbono sistema) {
        System.out.println("=== CADASTRAR PJ ===");
        String nome = lerLinha(sc, "Razão Social / Nome: ");
        String cnpj = lerLinha(sc, "CNPJ: ");

        Proprietario pj = new PessoaJuridica(nome, cnpj);
        sistema.cadastrarProprietario(pj);

        System.out.println("PJ cadastrado: " + pj);
    }

    private static void criarLote(Scanner sc, SistemaCarbono sistema) {
        System.out.println("=== CRIAR LOTE ===");
        String codigo = lerLinha(sc, "Código do lote: ");

        LoteCreditoCarbono lote = sistema.criarLote(codigo);
        System.out.println("Lote criado: " + lote.getCodigoLote() + " | ID=" + lote.getId() + " | Créditos=" + lote.getTotalCredito());
    }

    private static void listarLotes(SistemaCarbono sistema) {
        List<LoteCreditoCarbono> lotes = sistema.listarLotes();

        if (lotes.isEmpty()) {
            System.out.println("(nenhum lote cadastrado)");
            return;
        }

        System.out.println("=== LOTES ===");
        for (int i = 0; i < lotes.size(); i++) {
            LoteCreditoCarbono l = lotes.get(i);
            System.out.println((i + 1) + ") " + l.getCodigoLote() + " | ID=" + l.getId() + " | Créditos=" + l.getTotalCredito() + " | Status=" + l.getStatus());
        }
    }

    private static void registrarArvore(Scanner sc, SistemaCarbono sistema) {
        System.out.println("=== REGISTRAR ÁRVORE ===");

        UUID idLote = escolherLote(sc, sistema);
        String especie = lerLinha(sc, "Espécie: ");
        double lat = lerDouble(sc, "Latitude (ex: -10.916200): ");
        double lon = lerDouble(sc, "Longitude (ex: -37.668300): ");

        sistema.registrarArvore(idLote, new ArvoreGeradoraCredito(especie, lat, lon));
        System.out.println("Árvore registrada no lote.");
    }

    private static void definirPrimeiroProprietario(Scanner sc, SistemaCarbono sistema) {
        System.out.println("=== DEFINIR PRIMEIRO PROPRIETÁRIO ===");

        UUID idLote = escolherLote(sc, sistema);
        Proprietario p = escolherProprietario(sc, sistema);

        sistema.definirPrimeiroProprietario(idLote, p);
        System.out.println("Primeiro proprietário definido com sucesso.");
    }

    private static void venderLote(Scanner sc, SistemaCarbono sistema) {
        System.out.println("=== VENDER LOTE ===");

        UUID idLote = escolherLote(sc, sistema);

        System.out.println("Selecione o VENDEDOR (deve ser o proprietário atual):");
        Proprietario vendedor = escolherProprietario(sc, sistema);

        System.out.println("Selecione o COMPRADOR:");
        Proprietario comprador = escolherProprietario(sc, sistema);

        BigDecimal valor = lerBigDecimal(sc, "Valor da transação (ex: 1500.00): ");

        sistema.venderLote(idLote, vendedor, comprador, valor);
        System.out.println("Venda registrada com sucesso.");
    }

    private static void imprimirRelatorio(Scanner sc, SistemaCarbono sistema) {
        System.out.println("=== RELATÓRIO DO LOTE ===");
        UUID idLote = escolherLote(sc, sistema);

        System.out.println("\n==================== RELATÓRIO ====================");
        sistema.imprimirRelatorioLote(idLote);
    }

    // =========================
    // ===== SELETORES =========
    // =========================

    private static UUID escolherLote(Scanner sc, SistemaCarbono sistema) {
        List<LoteCreditoCarbono> lotes = sistema.listarLotes();
        if (lotes.isEmpty()) throw new RegraNegocioException("Não há lotes cadastrados.");

        listarLotes(sistema);
        int idx = lerInt(sc, "Escolha o número do lote: ") - 1;

        if (idx < 0 || idx >= lotes.size()) throw new RegraNegocioException("Lote inválido.");
        return lotes.get(idx).getId();
    }

    private static Proprietario escolherProprietario(Scanner sc, SistemaCarbono sistema) {
        List<Proprietario> props = sistema.listarProprietarios();
        if (props.isEmpty()) throw new RegraNegocioException("Não há proprietários cadastrados.");

        listarProprietarios(sistema);
        int idx = lerInt(sc, "Escolha o número do proprietário: ") - 1;

        if (idx < 0 || idx >= props.size()) throw new RegraNegocioException("Proprietário inválido.");
        return props.get(idx);
    }

    // =========================
    // ===== LEITURA SAFE ======
    // =========================

    private static int lerInt(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = sc.nextLine().trim();
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                System.out.println("Digite um número inteiro válido.");
            }
        }
    }

    private static double lerDouble(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = sc.nextLine().trim().replace(",", ".");
            try {
                return Double.parseDouble(s);
            } catch (NumberFormatException e) {
                System.out.println("Digite um número decimal válido (use ponto ou vírgula).");
            }
        }
    }

    private static BigDecimal lerBigDecimal(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = sc.nextLine().trim().replace(",", ".");
            try {
                return new BigDecimal(s);
            } catch (NumberFormatException e) {
                System.out.println("Digite um valor válido (ex: 1500.00).");
            }
        }
    }

    private static String lerLinha(Scanner sc, String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }
}