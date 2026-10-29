package br.com.estudo.estoqueapi;

public final class ItemEstoqueMapper {
    private ItemEstoqueMapper() {}
    public static ItemEstoque toEntity(ItemEstoqueRequest r, Long id) { return new ItemEstoque(id, r.nome().strip(), r.quantidade(), r.preco()); }
    public static ItemEstoqueResponse toResponse(ItemEstoque item) { return new ItemEstoqueResponse(item.getId(), item.getNome(), item.getQuantidade(), item.getPreco()); }
}
