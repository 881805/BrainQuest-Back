package com.project.demo.logic.entity.interview;

import com.project.demo.logic.entity.conversation.Conversation;
import com.project.demo.logic.entity.user.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Interview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @OneToOne(cascade = CascadeType.ALL)
    private Conversation conversation;

    private Boolean isOngoing = true;

    private Integer elapsedTurns = 0;

    private Integer maxTurns = 5;

    private LocalDateTime creationTime = LocalDateTime.now();

    public Interview() {}

    public Interview(User user, Conversation conversation, Integer maxTurns) {
        this.user = user;
        this.conversation = conversation;
        this.maxTurns = maxTurns;
        this.isOngoing = true;
        this.elapsedTurns = 0;
        this.creationTime = LocalDateTime.now();
    }

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Conversation getConversation() {
        return conversation;
    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }

    public Boolean getOngoing() {
        return isOngoing;
    }

    public void setOngoing(Boolean ongoing) {
        isOngoing = ongoing;
    }

    public Integer getElapsedTurns() {
        return elapsedTurns;
    }

    public void setElapsedTurns(Integer elapsedTurns) {
        this.elapsedTurns = elapsedTurns;
    }

    public Integer getMaxTurns() {
        return maxTurns;
    }

    public void setMaxTurns(Integer maxTurns) {
        this.maxTurns = maxTurns;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
    }

    @Override
    public String toString() {
        return "Interview{" +
                "id=" + id +
                ", user=" + user.getId() +
                ", conversation=" + (conversation != null ? conversation.getId() : null) +
                ", isOngoing=" + isOngoing +
                ", elapsedTurns=" + elapsedTurns +
                ", maxTurns=" + maxTurns +
                '}';
    }
}