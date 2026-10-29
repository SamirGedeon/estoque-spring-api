package br.com.estudo.estoqueapi;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.math.BigDecimal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value="/itens", produces="application/json")
public class ItemEstoqueController {
    private final ItemEstoqueModel model;
    public ItemEstoqueController(ItemEstoqueModel model) { this.model = model; }
    @PostMapping(consumes="application/json")
    public ResponseEntity<ItemEstoqueResponse> criar(@Valid @RequestBody ItemEstoqueRequest request) {
        ItemEstoque criado = model.criar(ItemEstoqueMapper.toEntity(request, null));
        return ResponseEntity.created(URI.create("/itens/"+criado.getId())).body(ItemEstoqueMapper.toResponse(criado));
    }
    @GetMapping
    public List<ItemEstoqueResponse> listar(@RequestParam(name="nome", required=false) String nome) { return model.listar(nome).stream().map(ItemEstoqueMapper::toResponse).toList(); }
    @GetMapping("/{id}")
    public ItemEstoqueResponse buscar(@PathVariable("id") Long id) { return ItemEstoqueMapper.toResponse(model.buscar(id).orElseThrow(() -> new RecursoNaoEncontradoException(id))); }
    @PutMapping(value="/{id}", consumes="application/json")
    public ItemEstoqueResponse atualizar(@PathVariable("id") Long id, @Valid @RequestBody ItemEstoqueRequest request) {
        return ItemEstoqueMapper.toResponse(model.atualizar(id, ItemEstoqueMapper.toEntity(request,id)).orElseThrow(() -> new RecursoNaoEncontradoException(id)));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable("id") Long id) {
        if (!model.excluir(id)) throw new RecursoNaoEncontradoException(id);
        return ResponseEntity.noContent().build();
    }
    @PatchMapping(value="/{id}/quantidade", consumes="application/json")
    public ItemEstoqueResponse alterarQuantidade(@PathVariable("id") Long id, @Valid @RequestBody QuantidadeRequest request) {
        ItemEstoque atual = model.buscar(id).orElseThrow(() -> new RecursoNaoEncontradoException(id));
        return ItemEstoqueMapper.toResponse(model.atualizar(id, new ItemEstoque(id, atual.getNome(), request.quantidade(), atual.getPreco()))
            .orElseThrow(() -> new RecursoNaoEncontradoException(id)));
    }
}
