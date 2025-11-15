package edu.espe.springlab.web.controller;

import edu.espe.springlab.domain.Student;
import edu.espe.springlab.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

/**
 * Prueba 5 - Interceptor agrega X-Elapsed-Time
 *
 * Debe usar MockMvc para:
 * 1. Llamar a cualquier endpoint GET
 * 2. Verificar HTTP 200
 * 3. Verificar que Header X-Elapsed-Time existe
 * 4. Verificar que su valor no está vacío
 */
@SpringBootTest
@Transactional
public class Prueba5_InterceptorElapsedTime {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private StudentRepository repository;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void prueba5_InterceptorAgregaElapsedTimeHeader_EndpointGetAll() throws Exception {
        System.out.println("=== EJECUTANDO PRUEBA 5: Interceptor X-Elapsed-Time - GET /api/students ===");

        // Llamar al endpoint GET /api/students
        mockMvc.perform(get("/api/students"))
                .andExpect(status().isOk())  // Verificar HTTP 200
                .andExpect(header().exists("X-Elapsed-Time"))  // Header X-Elapsed-Time existe
                .andExpect(header().string("X-Elapsed-Time", not(emptyString())))  // Su valor no está vacío
                .andExpect(header().string("X-Elapsed-Time", matchesPattern("\\d+ms")));  // Formato correcto (números + ms)

        System.out.println("✓ Prueba completada exitosamente para GET /api/students");
    }

    @Test
    void prueba5_InterceptorAgregaElapsedTimeHeader_EndpointGetStats() throws Exception {
        System.out.println("=== EJECUTANDO PRUEBA 5: Interceptor X-Elapsed-Time - GET /api/students/stats ===");

        // Llamar al endpoint GET /api/students/stats
        mockMvc.perform(get("/api/students/stats"))
                .andExpect(status().isOk())  // Verificar HTTP 200
                .andExpect(header().exists("X-Elapsed-Time"))  // Header X-Elapsed-Time existe
                .andExpect(header().string("X-Elapsed-Time", not(emptyString())))  // Su valor no está vacío
                .andExpect(header().string("X-Elapsed-Time", matchesPattern("\\d+ms")));  // Formato correcto

        System.out.println("✓ Prueba completada exitosamente para GET /api/students/stats");
    }

    @Test
    void prueba5_InterceptorAgregaElapsedTimeHeader_EndpointGetById() throws Exception {
        System.out.println("=== EJECUTANDO PRUEBA 5: Interceptor X-Elapsed-Time - GET /api/students/{id} ===");

        // Crear un estudiante para poder hacer GET por ID
        Student student = new Student();
        student.setFullName("Test Student");
        student.setEmail("test@prueba5.com");
        student.setBirthDate(LocalDate.of(2000, 1, 1));
        student.setActive(true);
        Student saved = repository.save(student);

        // Llamar al endpoint GET /api/students/{id}
        mockMvc.perform(get("/api/students/{id}", saved.getId()))
                .andExpect(status().isOk())  // Verificar HTTP 200
                .andExpect(header().exists("X-Elapsed-Time"))  // Header X-Elapsed-Time existe
                .andExpect(header().string("X-Elapsed-Time", not(emptyString())))  // Su valor no está vacío
                .andExpect(header().string("X-Elapsed-Time", matchesPattern("\\d+ms")));  // Formato correcto

        System.out.println("✓ Prueba completada exitosamente para GET /api/students/" + saved.getId());
    }

    @Test
    void prueba5_InterceptorAgregaElapsedTimeHeader_EndpointSearch() throws Exception {
        System.out.println("=== EJECUTANDO PRUEBA 5: Interceptor X-Elapsed-Time - GET /api/students/search ===");

        // Llamar al endpoint GET /api/students/search
        mockMvc.perform(get("/api/students/search").param("name", "test"))
                .andExpect(status().isOk())  // Verificar HTTP 200
                .andExpect(header().exists("X-Elapsed-Time"))  // Header X-Elapsed-Time existe
                .andExpect(header().string("X-Elapsed-Time", not(emptyString())))  // Su valor no está vacío
                .andExpect(header().string("X-Elapsed-Time", matchesPattern("\\d+ms")));  // Formato correcto

        System.out.println("✓ Prueba completada exitosamente para GET /api/students/search");
    }

    @Test
    void prueba5_VerificarHeaderEnMultiplesLlamadas() throws Exception {
        System.out.println("=== VERIFICANDO X-Elapsed-Time EN MÚLTIPLES LLAMADAS ===");

        // Realizar múltiples llamadas y verificar que el header está presente en todas
        for (int i = 1; i <= 3; i++) {
            System.out.println("Llamada " + i + ":");

            mockMvc.perform(get("/api/students"))
                    .andExpect(status().isOk())
                    .andExpect(header().exists("X-Elapsed-Time"))
                    .andExpect(header().string("X-Elapsed-Time", not(emptyString())))
                    .andExpect(header().string("X-Elapsed-Time", matchesPattern("\\d+ms")));

            System.out.println("  ✓ Header X-Elapsed-Time presente y válido");

            // Pequeña pausa para ver diferentes tiempos
            Thread.sleep(10);
        }

        System.out.println("✓ Header presente en todas las llamadas");
    }

    @Test
    void prueba5_VerificarFormatoDelHeader() throws Exception {
        System.out.println("=== VERIFICANDO FORMATO DEL HEADER X-Elapsed-Time ===");

        // Realizar la llamada y capturar el valor del header
        mockMvc.perform(get("/api/students"))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Elapsed-Time"))
                .andExpect(header().string("X-Elapsed-Time", not(emptyString())))
                // Verificar que el formato es números seguidos de "ms"
                .andExpect(header().string("X-Elapsed-Time", matchesPattern("\\d+ms")))
                // Verificar que el tiempo es un número válido (mayor o igual a 0)
                .andExpect(header().string("X-Elapsed-Time",
                    allOf(
                        containsString("ms"),
                        not(containsString("-"))  // No debe ser negativo
                    )));

        System.out.println("✓ Formato del header es correcto (números + 'ms')");
    }
}
