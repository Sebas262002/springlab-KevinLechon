package edu.espe.springlab.service;

import edu.espe.springlab.domain.Student;
import edu.espe.springlab.dto.StudentRequestData;
import edu.espe.springlab.repository.StudentRepository;
import edu.espe.springlab.service.impl.StudentServiceImpl;
import edu.espe.springlab.web.advice.ConflictException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertThrows;

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
}
