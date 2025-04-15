package com.project.demo.logic.entity.mission;

import com.project.demo.logic.entity.Objective.Objective;
import com.project.demo.logic.entity.gameType.GameType;
import com.project.demo.logic.entity.user.User;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.util.Date;


@Table(name = "mission")
@Entity
public class Mission {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "created_by_user_id")
    private User createdBy;


    @CreationTimestamp
    private Date createdAt;

    private LocalDate startDate;

    private LocalDate endDate;

    private boolean isDaily;

    private boolean isActive;

    @ManyToOne
    @JoinColumn(name = "objective_id")
    private Objective objective;

    private long experience;

    @ManyToOne
    @JoinColumn(name = "game_type_id")
    private GameType gameType;

    public Mission() {
    }





    public Mission(User createdBy, Integer id, Date createdAt, LocalDate startDate, LocalDate endDate, boolean isDaily, boolean isActive, Objective objective, long experience, GameType gameType) {
        this.createdBy = createdBy;
        this.id = id;
        this.createdAt = createdAt;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isDaily = isDaily;
        this.isActive = isActive;
        this.objective = objective;
        this.experience = experience;
        this.gameType = gameType;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean active) {
        isActive = active;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public boolean getIsDaily() {
        return this.isDaily;
    }

    public void setIsDaily(boolean daily) {
        isDaily = daily;
    }

    public long getExperience() {
        return experience;
    }

    public void setExperience(long experience) {
        this.experience = experience;
    }

    public GameType getGameType() {
        return gameType;
    }

    public void setGameType(GameType gameType) {
        this.gameType = gameType;
    }

    public Objective getObjective() {
        return objective;
    }

    public void setObjective(Objective objective) {
        this.objective = objective;
    }


    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }



    @Override
    public String toString() {
        return "Mission{" +
                "id=" + id +
                ", createdBy=" + createdBy +
                ", createdAt=" + createdAt +
                ", isDaily=" + isDaily +
                ", objective=" + objective +
                ", experience=" + experience +
                ", gameType=" + gameType +
                '}';
    }
}
