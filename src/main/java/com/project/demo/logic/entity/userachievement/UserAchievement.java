package com.project.demo.logic.entity.userachievement;

import com.project.demo.logic.entity.achievement.Achievement;
import com.project.demo.logic.entity.user.User;
import jakarta.persistence.*;

import java.awt.print.PrinterGraphics;
import java.time.LocalDateTime;
import java.util.Date;

@Table(name = "user_achievement")
@Entity
public class UserAchievement {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "achievement_id")
    private Achievement achievement;


    private boolean isCompleted;
    private Date completedAt;
    private int progress;

    public UserAchievement() {}

    public UserAchievement(Integer id, User user, Achievement achievement, boolean isCompleted, Date completedAt, int progress) {
        this.id = id;
        this.user = user;
        this.achievement = achievement;
        this.isCompleted = isCompleted;
        this.completedAt = completedAt;
        this.progress = progress;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Achievement getAchievement() {
        return achievement;
    }

    public void setAchievement(Achievement achievement) {
        this.achievement = achievement;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public Date getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Date completedAt) {
        this.completedAt = completedAt;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
