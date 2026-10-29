package br.com.estudo.estoqueapi;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest
@AutoConfigureMockMvc
class ApiTest {
    @Autowired MockMvc mvc;
    @Autowired com.fasterxml.jackson.databind.ObjectMapper json;
    @Test void crudFiltroERegras() throws Exception {
        String body="{\"nome\": \"Teclado\", \"quantidade\": 10, \"preco\": 120.0}";
        String response=mvc.perform(post("/itens").contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isCreated()).andExpect(header().exists("Location")).andReturn().getResponse().getContentAsString();
        long id=json.readTree(response).get("id").asLong();
        mvc.perform(get("/itens/"+id)).andExpect(status().isOk()).andExpect(jsonPath("$.nome").value("Teclado"));
        mvc.perform(get("/itens").param("nome","tecla")).andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value(id));
        mvc.perform(patch("/itens/"+id+"/quantidade").contentType(MediaType.APPLICATION_JSON).content("{\"quantidade\": 25}")).andExpect(status().isOk()).andExpect(jsonPath("$.quantidade").value(25));
        mvc.perform(put("/itens/"+id).contentType(MediaType.APPLICATION_JSON).content("{\"nome\": \"Teclado Atualizado\", \"quantidade\": 10, \"preco\": 120.0}")).andExpect(status().isOk()).andExpect(jsonPath("$.nome").value("Teclado Atualizado"));
        mvc.perform(delete("/itens/"+id)).andExpect(status().isNoContent()).andExpect(content().string(""));
        mvc.perform(get("/itens/"+id)).andExpect(status().isNotFound());
        mvc.perform(delete("/itens/"+id)).andExpect(status().isNotFound());
    }
    @Test void validacaoEContratoHttp() throws Exception {
        mvc.perform(post("/itens").contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().isBadRequest()).andExpect(jsonPath("$.campos").isArray());
        mvc.perform(post("/itens").contentType(MediaType.APPLICATION_JSON).content("{")).andExpect(status().isBadRequest());
        mvc.perform(get("/itens/abc")).andExpect(status().isBadRequest());
        mvc.perform(post("/itens").contentType(MediaType.TEXT_PLAIN).content("teste")).andExpect(status().isUnsupportedMediaType());
        mvc.perform(get("/itens").accept(MediaType.TEXT_HTML)).andExpect(status().isNotAcceptable());
    }
}
