package br.com.estudo.estoqueapi;

import java.math.BigDecimal;
import jakarta.validation.constraints.*;

// DTO de entrada; ID é gerado no servidor.
public record ItemEstoqueRequest(
    @NotBlank(message="nome é obrigatório") @Size(max=120) String nome,
    @NotNull @Min(0) Integer quantidade,
    @NotNull @DecimalMin("0.00") @Digits(integer=9, fraction=2) BigDecimal preco
) {}
