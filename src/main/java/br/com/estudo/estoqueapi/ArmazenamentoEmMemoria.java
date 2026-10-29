package br.com.estudo.estoqueapi;

import java.util.*;
import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public abstract class ArmazenamentoEmMemoria implements ItemEstoqueModel {
    private final Map<Long, ItemEstoque> itens = new LinkedHashMap<>();
    private final AtomicLong sequencia = new AtomicLong();
    public synchronized ItemEstoque criar(ItemEstoque entrada) {
        Long id = sequencia.incrementAndGet();
        ItemEstoque criado = new ItemEstoque(id, entrada.getNome(), entrada.getQuantidade(), entrada.getPreco());
        itens.put(id, criado);
        return criado;
    }
    public synchronized List<ItemEstoque> listar(String nome) {
        
        return itens.values().stream()
                        .filter(item -> nome == null || nome.isBlank() || item.getNome().toLowerCase(Locale.ROOT).contains(nome.strip().toLowerCase(Locale.ROOT)))
            .toList();
    }
    public synchronized Optional<ItemEstoque> buscar(Long id) { return Optional.ofNullable(itens.get(id)); }
    public synchronized Optional<ItemEstoque> atualizar(Long id, ItemEstoque entrada) {
        if (!itens.containsKey(id)) return Optional.empty();
        ItemEstoque atualizado = new ItemEstoque(id, entrada.getNome(), entrada.getQuantidade(), entrada.getPreco());
        itens.put(id, atualizado);
        return Optional.of(atualizado);
    }
    public synchronized boolean excluir(Long id) { return itens.remove(id) != null; }
}
