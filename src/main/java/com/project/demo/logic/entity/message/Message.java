package com.project.demo.logic.entity.message;


import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name= "message")
public class Message {
private String conversationId;
private String contentText;
private LocalDateTime createDate;
private String sendingUserId;
private boolean isSent;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Message(String conversationId, String contentText, LocalDateTime createDate, String sendingUserId, boolean isSent) {
    this.conversationId = conversationId;
    this.contentText = contentText;
    this.createDate = createDate;
    this.sendingUserId = sendingUserId;
    this.isSent = isSent;
}

    public Message() {
    }

    public Message(String conversationId, Long id, boolean isSent, String sendingUserId, String contentText, LocalDateTime createDate) {
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

public String getSendingUserId() {
    return sendingUserId;
}

public void setSendingUserId(String sendingUserId) {
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

