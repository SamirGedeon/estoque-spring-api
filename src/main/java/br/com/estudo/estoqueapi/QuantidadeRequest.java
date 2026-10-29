package br.com.estudo.estoqueapi;

import jakarta.validation.constraints.*;
public record QuantidadeRequest(@NotNull @Min(0) Integer quantidade) {}
