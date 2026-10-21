package com.certifolio;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class StudentApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetStudentsEndpoint() throws Exception {
        mockMvc.perform(get("/api/v1/students")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    void testGetAnalyticsSummaryEndpoint() throws Exception {
        mockMvc.perform(get("/api/v1/analytics/summary")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalStudents").isNumber())
                .andExpect(jsonPath("$.totalCertifications").isNumber());
    }
}
