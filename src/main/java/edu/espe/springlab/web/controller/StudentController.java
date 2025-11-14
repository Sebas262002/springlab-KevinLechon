package edu.espe.springlab.web.controller;

import edu.espe.springlab.dto.StudentRequestData;
import edu.espe.springlab.dto.StudentResponse;
import edu.espe.springlab.dto.StudentStatsResponse;
import edu.espe.springlab.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) { this.studentService = studentService; }

    @PostMapping
    public ResponseEntity<StudentResponse> create(@Valid @RequestBody StudentRequestData request){
        return ResponseEntity.status(HttpStatus.CREATED).body(studentService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentResponse> getById(@PathVariable Long id){
        return ResponseEntity.ok(studentService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<StudentResponse>> getAll(){
        return ResponseEntity.ok(studentService.list());
    }

    //Kevin Lechon
    //Desactivar un estudiante
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<StudentResponse> deactivate(@PathVariable Long id){
        return ResponseEntity.ok(studentService.deactivate(id));
    }

    //Entrega el total de estudiantes y el total de estudiantes activos
    //Kevin Lechon
    @GetMapping("/stats")
    public ResponseEntity<StudentStatsResponse> getStats(){
        return ResponseEntity.ok(studentService.getStats());
    }

    //Kevin Lechon
    @GetMapping("/search")
    public ResponseEntity<List<StudentResponse>> searchByName(@RequestParam String name){
        return ResponseEntity.ok(studentService.findByPartialName(name));
    }
}
