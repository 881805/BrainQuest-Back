package com.project.demo.logic.entity.message;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.demo.logic.entity.conversation.Conversation;
import com.project.demo.logic.entity.user.User;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name= "message")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;

    @Column(name = "content_text", length = 1000)
    private String contentText;

    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private Date createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "userId")
    private User user;

    private boolean isSent;

    public Message(Long id, Conversation conversation, String contentText, Date createdAt, User user, boolean isSent) {
        this.id = id;
        this.conversation = conversation;
        this.contentText = contentText;
        this.createdAt = createdAt;
        this.user = user;
        this.isSent = isSent;
    }

    public Message() {}

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }


    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", conversation=" + conversation +
                ", contentText='" + contentText + '\'' +
                ", createdAt=" + createdAt +
                ", user=" + user +
                ", isSent=" + isSent +
                '}';
    }

    public boolean getIsSent() {
        return isSent;
    }

    public void setIsSent(boolean sent) {
        isSent = sent;
    }

    public Conversation getConversation() { return conversation; }
    public void setConversation(Conversation conversation) { this.conversation = conversation; }
}
