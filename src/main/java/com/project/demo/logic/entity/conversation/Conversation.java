package com.project.demo.logic.entity.conversation;


import com.project.demo.logic.entity.user.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "conversation")
public class Conversation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "userId", nullable = false)
    private User user1;

    @ManyToOne
    @JoinColumn(name = "user_id2", referencedColumnName = "userId", nullable = true)
    private User user2;

    private LocalDateTime createDate;
    private boolean isMultiplayer;
    private String gameId;

    public Conversation() {
    }

    public Conversation(User user1, User user2, LocalDateTime createDate, boolean isMultiplayer, String gameId) {
        this.user1 = user1;
        this.user2 = user2;
        this.createDate = createDate;
        this.isMultiplayer = isMultiplayer;
        this.gameId = gameId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser1() {
        return user1;
    }

    public void setUser1(User user1) {
        this.user1 = user1;
    }

    public User getUser2() {
        return user2;
    }

    public void setUser2(User user2) {
        this.user2 = user2;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    public boolean isMultiplayer() {
        return isMultiplayer;
    }

    public void setMultiplayer(boolean multiplayer) {
        isMultiplayer = multiplayer;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    @Override
    public String toString() {
        return "Conversation{" +
                "id=" + id +
                ", user1=" + user1 +
                ", user2=" + user2 +
                ", createDate=" + createDate +
                ", isMultiplayer=" + isMultiplayer +
                ", gameId='" + gameId + '\'' +
                '}';
    }
}
