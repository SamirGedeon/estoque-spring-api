package br.com.estudo.estoqueapi;


import java.util.List;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

// A classe base também padroniza erros de conversão e negociação HTTP.
@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {
    public record CampoErro(String campo, String mensagem) {}
    public record Erro(int status, String mensagem, List<CampoErro> campos) {}

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<Erro> naoEncontrado(RecursoNaoEncontradoException ex) {
        return ResponseEntity.status(404).body(new Erro(404, ex.getMessage(), List.of()));
    }
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {
        List<CampoErro> campos = ex.getBindingResult().getFieldErrors().stream()
            .map(e -> new CampoErro(e.getField(), e.getDefaultMessage())).toList();
        return new ResponseEntity<>(new Erro(400, "Dados inválidos", campos), headers, status);
    }
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String mensagem = switch (status.value()) {
            case 400 -> "JSON ou parâmetro inválido";
            case 405 -> "Método HTTP não permitido";
            case 406 -> "Formato de resposta não disponível";
            case 415 -> "Envie Content-Type: application/json";
            default -> "Não foi possível processar a requisição";
        };
        return new ResponseEntity<>(new Erro(status.value(), mensagem, List.of()), headers, status);
    }
}
