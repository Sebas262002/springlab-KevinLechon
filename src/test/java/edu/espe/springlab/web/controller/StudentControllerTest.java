package edu.espe.springlab.web.controller;

import edu.espe.springlab.domain.Student;
import edu.espe.springlab.repository.StudentRepository;
import edu.espe.springlab.service.StudentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@Transactional
public class StudentControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private StudentRepository repository;

    @Autowired
    private StudentService studentService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void shouldReturnElapsedTimeHeaderOnGetRequest() throws Exception {
        // Prueba 5: Interceptor agrega X-Elapsed-Time
        // Llamar a cualquier endpoint GET
        mockMvc.perform(get("/api/students"))
                .andExpect(status().isOk())  // Verificar HTTP 200
                .andExpect(header().exists("X-Elapsed-Time"))  // Header X-Elapsed-Time existe
                .andExpect(header().string("X-Elapsed-Time", not(emptyString())));  // Su valor no está vacío
    }

    @Test
    void shouldReturnStatsCorrectly() throws Exception {
        // Prueba 4: Estadísticas /stats
        // Crear 3 estudiantes: 2 activos y 1 inactivo
        Student active1 = new Student();
        active1.setFullName("Active Student 1");
        active1.setEmail("active1@stats.com");
        active1.setBirthDate(LocalDate.of(1995, 1, 1));
        active1.setActive(true);

        Student active2 = new Student();
        active2.setFullName("Active Student 2");
        active2.setEmail("active2@stats.com");
        active2.setBirthDate(LocalDate.of(1996, 2, 2));
        active2.setActive(true);

        Student inactive = new Student();
        inactive.setFullName("Inactive Student");
        inactive.setEmail("inactive@stats.com");
        inactive.setBirthDate(LocalDate.of(1997, 3, 3));
        inactive.setActive(false);

        repository.save(active1);
        repository.save(active2);
        repository.save(inactive);

        // Llamar al endpoint /stats
        mockMvc.perform(get("/api/students/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total", is(3)))     // total = 3
                .andExpect(jsonPath("$.activos", is(2)))   // activos = 2
                .andExpect(jsonPath("$.inactivos", is(1))); // inactivos = 1
    }

    @Test
    void shouldSearchByPartialName() throws Exception {
        // Prueba 6: Funcionalidad extra - búsqueda por nombre parcial
        // Guardar: "Ana", "Andrea", "Juan"
        Student ana = new Student();
        ana.setFullName("Ana");
        ana.setEmail("ana@search.com");
        ana.setBirthDate(LocalDate.of(2000, 1, 1));
        ana.setActive(true);

        Student andrea = new Student();
        andrea.setFullName("Andrea");
        andrea.setEmail("andrea@search.com");
        andrea.setBirthDate(LocalDate.of(2000, 2, 2));
        andrea.setActive(true);

        Student juan = new Student();
        juan.setFullName("Juan");
        juan.setEmail("juan@search.com");
        juan.setBirthDate(LocalDate.of(2000, 3, 3));
        juan.setActive(true);

        repository.save(ana);
        repository.save(andrea);
        repository.save(juan);

        // Buscar "an"
        mockMvc.perform(get("/api/students/search").param("name", "an"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))  // Retorna 2 elementos
                .andExpect(jsonPath("$[*].fullName", containsInAnyOrder("Ana", "Andrea")));  // Ana y Andrea, pero no Juan
    }

    @Test
    void shouldDeactivateStudentViaPatch() throws Exception {
        // Prueba 3: Desactivar estudiante (PATCH)
        // Crear un estudiante activo
        Student student = new Student();
        student.setFullName("Test Student");
        student.setEmail("test.patch@example.com");
        student.setBirthDate(LocalDate.of(1995, 6, 15));
        student.setActive(true);
        Student saved = repository.save(student);

        // Llamar al método de desactivación
        mockMvc.perform(patch("/api/students/{id}/deactivate", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active", is(false)))  // active = false
                .andExpect(jsonPath("$.id", is(saved.getId().intValue())))  // ID sin cambios
                .andExpect(jsonPath("$.fullName", is("Test Student")))  // fullName sin cambios
                .andExpect(jsonPath("$.email", is("test.patch@example.com")));  // email sin cambios

        // Verificar en el repositorio que el estudiante está desactivado
        Student retrieved = repository.findById(saved.getId()).orElseThrow();
        assert !retrieved.getActive();
    }
}
