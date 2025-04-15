package com.project.demo.logic.entity.Objective;

import jakarta.persistence.*;

@Table(name = "objective")
@Entity
public class Objective {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Integer id;

    private int ammountSuccesses;
    private int scoreCondition;

    private String objectiveText;

    public Objective() {
    }

    public Objective(Integer id, int ammountSuccesses, int scoreCondition, String objectiveText) {
        this.id = id;
        this.ammountSuccesses = ammountSuccesses;
        this.scoreCondition = scoreCondition;
        this.objectiveText = objectiveText;
    }

    @Override
    public String toString() {
        return "Objective{" +
                "id=" + id +
                ", ammountSuccesses=" + ammountSuccesses +
                ", scoreCondition=" + scoreCondition +
                ", objectiveText='" + objectiveText + '\'' +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getAmmountSuccesses() {
        return ammountSuccesses;
    }

    public void setAmmountSuccesses(int ammountSuccesses) {
        this.ammountSuccesses = ammountSuccesses;
    }

    public int getScoreCondition() {
        return scoreCondition;
    }

    public void setScoreCondition(int scoreCondition) {
        this.scoreCondition = scoreCondition;
    }

    public String getObjectiveText() {
        return objectiveText;
    }

    public void setObjectiveText(String objectiveText) {
        this.objectiveText = objectiveText;
    }
}
