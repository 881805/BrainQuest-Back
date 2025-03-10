package com.project.demo.logic.entity.level;

import jakarta.persistence.*;

@Entity
@Table(name = "level")
public class Level {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int expRequired;
    private String description;

    public Level() {
    }

    public Level(String name, int expRequired, String description) {
        this.name = name;
        this.expRequired = expRequired;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getExpRequired() {
        return expRequired;
    }

    public void setExpRequired(int expRequired) {
        this.expRequired = expRequired;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Level{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", expRequired=" + expRequired +
                ", description='" + description + '\'' +
                '}';
    }
}
