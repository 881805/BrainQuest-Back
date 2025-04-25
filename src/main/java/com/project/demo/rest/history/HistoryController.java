package com.project.demo.rest.history;


import com.project.demo.logic.entity.game.Game;
import com.project.demo.logic.entity.game.GameRepository;
import com.project.demo.logic.entity.gameType.GameType;
import com.project.demo.logic.entity.gameType.GameTypeEnum;
import com.project.demo.logic.entity.gameType.GameTypeRepository;
import com.project.demo.logic.entity.history.History;
import com.project.demo.logic.entity.history.HistoryRepository;
import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.http.Meta;
import com.project.demo.logic.entity.user.User;
import com.project.demo.logic.entity.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Sort;

import java.util.Optional;

@RequestMapping("/history")
@RestController
public class HistoryController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HistoryRepository historyRepository;

    @Autowired
    private GameTypeRepository gameTypeRepository;


    @Autowired
    private GameRepository gameRepository;


    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createHistory(@RequestBody History history, HttpServletRequest request) {

        User user = userRepository.findById(history.getUser().getId()).orElse(null);

        Game game = gameRepository.findById(history.getGame().getId()).orElse(null);

        history.setUser(user);
        history.setGame(game);

        historyRepository.save(history);

        return new ResponseEntity<>(history, HttpStatus.CREATED);
    }



    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {

        Pageable pageable = PageRequest.of(page-1, size);
        Page<History> gamePage = historyRepository.findAll(pageable);
        Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
        meta.setTotalPages(gamePage.getTotalPages());
        meta.setTotalElements(gamePage.getTotalElements());
        meta.setPageNumber(gamePage.getNumber() + 1);
        meta.setPageSize(gamePage.getSize());

        return new GlobalResponseHandler().handleResponse("Histories retrieved successfully",
                gamePage.getContent(), HttpStatus.OK, meta);
    }


    @GetMapping("/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());



        User user = userRepository.findById(userId).orElse(null);


        Page<History> gamePage = historyRepository.findAllByUser(user,pageable);

        Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
        meta.setTotalPages(gamePage.getTotalPages());
        meta.setTotalElements(gamePage.getTotalElements());
        meta.setPageNumber(gamePage.getNumber() + 1);
        meta.setPageSize(gamePage.getSize());


        return new GlobalResponseHandler().handleResponse("Games retrieved successfully",
                gamePage.getContent(), HttpStatus.OK, meta);
    }

    @GetMapping("/{gameType}/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getByUserAndGameTypeAndIsActive(
            @PathVariable Long userId,
            @PathVariable String gameType,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {

        Pageable pageable = PageRequest.of(page - 1, size);


        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }


        GameTypeEnum typeEnum;
        try {
            typeEnum = GameTypeEnum.valueOf(gameType.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid game type");
        }

        Optional<GameType> matchedGameType = gameTypeRepository.findByGameType(typeEnum);
        if (matchedGameType.get() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Game type not found");
        }


        Page<History> historyPage = historyRepository
                .findByUserAndGame_GameType_GameTypeAndGame_IsOngoingTrue(user, matchedGameType.get().getGameType(), pageable);

        // Prepare metadata for response
        Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
        meta.setTotalPages(historyPage.getTotalPages());
        meta.setTotalElements(historyPage.getTotalElements());
        meta.setPageNumber(historyPage.getNumber() + 1);
        meta.setPageSize(historyPage.getSize());

        return new GlobalResponseHandler().handleResponse("History retrieved successfully",
                historyPage.getContent(), HttpStatus.OK, meta);
    }


    @PutMapping("/{userID}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateGame(@RequestBody History history, HttpServletRequest request) {
        History foundHistory = historyRepository.findById(history.getId()).orElse(null);
        User user = userRepository.findById(history.getUser().getId()).orElse(null);
        Game game = gameRepository.findById(history.getGame().getId()).orElse(null);
        foundHistory.setUser(user);
        foundHistory.setGame(game);
        foundHistory.setLastPlayed(history.getLastPlayed());

        History savedGame = historyRepository.save(foundHistory);
        return new GlobalResponseHandler().handleResponse(
                "Game successfully updated",
                savedGame,
                HttpStatus.OK,
                request);
    }

    @DeleteMapping("/{historyId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<?> deleteHistory(@PathVariable Long historyId, HttpServletRequest request) {
        Optional<History> foundGame = historyRepository.findById(historyId);
        if(foundGame.isPresent()) {
            historyRepository.delete(foundGame.get());

            return new GlobalResponseHandler().handleResponse("History deleted successfully",
                    foundGame.get(), HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("History id " + historyId + " not found"  ,
                    HttpStatus.NOT_FOUND, request);
        }
    }




}
