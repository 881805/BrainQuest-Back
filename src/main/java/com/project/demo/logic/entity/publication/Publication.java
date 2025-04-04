package com.project.demo.logic.entity.publication;

import com.project.demo.logic.entity.user.User;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Entity
@Table(name = "publication")
public class Publication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "achievement")
    private String achievement;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "userId")
    private User user;

    // ---------- Constructors ----------
    public Publication() {}

    public Publication(Long id, String achievement, String description, String comment, User user) {
        this.id = id;
        this.achievement = achievement;
        this.description = description;
        this.comment = comment;
        this.user = user;
    }

    // ---------- Getters and Setters ----------
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }



    public String getAchievement() {
        return achievement;
    }

    public void setAchievement(String achievement) {
        this.achievement = achievement;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}