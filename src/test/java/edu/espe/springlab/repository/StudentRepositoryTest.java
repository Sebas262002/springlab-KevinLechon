package edu.espe.springlab.repository;

import edu.espe.springlab.domain.Student;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
public class StudentRepositoryTest {
    @Autowired
    private StudentRepository repository;

    @Test
    void shouldSaveAndFindStudentByEmail() {
        Student s = new Student();
        s.setFullName("Test User");
        s.setEmail("test@example.com");
        s.setBirthDate(LocalDate.of(2000,10,10));
        s.setActive(true);

        repository.save(s);

        var result = repository.findByEmail("test@example.com");
        assertThat(result).isPresent();
        assertThat(result.get().getFullName()).isEqualTo("Test User");
    }

    @Test
    void shouldFindStudentsByPartialName() {
        // Arrange - Guardar estudiantes: "Ana", "Andrea", "Juan"
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

        // Act - Buscar "an"
        List<Student> result = repository.findByFullNameContainingIgnoreCase("an");

        // Assert - Verificar que retorne Ana y Andrea, pero no Juan
        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(Student::getFullName)
                .containsExactlyInAnyOrder("Ana", "Andrea");
    }

    @Test
    void shouldFindByPartialName_ExactRequirement() {
        // Prueba 6: Caso específico requerido
        // Arrange - Guardar: "Ana", "Andrea", "Juan"
        Student ana = new Student();
        ana.setFullName("Ana");
        ana.setEmail("ana.prueba6@example.com");
        ana.setBirthDate(LocalDate.of(2000, 1, 1));
        ana.setActive(true);

        Student andrea = new Student();
        andrea.setFullName("Andrea");
        andrea.setEmail("andrea.prueba6@example.com");
        andrea.setBirthDate(LocalDate.of(2000, 2, 2));
        andrea.setActive(true);

        Student juan = new Student();
        juan.setFullName("Juan");
        juan.setEmail("juan.prueba6@example.com");
        juan.setBirthDate(LocalDate.of(2000, 3, 3));
        juan.setActive(true);

        repository.save(ana);
        repository.save(andrea);
        repository.save(juan);

        // Act - Buscar "an"
        List<Student> result = repository.findByFullNameContainingIgnoreCase("an");

        // Assert - Verificar que retorne Ana y Andrea, pero no Juan
        assertThat(result).hasSize(2);

        // Verificar nombres específicos
        List<String> foundNames = result.stream()
                .map(Student::getFullName)
                .sorted()
                .toList();

        assertThat(foundNames).containsExactly("Ana", "Andrea");

        // Verificar explícitamente que Juan NO está incluido
        assertThat(result).noneMatch(student -> "Juan".equals(student.getFullName()));
    }
}
