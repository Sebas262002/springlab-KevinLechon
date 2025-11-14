package edu.espe.springlab.repository;

import edu.espe.springlab.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    //Buscar un estudiante por email
    Optional<Student> findByEmail(String email);
    //Responder si existe el estudiante con ese email
    boolean existsByEmail(String email);
    //Buscar estudiantes por nombre parcial (ignore case)
    List<Student> findByFullNameContainingIgnoreCase(String partialName);
    //Contar estudiantes activos
    long countByActiveTrue();
    //Contar estudiantes inactivos
    long countByActiveFalse();
}
