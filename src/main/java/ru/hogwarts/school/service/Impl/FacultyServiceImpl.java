package ru.hogwarts.school.service.Impl;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.exception.MethodNotAllowedException;
import ru.hogwarts.school.exception.NotFoundException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.service.FacultyService;

import java.util.List;

@Service
public class FacultyServiceImpl implements FacultyService {
    private FacultyRepository facultyRepository;

    public FacultyServiceImpl(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    @Override
    public Faculty createFaculty(Faculty faculty) {
        return facultyRepository.save(faculty);
    }

    @Override
    public Faculty getFaculty(Long id) {
        return facultyRepository.findById(id).orElseThrow(() -> new NotFoundException("Faculty is not found for id" + id));
    }

    @Override
    public Faculty editFaculty(Long id, Faculty faculty) {
        Faculty facultyFromDb = getFaculty(id);
        facultyFromDb.setName(faculty.getName());
        facultyFromDb.setColor(faculty.getColor());
        return facultyRepository.save(facultyFromDb);
    }

    @Override
    public void deleteFaculty(Long id) {
        Faculty faculty = getFaculty(id);
        if (faculty.getStudents() != null && !faculty.getStudents().isEmpty()) {
            throw new MethodNotAllowedException("Faculty with stundets is not allowed to delete ");
        }
        facultyRepository.delete(faculty);
    }

    @Override
    public List<Faculty> findAll(String name, String color) {
        if (name != null && color != null) {
            return facultyRepository.findAllByNameIgnoreCaseAndColorIgnoreCase(name, color);
        } else if (name != null) {
            return facultyRepository.findAllByNameIgnoreCase(name);
        } else if (color != null) {
            return facultyRepository.findAllByColorIgnoreCase(color);
        } else {
            return facultyRepository.findAll();
        }
    }

    @Override
    public List<Student> getStudents(Long facultyId) {
        return getFaculty(facultyId).getStudents();
    }
}
