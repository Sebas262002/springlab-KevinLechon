package edu.espe.springlab.web.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

/**
 * Prueba 5 - Interceptor agrega X-Elapsed-Time (1 pt)
 *
 * Debe usar MockMvc para:
 * 1. Llamar a cualquier endpoint GET
 * 2. Verificar: HTTP 200, Header X-Elapsed-Time existe, Su valor no está vacío
 */
@SpringBootTest
@Transactional
public class InterceptorElapsedTimeTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void shouldAddElapsedTimeHeaderOnGetRequest() throws Exception {
        // Llamar a cualquier endpoint GET
        mockMvc.perform(get("/api/students"))
                // Verificar HTTP 200
                .andExpect(status().isOk())
                // Verificar Header X-Elapsed-Time existe
                .andExpect(header().exists("X-Elapsed-Time"))
                // Verificar Su valor no está vacío
                .andExpect(header().string("X-Elapsed-Time", not(emptyString())));
    }

    @Test
    void shouldAddElapsedTimeHeaderOnAnyGetEndpoint() throws Exception {
        // Probar con el endpoint de stats
        mockMvc.perform(get("/api/students/stats"))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Elapsed-Time"))
                .andExpect(header().string("X-Elapsed-Time", not(emptyString())));
    }

    @Test
    void shouldAddElapsedTimeHeaderOnSearchEndpoint() throws Exception {
        // Probar con el endpoint de búsqueda
        mockMvc.perform(get("/api/students/search").param("name", "test"))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Elapsed-Time"))
                .andExpect(header().string("X-Elapsed-Time", not(emptyString())));
    }
}
