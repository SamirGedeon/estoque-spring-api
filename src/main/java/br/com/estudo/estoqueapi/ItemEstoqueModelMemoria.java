package br.com.estudo.estoqueapi;

import java.math.BigDecimal;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
@Service
@Profile("memoria")
public class ItemEstoqueModelMemoria extends ArmazenamentoEmMemoria {  }
