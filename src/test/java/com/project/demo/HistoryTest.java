package com.project.demo;

import com.project.demo.logic.entity.game.Game;
import com.project.demo.logic.entity.game.GameRepository;
import com.project.demo.logic.entity.history.History;
import com.project.demo.logic.entity.history.HistoryRepository;
import com.project.demo.logic.entity.user.User;
import com.project.demo.logic.entity.user.UserRepository;
import com.project.demo.rest.history.HistoryController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class HistoryTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private HistoryRepository historyRepository;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private HistoryController historyController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateHistory() {
        // Arrange
        User user = new User();
        user.setId(1L);

        Game game = new Game();
        game.setId(2L);

        History history = new History();
        history.setUser(user);
        history.setGame(game);

        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(user));
        when(gameRepository.findById(2L)).thenReturn(java.util.Optional.of(game));
        when(historyRepository.save(any(History.class))).thenReturn(history);

        // Act
        ResponseEntity<?> response = historyController.createHistory(history, request);

        // Assert
        assertEquals(201, response.getStatusCodeValue());
        assertEquals(history, response.getBody());
    }
}
