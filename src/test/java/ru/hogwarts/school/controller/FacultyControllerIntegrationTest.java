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
import ru.hogwarts.school.repository.FacultyRepository;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;


@SpringBootTest(webEnvironment = RANDOM_PORT)
public class FacultyControllerIntegrationTest {
    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private FacultyRepository facultyRepository;

    @BeforeEach
    public void clearDatabase() {
        facultyRepository.deleteAll();
    }

    @Test
    void shouldCreateFaculty() {
        //дано в задаче подготовили факультет, указали ему название и цвет
        Faculty faculty = new Faculty(1L, "Slytherin", "Red");
        //что произойдёт
        //отправляем запрос на наш сервер и провалидируем результат ниже
        //для этого используем testRestTemplate с набором методов
        //отправляем Post запрос  указываем
        ResponseEntity<Faculty> facultyResponseEntity = testRestTemplate.postForEntity(
                //по какому адресу обращаемся
                "http://localhost:" + port + "/faculties",
                //что отправляем модель
                faculty,
                //какой тип данных хотим получить
                Faculty.class
                //ResponseEntity обёртка ответ от сервера
        );
        //результат
        //убеждаемся что не Null
        assertNotNull(facultyResponseEntity);
        //проверяем что сервер вернул 200
        assertEquals(facultyResponseEntity.getStatusCode(), HttpStatusCode.valueOf(200));
//проверяем сами данные
        //getBody достаёт из тела запроса модель
        //так как мы указали Faculty.class, testRestTemplate
        //автоматически преобразует в модель (получаем не Json)
        Faculty actualFaculty = facultyResponseEntity.getBody();
        //проверяем что у нашего Faculty появился Id
        assertNotNull(actualFaculty.getId());
        //проверяем что то имя и цвет которое мы дали сущности на вход
        //вернул нам сервер
        assertEquals(actualFaculty.getName(), faculty.getName());
        assertThat(actualFaculty.getColor()).isEqualTo(faculty.getColor());
    }

    @Test
    void shouldUpdateFaculty() {
        Faculty faculty = new Faculty(1L, "fit", "blue");
        faculty = facultyRepository.save(faculty);

        Faculty facultyForUpdate = new Faculty(1L, "asu", "yellow");
        ResponseEntity<Faculty> facultyResponseEntity = testRestTemplate.exchange(
                "http://localhost:" + port + "/faculties/" + faculty.getId(),
                HttpMethod.PUT,
                new HttpEntity<Faculty>(facultyForUpdate),
                Faculty.class
        );
        assertNotNull(facultyResponseEntity);
        assertEquals(facultyResponseEntity.getStatusCode(), HttpStatusCode.valueOf(200));
        Faculty actualFaculty = facultyResponseEntity.getBody();
        assertNotNull(facultyForUpdate.getId());
        assertEquals(actualFaculty.getName(), facultyForUpdate.getName());
        assertThat(actualFaculty.getColor()).isEqualTo(facultyForUpdate.getColor());
    }

    @Test
    void shouldGetFaculty() {
        Faculty faculty = new Faculty(1L, "fit", "blue");
        faculty = facultyRepository.save(faculty);

        ResponseEntity<Faculty> facultyResponseEntity = testRestTemplate.getForEntity(
                "http://localhost:" + port + "/faculties/" + faculty.getId(),
                Faculty.class
        );
        assertNotNull(facultyResponseEntity);
        assertEquals(facultyResponseEntity.getStatusCode(), HttpStatusCode.valueOf(200));
        Faculty actualFaculty = facultyResponseEntity.getBody();
        assertNotNull(actualFaculty.getId());
        assertEquals(actualFaculty.getName(), faculty.getName());
        assertThat(actualFaculty.getColor()).isEqualTo(faculty.getColor());
    }

    @Test
    void shouldDeleteFaculty() {
        Faculty faculty = new Faculty(1L, "fit", "blue");
        faculty = facultyRepository.save(faculty);

        ResponseEntity<Faculty> facultyResponseEntity = testRestTemplate.exchange(
                "http://localhost:" + port + "/faculties/" + faculty.getId(),
                HttpMethod.DELETE,
                null,
                Faculty.class
        );
        assertNotNull(facultyResponseEntity);
        assertEquals(facultyResponseEntity.getStatusCode(), HttpStatusCode.valueOf(200));

        //через репозиторий сделали поиск id и убедились что сущности в базе не осталось
        assertThat(facultyRepository.findById(faculty.getId())).isNotPresent();
    }
}
