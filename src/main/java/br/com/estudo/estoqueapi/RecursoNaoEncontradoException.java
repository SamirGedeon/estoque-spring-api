package br.com.estudo.estoqueapi;


public class RecursoNaoEncontradoException extends RuntimeException {
    public RecursoNaoEncontradoException(Long id) {
        super("Recurso " + id + " não encontrado");
    }
}
