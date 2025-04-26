package com.project.demo;

import com.project.demo.logic.entity.game.Game;

import com.project.demo.logic.entity.game.GameRepository;
import com.project.demo.service.GameService;
import com.project.demo.service.InterviewService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class InterviewServiceTest {

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private GameService gameService;

    @InjectMocks
    private InterviewService interviewService;


    public InterviewServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testIncrementTurn() {
        Game game = new Game();
        game.setElapsedTurns(5);

        interviewService.incrementTurn(game);

        assertEquals(6, game.getElapsedTurns());
        verify(gameRepository, times(1)).save(game);
    }
}
