package com.project.demo;


import com.project.demo.logic.entity.mission.Mission;
import com.project.demo.logic.entity.missionXUser.MissionXUser;
import com.project.demo.logic.entity.missionXUser.MissionXUserRepository;
import com.project.demo.logic.entity.user.User;
import com.project.demo.service.MissionXUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MissionXUserServiceTest {

    @Mock
    private MissionXUserRepository missionXUserRepository;

    @InjectMocks
    private MissionXUserService missionService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRandomizeMissions_returnsCorrectAmountWithoutRepeats() {
        User user = new User(); // Minimal user setup
        user.setId(1L); // if needed

        // Create missions
        Mission m1 = new Mission();
        m1.setId(1);

        Mission m2 = new Mission();
        m2.setId(2);

        Mission m3 = new Mission();
        m3.setId(3);

        List<Mission> allMissions = List.of(m1, m2, m3);

        // Already assigned m1
        MissionXUser existingMXU = new MissionXUser();
        existingMXU.setMission(m1);
        List<MissionXUser> existingMissions = List.of(existingMXU);

        when(missionXUserRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        List<MissionXUser> result = missionService.randomizeMissions(user, allMissions, existingMissions, 2);

        assertEquals(2, result.size());
        assertTrue(result.stream().noneMatch(mxu -> mxu.getMission().getId().equals(1))); // should skip m1
        assertTrue(result.stream().allMatch(mxu -> mxu.getUser() == user)); // should assign to given user

        verify(missionXUserRepository, times(2)).save(any());
    }

}
