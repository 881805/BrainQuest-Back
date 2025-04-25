package com.project.demo;

import com.project.demo.logic.entity.missionXUser.MissionXUser;
import com.project.demo.logic.entity.missionXUser.MissionXUserRepository;
import com.project.demo.rest.missionXUser.MissionXUserController;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MissionXUserTest {

    @Mock
    private MissionXUserRepository missionXUserRepository;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private MissionXUserController missionXUserController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testDeleteExistingMission() {

        MissionXUser mission = new MissionXUser();
        mission.setId(1);

        when(missionXUserRepository.findById(1L)).thenReturn(Optional.of(mission));

        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost/misiones/1"));
        ResponseEntity<?> response = missionXUserController.deleteMessage(1L, request);


        assertEquals(200, response.getStatusCodeValue());
        verify(missionXUserRepository).delete(mission);
    }

    @Test
    public void testDeleteNonExistingMission() {
        when(missionXUserRepository.findById(999L)).thenReturn(Optional.empty());

        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost/misiones/1"));
        ResponseEntity<?> response = missionXUserController.deleteMessage(999L, request);


        assertEquals(404, response.getStatusCodeValue());
        verify(missionXUserRepository, never()).delete(any());
    }
}
