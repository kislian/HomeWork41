package ru.hogwarts.school.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.service.FacultyService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(FacultyController.class)
public class FacultyControllerWebMvcTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private FacultyService facultyService;

    @Test
    void shouldGetFaculty() throws Exception {
        Long facultyId = 1L;
        Faculty faculty = new Faculty(facultyId, "fit", "red");
        //моккируем обращение к сервису
        when(facultyService.getFaculty(facultyId)).thenReturn(faculty);
        ResultActions perform = mockMvc.perform(get("/faculties/{id}", facultyId));

        perform
                .andExpect(jsonPath("$.name").value(faculty.getName()))
                .andExpect(jsonPath("$.color").value(faculty.getColor()))
                .andDo(print());
    }

    @Test
    void shouldCreateFaculty() throws Exception {
        Long facultyId = 1L;
        Faculty faculty = new Faculty(facultyId, "fit", "red");
        Faculty expectFaculty = new Faculty(facultyId, "fit", "red");
        expectFaculty.setId(facultyId);

        when(facultyService.createFaculty(faculty)).thenReturn(expectFaculty);

        ResultActions perform = mockMvc.perform(post("/faculties")
                .content(objectMapper.writeValueAsString(faculty))
                .contentType(MediaType.APPLICATION_JSON));

        perform
                .andExpect(jsonPath("$.id").value(expectFaculty.getId()))
                .andExpect(jsonPath("$.name").value(expectFaculty.getName()))
                .andExpect(jsonPath("$.color").value(expectFaculty.getColor()))
                .andDo(print());
    }

    @Test
    void shouldUpdateFaculty() throws Exception {
        Long facultyId = 1L;
        Faculty faculty = new Faculty(facultyId, "fit", "red");

        when(facultyService.editFaculty(facultyId, faculty)).thenReturn(faculty);

        ResultActions perform = mockMvc.perform(put("/faculties/{id}", facultyId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(faculty)));

        perform
                .andExpect(jsonPath("$.name").value(faculty.getName()))
                .andExpect(jsonPath("$.color").value(faculty.getColor()))
                .andDo(print());
    }

    @Test
    void shouldDeleteFaculty() throws Exception {
        Long facultyId = 1L;

        mockMvc.perform(delete("/faculties/{id}", facultyId));

        verify(facultyService).deleteFaculty(facultyId);
    }
}
