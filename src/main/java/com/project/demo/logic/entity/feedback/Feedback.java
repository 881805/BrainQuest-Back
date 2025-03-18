package com.project.demo.logic.entity.feedback;

import com.project.demo.logic.entity.user.User;
import jakarta.persistence.*;

@Entity
@Table(name = "feedback")
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String subject;  // The subject or title of the feedback
    private String description;  // Detailed description of the feedback
    private String status;  // Current status of the feedback (e.g., "open", "in progress", "closed")
    private String createdAt;  // Timestamp when the feedback was created
    private String updatedAt;  // Timestamp when the feedback was last updated
    private String priority;  // Priority level (e.g., "low", "medium", "high")

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "userId")
    private User user;  // The user who submitted the feedback

    @ManyToOne
    @JoinColumn(name = "assigned_to", referencedColumnName = "userId")
    private User assignedTo;  // The user assigned to handle the feedback (can be null)

    public Feedback() {
    }

    public Feedback(String subject, String description, String status, String createdAt, String updatedAt, String priority, User user, User assignedTo) {
        this.subject = subject;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.priority = priority;
        this.user = user;
        this.assignedTo = assignedTo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(User assignedTo) {
        this.assignedTo = assignedTo;
    }

    @Override
    public String toString() {
        return "Feedback{" +
                "id=" + id +
                ", subject='" + subject + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", priority='" + priority + '\'' +
                ", user=" + user +
                ", assignedTo=" + assignedTo +
                '}';
    }
}
