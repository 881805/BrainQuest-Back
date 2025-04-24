package com.project.demo.logic.entity.achievement;

import com.project.demo.logic.entity.aim.Aim;
import com.project.demo.logic.entity.gameType.GameType;
import com.project.demo.logic.entity.user.User;
import jakarta.persistence.*;

@Table(name = "Achievement")
@Entity
public class Achievement {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Integer id;

    private String name;
    private String description;
    private long experience;

    @ManyToOne
    @JoinColumn(name = "created_by_user_id")
    private User createdBy;

    @ManyToOne
    @JoinColumn(name = "aim_id")
    private Aim aim;

    @ManyToOne
    @JoinColumn(name = "game_type_id")
    private GameType gameType;

    public Achievement() {}

    public Achievement(Integer id, String name, String description, long experience ,User createdBy, Aim aim,  GameType gameType) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.experience = experience;
        this.createdBy = createdBy;
        this.aim = aim;
        this.gameType = gameType;
    }

    @Override
    public String toString() {
        return "Achievement { " +
                "id=" + id +
                ", name=" + name +
                ", description=" + description +
                ", experience=" + experience +
                ", createdBy= " + createdBy +
                ", aim= " + aim +
                ", gameType= " + gameType +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getExperience() {
        return experience;
    }

    public void setExperience(long experience) {
        this.experience = experience;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public Aim getAim() {
        return aim;
    }

    public void setAim(Aim aim) {
        this.aim = aim;
    }

    public GameType getGameType() {
        return gameType;
    }

    public void setGameType(GameType gameType) {
        this.gameType = gameType;
    }
}
