package com.project.demo.rest.publication;

import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.http.Meta;
import com.project.demo.logic.entity.publication.Publication;
import com.project.demo.logic.entity.publication.PublicationRepository;
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

import java.util.Optional;

@RequestMapping("/publications")
@RestController

public class PublicationController {

    @Autowired
    private PublicationRepository publicationRepository;

    @Autowired
    private UserRepository userRepository;

    // 🟢 CREATE
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createPublication(@RequestBody Publication publication, HttpServletRequest request) {
        publicationRepository.save(publication);
        return new GlobalResponseHandler().handleResponse("Publication created successfully",
                publication, HttpStatus.CREATED, request);
    }

    // 🔵 GET ALL (paged)
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getAllPublications(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Publication> publicationPage = publicationRepository.findAll(pageable);

        Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
        meta.setTotalPages(publicationPage.getTotalPages());
        meta.setTotalElements(publicationPage.getTotalElements());
        meta.setPageNumber(publicationPage.getNumber() + 1);
        meta.setPageSize(publicationPage.getSize());

        return new GlobalResponseHandler().handleResponse("Publications retrieved successfully",
                publicationPage.getContent(), HttpStatus.OK, meta);
    }

    // 🟠 UPDATE
    @PutMapping("/{publicationId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updatePublication(@PathVariable Long publicationId,
                                               @RequestBody Publication updated,
                                               HttpServletRequest request) {
        Optional<Publication> found = publicationRepository.findById(publicationId);

        if (found.isEmpty()) {
            return new GlobalResponseHandler().handleResponse("Publication ID " + publicationId + " not found",
                    HttpStatus.NOT_FOUND, request);
        }

        Publication existing = found.get();

        if (updated.getAchievement() != null) {
            existing.setAchievement(updated.getAchievement());
        }
        if (updated.getDescription() != null) {
            existing.setDescription(updated.getDescription());
        }
        if (updated.getComment() != null) {
            existing.setComment(updated.getComment());
        }
        if (updated.getUser() != null) {
            Optional<User> user = userRepository.findById(updated.getUser().getId());
            user.ifPresent(existing::setUser);
        }

        Publication saved = publicationRepository.save(existing);

        return new GlobalResponseHandler().handleResponse("Publication updated successfully",
                saved, HttpStatus.OK, request);
    }

    // 🔴 DELETE
    @DeleteMapping("/{publicationId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<?> deletePublication(@PathVariable Long publicationId, HttpServletRequest request) {
        Optional<Publication> found = publicationRepository.findById(publicationId);
        if (found.isPresent()) {
            publicationRepository.delete(found.get());
            return new GlobalResponseHandler().handleResponse("Publication deleted successfully",
                    found.get(), HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Publication ID " + publicationId + " not found",
                    HttpStatus.NOT_FOUND, request);
        }
    }
}