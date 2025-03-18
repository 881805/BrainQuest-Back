package com.project.demo.logic.entity;

import com.project.demo.logic.entity.user.User;
import jakarta.persistence.*;

@Entity
@Table(name = "post")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;  // The content of the post (e.g., achievement text, etc.)
    private String imageUrl; // Optional image URL for the post (if there is an image associated)
    private String createdAt; // Timestamp or date the post was created

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "userId")
    private User user;  // The player who created the post

    public Post() {
    }

    public Post(String content, String imageUrl, String createdAt, User user) {
        this.content = content;
        this.imageUrl = imageUrl;
        this.createdAt = createdAt;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", user=" + user +
                '}';
    }
}
