package com.project.demo.rest.typing;

import com.project.demo.logic.entity.typing.Typing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.project.demo.service.TypingService;
import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.http.Meta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import jakarta.servlet.http.HttpServletRequest;
@RestController
@RequestMapping("/typing")
public class TypingController {

    @Autowired
    private TypingService typingService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/generate")
    public ResponseEntity<?> generateTypingExercise(@RequestBody Typing typingRequest) {
        try {
            Typing result = typingService.generateTypingExercise(
                    typingRequest.getCategory(), typingRequest.getDifficulty()
            );
            return new ResponseEntity<>(result, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getAllTypingExercises(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request
    ) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Typing> typingPage = typingService.getAllTypingExercises(pageable);

        Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
        meta.setTotalPages(typingPage.getTotalPages());
        meta.setTotalElements(typingPage.getTotalElements());
        meta.setPageNumber(typingPage.getNumber() + 1);
        meta.setPageSize(typingPage.getSize());

        return new GlobalResponseHandler().handleResponse("Typing exercises retrieved successfully",
                typingPage.getContent(), HttpStatus.OK, meta);
    }
}