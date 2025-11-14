package edu.espe.springlab.service;

import edu.espe.springlab.domain.Student;
import edu.espe.springlab.dto.StudentRequestData;
import edu.espe.springlab.dto.StudentResponse;
import edu.espe.springlab.dto.StudentStatsResponse;
import edu.espe.springlab.repository.StudentRepository;
import edu.espe.springlab.service.impl.StudentServiceImpl;
import edu.espe.springlab.web.advice.ConflictException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class StudentServiceTest {

    @Autowired
    private StudentServiceImpl service;

    @Autowired
    private StudentRepository repository;

    @Test
    void shouldThrowConflictWhenEmailExists() {
        // Crear un estudiante existente
        Student existing = new Student();
        existing.setFullName("Existing Student");
        existing.setEmail("duplicate@example.com");
        existing.setBirthDate(java.time.LocalDate.of(1995, 5, 15));
        existing.setActive(true);
        repository.save(existing);

        // Crear el request con el mismo email
        StudentRequestData req = new StudentRequestData();
        req.setFullName("New Student");
        req.setEmail("duplicate@example.com");
        req.setBirthDate(java.time.LocalDate.of(1995, 5, 15));

        // Verificar que lanza la excepción ConflictException
        assertThrows(ConflictException.class, () -> {
            service.create(req);
        });
    }

    @Test
    void shouldFindStudentsByPartialName() {
        // Arrange - Crear y guardar estudiantes: "Ana", "Andrea", "Juan"
        Student ana = new Student();
        ana.setFullName("Ana");
        ana.setEmail("ana@test.com");
        ana.setBirthDate(java.time.LocalDate.of(2000, 1, 1));
        ana.setActive(true);

        Student andrea = new Student();
        andrea.setFullName("Andrea");
        andrea.setEmail("andrea@test.com");
        andrea.setBirthDate(java.time.LocalDate.of(2000, 2, 2));
        andrea.setActive(true);

        Student juan = new Student();
        juan.setFullName("Juan");
        juan.setEmail("juan@test.com");
        juan.setBirthDate(java.time.LocalDate.of(2000, 3, 3));
        juan.setActive(true);

        repository.save(ana);
        repository.save(andrea);
        repository.save(juan);

        // Act - Buscar "an"
        List<StudentResponse> result = service.findByPartialName("an");

        // Assert - Verificar que retorne Ana y Andrea, pero no Juan
        assertEquals(2, result.size());

        List<String> names = result.stream()
                .map(StudentResponse::getFullName)
                .sorted()
                .toList();

        assertEquals("Ana", names.get(0));
        assertEquals("Andrea", names.get(1));
    }

    @Test
    void shouldDeactivateStudent() {
        // Arrange - Crear un estudiante activo
        Student student = new Student();
        student.setFullName("Test Student");
        student.setEmail("test.deactivate@example.com");
        student.setBirthDate(java.time.LocalDate.of(1995, 6, 15));
        student.setActive(true);
        Student saved = repository.save(student);

        // Act - Llamar al método de desactivación
        StudentResponse deactivated = service.deactivate(saved.getId());

        // Assert - Recuperar el estudiante del repositorio y verificar
        Student retrieved = repository.findById(saved.getId()).orElseThrow();

        // Verificar que active = false
        assertEquals(false, retrieved.getActive());
        assertEquals(false, deactivated.getActive());

        // Verificar que el resto de atributos sin cambios
        assertEquals(saved.getId(), retrieved.getId());
        assertEquals("Test Student", retrieved.getFullName());
        assertEquals("test.deactivate@example.com", retrieved.getEmail());
        assertEquals(java.time.LocalDate.of(1995, 6, 15), retrieved.getBirthDate());
    }

    @Test
    void shouldGetCorrectStats() {
        // Arrange - Crear 3 estudiantes: 2 activos y 1 inactivo
        Student active1 = new Student();
        active1.setFullName("Active Student 1");
        active1.setEmail("active1@example.com");
        active1.setBirthDate(java.time.LocalDate.of(1995, 1, 1));
        active1.setActive(true);

        Student active2 = new Student();
        active2.setFullName("Active Student 2");
        active2.setEmail("active2@example.com");
        active2.setBirthDate(java.time.LocalDate.of(1996, 2, 2));
        active2.setActive(true);

        Student inactive = new Student();
        inactive.setFullName("Inactive Student");
        inactive.setEmail("inactive@example.com");
        inactive.setBirthDate(java.time.LocalDate.of(1997, 3, 3));
        inactive.setActive(false);

        repository.save(active1);
        repository.save(active2);
        repository.save(inactive);

        // Act - Llamar al servicio getStats()
        StudentStatsResponse stats = service.getStats();

        // Assert - Verificar que la respuesta incluya: total=3, activos=2, inactivos=1
        assertEquals(3, stats.getTotal());
        assertEquals(2, stats.getActivos());
        assertEquals(1, stats.getInactivos());
    }
}
