package edu.espe.springlab.web.controller;

import edu.espe.springlab.domain.Student;
import edu.espe.springlab.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@Transactional
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
public class StudentSearchIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private StudentRepository repository;

    @BeforeEach
    void setUp() {
        // Configurar MockMvc
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        // Limpiar la base de datos antes de cada prueba
        repository.deleteAll();
    }

    @Test
    void shouldSearchStudentsByPartialName() throws Exception {
        // Arrange - Guardar: "Ana", "Andrea", "Juan"
        Student ana = new Student();
        ana.setFullName("Ana");
        ana.setEmail("ana@example.com");
        ana.setBirthDate(LocalDate.of(2000, 1, 1));
        ana.setActive(true);

        Student andrea = new Student();
        andrea.setFullName("Andrea");
        andrea.setEmail("andrea@example.com");
        andrea.setBirthDate(LocalDate.of(2000, 2, 2));
        andrea.setActive(true);

        Student juan = new Student();
        juan.setFullName("Juan");
        juan.setEmail("juan@example.com");
        juan.setBirthDate(LocalDate.of(2000, 3, 3));
        juan.setActive(true);

        repository.save(ana);
        repository.save(andrea);
        repository.save(juan);

        // Act & Assert - Buscar "an" y verificar que retorne Ana y Andrea, pero no Juan
        mockMvc.perform(get("/api/students/search")
                .param("name", "an"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))  // Debe retornar exactamente 2 estudiantes
                .andExpect(jsonPath("$[*].fullName", containsInAnyOrder("Ana", "Andrea")))  // Ana y Andrea
                .andExpect(jsonPath("$[*].fullName", not(hasItem("Juan"))));  // Pero NO Juan
    }

    @Test
    void shouldSearchStudentsByPartialNameIgnoreCase() throws Exception {
        // Arrange - Guardar estudiantes con diferentes casos
        Student ana = new Student();
        ana.setFullName("Ana María");
        ana.setEmail("ana.maria@example.com");
        ana.setBirthDate(LocalDate.of(2000, 1, 1));
        ana.setActive(true);

        Student andrea = new Student();
        andrea.setFullName("ANDREA LÓPEZ");
        andrea.setEmail("andrea.lopez@example.com");
        andrea.setBirthDate(LocalDate.of(2000, 2, 2));
        andrea.setActive(true);

        Student antonio = new Student();
        antonio.setFullName("antonio garcía");
        antonio.setEmail("antonio.garcia@example.com");
        antonio.setBirthDate(LocalDate.of(2000, 3, 3));
        antonio.setActive(true);

        Student pedro = new Student();
        pedro.setFullName("Pedro Sánchez");
        pedro.setEmail("pedro.sanchez@example.com");
        pedro.setBirthDate(LocalDate.of(2000, 4, 4));
        pedro.setActive(true);

        repository.save(ana);
        repository.save(andrea);
        repository.save(antonio);
        repository.save(pedro);

        // Act & Assert - Buscar "AN" (mayúscula) debe encontrar Ana, Andrea y Antonio
        mockMvc.perform(get("/api/students/search")
                .param("name", "AN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[*].fullName", containsInAnyOrder("Ana María", "ANDREA LÓPEZ", "antonio garcía")))
                .andExpect(jsonPath("$[*].fullName", not(hasItem("Pedro Sánchez"))));
    }

    @Test
    void shouldReturnEmptyListWhenNoMatches() throws Exception {
        // Arrange - Guardar algunos estudiantes
        Student pedro = new Student();
        pedro.setFullName("Pedro");
        pedro.setEmail("pedro@example.com");
        pedro.setBirthDate(LocalDate.of(2000, 1, 1));
        pedro.setActive(true);

        Student luis = new Student();
        luis.setFullName("Luis");
        luis.setEmail("luis@example.com");
        luis.setBirthDate(LocalDate.of(2000, 2, 2));
        luis.setActive(true);

        repository.save(pedro);
        repository.save(luis);

        // Act & Assert - Buscar "xyz" que no existe
        mockMvc.perform(get("/api/students/search")
                .param("name", "xyz"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));  // Lista vacía
    }

    @Test
    void shouldSearchWithSpecialCharacters() throws Exception {
        // Arrange - Guardar estudiantes con caracteres especiales
        Student maria = new Student();
        maria.setFullName("María José");
        maria.setEmail("maria.jose@example.com");
        maria.setBirthDate(LocalDate.of(2000, 1, 1));
        maria.setActive(true);

        Student jose = new Student();
        jose.setFullName("José María");
        jose.setEmail("jose.maria@example.com");
        jose.setBirthDate(LocalDate.of(2000, 2, 2));
        jose.setActive(true);

        repository.save(maria);
        repository.save(jose);

        // Act & Assert - Buscar "José"
        mockMvc.perform(get("/api/students/search")
                .param("name", "José"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))  // Debe encontrar ambos
                .andExpect(jsonPath("$[*].fullName", containsInAnyOrder("María José", "José María")));
    }
}
