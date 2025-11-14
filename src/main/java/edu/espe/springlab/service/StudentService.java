package edu.espe.springlab.service;

import edu.espe.springlab.dto.StudentRequestData;
import edu.espe.springlab.dto.StudentResponse;
import edu.espe.springlab.dto.StudentStatsResponse;

import java.util.List;

public interface StudentService {

    //Crear un estudiante a partir del DTO validado
    StudentResponse create(StudentRequestData request);

    //Busqueda por ID
    StudentResponse getById(Long id);

    //Listar todos los estudiantes
    List<StudentResponse> list();

    //Cambiar estado del estudiante
    StudentResponse deactivate(Long id);

    //Buscar estudiantes por nombre parcial
    List<StudentResponse> findByPartialName(String partialName);

    //Obtener estadísticas de estudiantes
    StudentStatsResponse getStats();
}
