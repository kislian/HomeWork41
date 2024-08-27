package ru.hogwarts.school.service.Impl;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.exception.NotFoundException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.service.FacultyService;
import ru.hogwarts.school.service.StudentService;

import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;
    private final FacultyService facultyService;

    public StudentServiceImpl(StudentRepository studentRepository, FacultyService facultyService) {
        this.studentRepository = studentRepository;
        this.facultyService = facultyService;
    }

    @Override
    public Student getStudent(Long id) {
        return studentRepository.findById(id).orElseThrow(() -> new NotFoundException("Student is not found for id" + id));
    }

    @Override
    public Student createStudent(long facultyId, Student student) {
        Faculty faculty = facultyService.getFaculty(facultyId);
        student.setFaculty(faculty);
        return studentRepository.save(student);
    }

    @Override
    public Student editStudent(Long id, Student student) {
        Student studentFromBd = getStudent(id);
        studentFromBd.setName(student.getName());
        studentFromBd.setAge(student.getAge());
        return studentRepository.save(studentFromBd);
    }

    @Override
    public void deleteStudent(Long id) {
        studentRepository.delete(getStudent(id));
    }

    @Override
    public List<Student> filterByAge(int age) {
        return studentRepository.findAllByAge(age);
    }

    @Override
    public List<Student> findAllByAgeBetween(int fromAge, int toAge) {
        return studentRepository.findAllByAgeBetween(fromAge, toAge);
    }

    @Override
    public Faculty getFaculty(Long studentId) {
        return getStudent(studentId).getFaculty();
    }
}
