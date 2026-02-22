package org.atividade.entities;

public final class PessoaFisica extends Proprietario {
    private final String cpf;

    public PessoaFisica(String nome, String cpf) {
        super(nome);
        if (cpf == null || cpf.isBlank()) {
            throw new IllegalArgumentException("CPF não pode ser vazio.");
        }
        this.cpf = cpf.trim();
    }

    @Override
    public String getDocumento() { return cpf; }

    @Override
    public String getTipo() { return "PF"; }
}