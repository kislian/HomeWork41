package ru.hogwarts.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.hogwarts.school.model.Faculty;

import java.util.List;

public interface FacultyRepository extends JpaRepository<Faculty,Long> {
    List<Faculty> findAllByColorIgnoreCase(String color);
    List<Faculty> findAllByNameIgnoreCase(String name);
    List<Faculty> findAllByNameIgnoreCaseAndColorIgnoreCase(String name, String Color);
}
