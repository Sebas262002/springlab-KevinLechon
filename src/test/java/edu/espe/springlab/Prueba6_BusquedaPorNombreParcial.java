package edu.espe.springlab;

import edu.espe.springlab.domain.Student;
import edu.espe.springlab.repository.StudentRepository;
import edu.espe.springlab.service.StudentService;
import edu.espe.springlab.dto.StudentResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Prueba específica para la Prueba 6 - Funcionalidad extra: búsqueda por nombre parcial
 *
 * Esta clase contiene la prueba exacta requerida:
 * 1. Guardar: "Ana", "Andrea", "Juan"
 * 2. Buscar "an"
 * 3. Verificar que retorne Ana y Andrea, pero no Juan
 */
@SpringBootTest
@Transactional
public class Prueba6_BusquedaPorNombreParcial {

    @Autowired
    private StudentRepository repository;

    @Autowired
    private StudentService studentService;

    @Test
    void prueba6_BusquedaPorNombreParcial_CasoCompleto() {
        System.out.println("=== EJECUTANDO PRUEBA 6: Búsqueda por nombre parcial ===");

        // 1. Guardar: "Ana", "Andrea", "Juan"
        System.out.println("1. Guardando estudiantes: Ana, Andrea, Juan");

        Student ana = new Student();
        ana.setFullName("Ana");
        ana.setEmail("ana@prueba6.com");
        ana.setBirthDate(LocalDate.of(2000, 1, 1));
        ana.setActive(true);

        Student andrea = new Student();
        andrea.setFullName("Andrea");
        andrea.setEmail("andrea@prueba6.com");
        andrea.setBirthDate(LocalDate.of(2000, 2, 2));
        andrea.setActive(true);

        Student juan = new Student();
        juan.setFullName("Juan");
        juan.setEmail("juan@prueba6.com");
        juan.setBirthDate(LocalDate.of(2000, 3, 3));
        juan.setActive(true);

        Student anaGuardada = repository.save(ana);
        Student andreaGuardada = repository.save(andrea);
        Student juanGuardado = repository.save(juan);

        System.out.println("   - Ana guardada con ID: " + anaGuardada.getId());
        System.out.println("   - Andrea guardada con ID: " + andreaGuardada.getId());
        System.out.println("   - Juan guardado con ID: " + juanGuardado.getId());

        // 2. Buscar "an" usando el repositorio directamente
        System.out.println("2. Buscando con 'an' en el repositorio...");
        List<Student> resultadoRepo = repository.findByFullNameContainingIgnoreCase("an");

        System.out.println("   Resultados del repositorio:");
        resultadoRepo.forEach(student ->
            System.out.println("   - " + student.getFullName() + " (ID: " + student.getId() + ")")
        );

        // 3. Buscar "an" usando el servicio
        System.out.println("3. Buscando con 'an' en el servicio...");
        List<StudentResponse> resultadoServicio = studentService.findByPartialName("an");

        System.out.println("   Resultados del servicio:");
        resultadoServicio.forEach(response ->
            System.out.println("   - " + response.getFullName() + " (ID: " + response.getId() + ")")
        );

        // 4. Verificaciones
        System.out.println("4. Verificaciones:");

        // Verificar repositorio
        assertEquals(2, resultadoRepo.size(), "El repositorio debe retornar exactamente 2 estudiantes");
        System.out.println("   ✓ Repositorio retorna 2 estudiantes");

        List<String> nombresRepo = resultadoRepo.stream()
                .map(Student::getFullName)
                .sorted()
                .toList();

        assertTrue(nombresRepo.contains("Ana"), "Debe incluir a Ana");
        assertTrue(nombresRepo.contains("Andrea"), "Debe incluir a Andrea");
        assertFalse(nombresRepo.contains("Juan"), "NO debe incluir a Juan");
        System.out.println("   ✓ Repositorio incluye Ana y Andrea, excluye Juan");

        // Verificar servicio
        assertEquals(2, resultadoServicio.size(), "El servicio debe retornar exactamente 2 estudiantes");
        System.out.println("   ✓ Servicio retorna 2 estudiantes");

        List<String> nombresServicio = resultadoServicio.stream()
                .map(StudentResponse::getFullName)
                .sorted()
                .toList();

        assertTrue(nombresServicio.contains("Ana"), "Servicio debe incluir a Ana");
        assertTrue(nombresServicio.contains("Andrea"), "Servicio debe incluir a Andrea");
        assertFalse(nombresServicio.contains("Juan"), "Servicio NO debe incluir a Juan");
        System.out.println("   ✓ Servicio incluye Ana y Andrea, excluye Juan");

        // Verificaciones adicionales
        assertEquals(List.of("Ana", "Andrea"), nombresRepo, "Los nombres deben ser exactamente Ana y Andrea");
        assertEquals(List.of("Ana", "Andrea"), nombresServicio, "Los nombres deben ser exactamente Ana y Andrea");

        System.out.println("   ✓ Todos los nombres son correctos en orden alfabético");

        System.out.println("=== PRUEBA 6 COMPLETADA EXITOSAMENTE ===");
    }

    @Test
    void prueba6_VerificarIgnoreCase() {
        System.out.println("=== VERIFICANDO IGNORE CASE ===");

        // Guardar estudiantes
        Student ana = new Student();
        ana.setFullName("Ana");
        ana.setEmail("ana@case.com");
        ana.setBirthDate(LocalDate.of(2000, 1, 1));
        ana.setActive(true);

        Student andrea = new Student();
        andrea.setFullName("ANDREA");  // Mayúsculas
        andrea.setEmail("andrea@case.com");
        andrea.setBirthDate(LocalDate.of(2000, 2, 2));
        andrea.setActive(true);

        Student juan = new Student();
        juan.setFullName("juan");  // Minúsculas
        juan.setEmail("juan@case.com");
        juan.setBirthDate(LocalDate.of(2000, 3, 3));
        juan.setActive(true);

        repository.save(ana);
        repository.save(andrea);
        repository.save(juan);

        // Buscar con diferentes casos
        List<Student> resultadoMinusculas = repository.findByFullNameContainingIgnoreCase("an");
        List<Student> resultadoMayusculas = repository.findByFullNameContainingIgnoreCase("AN");
        List<Student> resultadoMixto = repository.findByFullNameContainingIgnoreCase("An");

        System.out.println("Búsqueda 'an': " + resultadoMinusculas.size() + " resultados");
        System.out.println("Búsqueda 'AN': " + resultadoMayusculas.size() + " resultados");
        System.out.println("Búsqueda 'An': " + resultadoMixto.size() + " resultados");

        // Todas las búsquedas deben retornar los mismos resultados
        assertEquals(2, resultadoMinusculas.size());
        assertEquals(2, resultadoMayusculas.size());
        assertEquals(2, resultadoMixto.size());

        System.out.println("✓ IgnoreCase funciona correctamente");
    }
}
