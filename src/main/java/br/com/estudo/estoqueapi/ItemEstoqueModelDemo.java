package br.com.estudo.estoqueapi;

import java.math.BigDecimal;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
@Service
@Profile("demo")
public class ItemEstoqueModelDemo extends ArmazenamentoEmMemoria { public ItemEstoqueModelDemo() { criar(new ItemEstoque(null, "Teclado", 10, new BigDecimal("120.0"))); } }
