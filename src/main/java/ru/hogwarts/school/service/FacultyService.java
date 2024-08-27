package ru.hogwarts.school.service;

import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;

import java.util.List;

public interface FacultyService {
    Faculty getFaculty(Long id);

    Faculty createFaculty(Faculty faculty);

    Faculty editFaculty(Long id, Faculty faculty);

    void deleteFaculty(Long id);

    List<Faculty> findAll(String name, String color);

    List<Student> getStudents(Long facultyId);
}
