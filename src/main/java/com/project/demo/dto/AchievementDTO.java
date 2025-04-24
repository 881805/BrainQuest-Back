package com.project.demo.dto;

import com.project.demo.logic.entity.achievement.Achievement;

public class AchievementDTO {
    private Integer id;
    private String name;
    private String description;
    private long experience;
    private String createdByFullName;
    private String createdByRol;
    private String gameTypeName;
    private String aimName;

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

    public String getCreatedByFullName() {
        return createdByFullName;
    }

    public void setCreatedByFullName(String createdByFullName) {
        this.createdByFullName = createdByFullName;
    }

    public String getCreatedByRol() {
        return createdByRol;
    }

    public void setCreatedByRol(String createdByRol) {
        this.createdByRol = createdByRol;
    }

    public String getGameTypeName() {
        return gameTypeName;
    }

    public void setGameTypeName(String gameTypeName) {
        this.gameTypeName = gameTypeName;
    }

    public String getAimName() {
        return aimName;
    }

    public void setAimName(String aimName) {
        this.aimName = aimName;
    }


    public static AchievementDTO from(Achievement achievement) {
        AchievementDTO dto = new AchievementDTO();
        dto.setId(achievement.getId());
        dto.setName(achievement.getName());
        dto.setDescription(achievement.getDescription());
        dto.setExperience(achievement.getExperience());

        if(achievement.getCreatedBy() != null) {
            dto.setCreatedByFullName(achievement.getCreatedBy().getName() + " " + achievement.getCreatedBy().getLastname());


            if(achievement.getCreatedBy() != null){
                dto.setCreatedByRol(achievement.getCreatedBy().getName());
            }
        }

        if (achievement.getAim() != null) {
            dto.setAimName(achievement.getAim().getName());
        }

        return dto;
    }
}
