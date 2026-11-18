package com.practica.api_basica.controller;

import com.jayway.jsonpath.JsonPath;
import com.practica.api_basica.repository.EnMemoriaTareaRepository;
import com.practica.api_basica.service.TareaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas de integración del endpoint DELETE /api/tareas/{id}.
 *
 * El controlador guarda las tareas en memoria, por lo que se reinicia el
 * contexto de Spring antes de cada prueba para que no compartan estado.
 */
@WebMvcTest(TareaController.class)
@Import({TareaService.class, EnMemoriaTareaRepository.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class TareaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("DELETE /api/tareas/{id} elimina la tarea existente y responde 204")
    void eliminaTareaExistenteYRespondeNoContent() throws Exception {
        long idAEliminar = crearTarea("Tarea a eliminar");
        long idConservada = crearTarea("Tarea que debe permanecer");

        mockMvc.perform(delete("/api/tareas/{id}", idAEliminar))
                .andExpect(status().isNoContent());

        // La tarea eliminada ya no se puede consultar.
        mockMvc.perform(get("/api/tareas/{id}", idAEliminar))
                .andExpect(status().isNotFound());

        // El listado conserva únicamente la otra tarea.
        mockMvc.perform(get("/api/tareas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value((int) idConservada))
                .andExpect(jsonPath("$[0].titulo").value("Tarea que debe permanecer"));
    }

    @Test
    @DisplayName("DELETE /api/tareas/{id} responde 404 cuando la tarea no existe")
    void eliminaTareaInexistenteYRespondeNotFound() throws Exception {
        mockMvc.perform(delete("/api/tareas/{id}", 999L))
                .andExpect(status().isNotFound());

        // No se agregó ni se eliminó nada del listado.
        mockMvc.perform(get("/api/tareas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("Una tarea solo se puede eliminar una vez: la segunda vez responde 404")
    void eliminarDosVecesLaMismaTareaRespondeNotFoundLaSegundaVez() throws Exception {
        long id = crearTarea("Tarea de un solo uso");

        mockMvc.perform(delete("/api/tareas/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete("/api/tareas/{id}", id))
                .andExpect(status().isNotFound());
    }

    /**
     * Crea una tarea por la API y devuelve el id asignado por el controlador.
     */
    private long crearTarea(String titulo) throws Exception {
        MvcResult resultado = mockMvc.perform(post("/api/tareas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"titulo":"%s","completada":false}
                                """.formatted(titulo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value(titulo))
                .andReturn();

        Number id = JsonPath.read(
                resultado.getResponse().getContentAsString(StandardCharsets.UTF_8),
                "$.id");
        return id.longValue();
    }
}
