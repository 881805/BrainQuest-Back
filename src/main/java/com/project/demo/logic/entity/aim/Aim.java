package com.project.demo.logic.entity.aim;

import jakarta.persistence.*;

@Table(name = "Aim")
@Entity
public class Aim {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Integer id;

    private String name;
    private String description;
    @Column(name = "is_active", nullable = false)
    private Boolean active = true;
    private int value;

    public Aim() {}

    public Aim(Integer id, String name, String description, Boolean active, int value) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.active = active;
        this.value = value;
    }

    @Override
    public String toString() {
        return "Aim {" +
                "id=" + id +
                ", name=" + name +
                ", description=" + description +
                ", active=" + active + '\'' +
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

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
