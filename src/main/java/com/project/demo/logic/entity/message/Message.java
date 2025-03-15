package com.project.demo.logic.entity.message;


import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name= "message")
public class Message {
private String conversationId;

@Column(name = "content_text", length = 1000)
private String contentText;

//anhade create date a los metodos
    @Column(name = "create_date", nullable = false, updatable = false)
    private LocalDateTime createDate;

    @PrePersist
    public void prePersist() {
        this.createDate = LocalDateTime.now();
    }
private Long sendingUserId;
private boolean isSent;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Message(String conversationId, String contentText, LocalDateTime createDate, Long sendingUserId, boolean isSent) {
    this.conversationId = conversationId;

    this.contentText = contentText;
    this.createDate = createDate;
    this.sendingUserId = sendingUserId;
    this.isSent = isSent;
}

    public Message() {
    }

    public Message(String conversationId, Long id, boolean isSent, Long sendingUserId, String contentText, LocalDateTime createDate) {
        this.conversationId = conversationId;
        this.id = id;
        this.isSent = isSent;
        this.sendingUserId = sendingUserId;
        this.contentText = contentText;
        this.createDate = createDate;
    }

    public String getConversationId() {
    return conversationId;
}

public void setConversationId(String conversationId) {
    this.conversationId = conversationId;
}

public String getContentText() {
    return contentText;
}

public void setContentText(String contentText) {
    this.contentText = contentText;
}

public LocalDateTime getCreateDate() {
    return createDate;
}

public void setCreateDate(LocalDateTime createDate) {
    this.createDate = createDate;
}

public Long getSendingUserId() {
    return sendingUserId;
}

public void setSendingUserId(Long sendingUserId) {
    this.sendingUserId = sendingUserId;
}

public boolean isSent() {
    return isSent;
}

public void setSent(boolean sent) {
    isSent = sent;
}

@Override
public String toString() {
    return "Message{" +
            "conversationId='" + conversationId + '\'' +
            ", contentText='" + contentText + '\'' +
            ", createDate=" + createDate +
            ", sendingUserId='" + sendingUserId + '\'' +
            ", isSent=" + isSent +
            '}';
}

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}

