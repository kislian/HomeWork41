package ru.hogwarts.school.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;


@SpringBootTest(webEnvironment = RANDOM_PORT)
public class StudentControllerIntegrationTest {
    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private StudentRepository studentRepository;

    @BeforeEach
    public void clearDatabase() {
        studentRepository.deleteAll();
    }

    @Test
    void shouldCreateStudent() {
        Faculty faculty = new Faculty(1L, "Slytherin", "Red");
        ResponseEntity<Faculty> facultyResponseEntity = testRestTemplate.postForEntity(
                "http://localhost:" + port + "/faculties",
                faculty,
                Faculty.class
        );

        Student student = new Student(1L, "Petrov", 30);
        ResponseEntity<Student> studentResponseEntity = testRestTemplate.postForEntity(
                "http://localhost:" + port + "/students?facultyId=" + facultyResponseEntity.getBody().getId(),
                student,
                Student.class
        );
        assertNotNull(studentResponseEntity);
        assertEquals(studentResponseEntity.getStatusCode(), HttpStatusCode.valueOf(200));
        Student expectStudent = studentResponseEntity.getBody();
        assertNotNull(expectStudent.getId());
        assertEquals(expectStudent.getName(), student.getName());
        assertThat(expectStudent.getAge()).isEqualTo(student.getAge());
    }

    @Test
    void shouldUpdateStudent() {
        Student student = new Student(1L, "Sidorov", 30);
        student = studentRepository.save(student);

        Student expectStudent = new Student(1L, "Sidorov", 30);
        ResponseEntity<Student> studentResponseEntity = testRestTemplate.exchange(
                "http://localhost:" + port + "/students/" + student.getId(),
                HttpMethod.PUT,
                new HttpEntity(student),
                Student.class
        );
        assertNotNull(studentResponseEntity);
        assertEquals(studentResponseEntity.getStatusCode(), HttpStatusCode.valueOf(200));
        expectStudent = studentResponseEntity.getBody();
        assertNotNull(expectStudent.getId());
        assertEquals(expectStudent.getName(), student.getName());
        assertThat(expectStudent.getAge()).isEqualTo(student.getAge());
    }

    @Test
    void shouldGetStudent() {
        Student student = new Student(1L, "Ivanov", 30);
        student = studentRepository.save(student);

        ResponseEntity<Student> studentResponseEntity = testRestTemplate.getForEntity(
                "http://localhost:" + port + "/students/" + student.getId(),
                Student.class
        );
        assertNotNull(studentResponseEntity);
        assertEquals(studentResponseEntity.getStatusCode(), HttpStatusCode.valueOf(200));
        Student expectStudent = studentResponseEntity.getBody();
        assertNotNull(expectStudent.getId());
        assertEquals(expectStudent.getName(), student.getName());
        assertThat(expectStudent.getAge()).isEqualTo(student.getAge());
    }

    @Test
    void shouldDeleteStudent() {
        Student student = new Student(1L, "Ivanov", 30);
        student = studentRepository.save(student);

        ResponseEntity<Student> studentResponseEntity = testRestTemplate.exchange(
                "http://localhost:" + port + "/students/" + student.getId(),
                HttpMethod.DELETE,
                null,
                Student.class
        );
        assertNotNull(studentResponseEntity);
        assertEquals(studentResponseEntity.getStatusCode(), HttpStatusCode.valueOf(200));

        //через репозиторий сделали поиск id и убедились что сущности в базе не осталось
        assertThat(studentRepository.findById(student.getId())).isNotPresent();
    }
}
