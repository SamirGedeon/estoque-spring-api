package br.com.estudo.estoqueapi;

import java.util.*;
import java.math.BigDecimal;
public interface ItemEstoqueModel {
    ItemEstoque criar(ItemEstoque entrada);
    List<ItemEstoque> listar(String nome);
    Optional<ItemEstoque> buscar(Long id);
    Optional<ItemEstoque> atualizar(Long id, ItemEstoque entrada);
    boolean excluir(Long id);
}
