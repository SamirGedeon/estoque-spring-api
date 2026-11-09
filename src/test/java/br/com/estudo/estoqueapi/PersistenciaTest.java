package br.com.estudo.estoqueapi;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.jdbc.core.JdbcTemplate;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:teste-persistencia;DB_CLOSE_DELAY=-1")
@ActiveProfiles("persistente")
class PersistenciaTest {
    @Autowired ItemEstoqueModel model;
    @Autowired JdbcTemplate jdbc;

    @Test void gravaAtualizaFiltraEExcluiNoBanco() {
        ItemEstoque item = model.criar(new ItemEstoque(null, "Mouse 100%", 3, new BigDecimal("89.90")));
        assertEquals(3, jdbc.queryForObject("SELECT quantidade FROM itens WHERE id = ?", Integer.class, item.getId()));
        assertEquals(1, model.listar("100%").size());
        assertTrue(model.listar("nome inexistente").isEmpty());
        assertTrue(model.atualizar(item.getId(), new ItemEstoque(item.getId(), "Mouse 100%", 8, new BigDecimal("90.00"))).isPresent());
        assertEquals(8, model.buscar(item.getId()).orElseThrow().getQuantidade());
        // Uma nova instância de repositório consulta o registro gravado, sem lista em memória.
        assertEquals(8, new ItemEstoqueModelJdbc(jdbc).buscar(item.getId()).orElseThrow().getQuantidade());
        assertTrue(model.excluir(item.getId()));
        assertFalse(model.excluir(item.getId()));
        assertTrue(model.buscar(item.getId()).isEmpty());
    }
}
