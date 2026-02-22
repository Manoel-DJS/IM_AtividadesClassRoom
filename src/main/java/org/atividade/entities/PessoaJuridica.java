package org.atividade.entities;

public final class PessoaJuridica extends Proprietario {
    private final String cnpj;

    public PessoaJuridica(String nome, String cnpj) {
        super(nome);
        if (cnpj == null || cnpj.isBlank()) {
            throw new IllegalArgumentException("CNPJ não pode ser vazio.");
        }
        this.cnpj = cnpj.trim();
    }

    @Override
    public String getDocumento() { return cnpj; }

    @Override
    public String getTipo() { return "PJ"; }
}