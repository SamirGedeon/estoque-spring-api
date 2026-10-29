package br.com.estudo.estoqueapi;

import java.math.BigDecimal;

public record ItemEstoqueResponse(Long id, String nome, Integer quantidade, BigDecimal preco) {}
