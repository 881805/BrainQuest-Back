package com.project.demo.logic.entity.missionXUser;

import com.project.demo.logic.entity.mission.Mission;
import com.project.demo.logic.entity.user.User;
import jakarta.persistence.*;

import java.util.Date;

@Table(name = "mission_X_user")
@Entity
public class MissionXUser {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "mission_id")
    private Mission mission;

    private boolean isCompleted;

    private Date completedAt;

    private Date lastUpdated;

    private int progress;

    public MissionXUser() {
    }

    public MissionXUser(Integer id, User user, boolean isCompleted, Mission mission, Date completedAt, Date lastUpdated, int progress) {
        this.id = id;
        this.user = user;
        this.isCompleted = isCompleted;
        this.mission = mission;
        this.completedAt = completedAt;
        this.lastUpdated = lastUpdated;
        this.progress = progress;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public boolean getIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(boolean completed) {
        isCompleted = completed;
    }

    public Date getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Date completedAt) {
        this.completedAt = completedAt;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Mission getMission() {
        return mission;
    }

    public void setMission(Mission mission) {
        this.mission = mission;
    }


}
