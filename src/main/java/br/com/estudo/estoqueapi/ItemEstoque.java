package br.com.estudo.estoqueapi;

import java.math.BigDecimal;

public class ItemEstoque {
    private final Long id;
    private final String nome;
    private final Integer quantidade;
    private final BigDecimal preco;
    public ItemEstoque(Long id, String nome, Integer quantidade, BigDecimal preco) {
        this.id = id;
        this.nome = nome;
        this.quantidade = quantidade;
        this.preco = preco;
    }
    public Long getId() { return id; }
    public String getNome() { return nome; }
    public Integer getQuantidade() { return quantidade; }
    public BigDecimal getPreco() { return preco; }
}
