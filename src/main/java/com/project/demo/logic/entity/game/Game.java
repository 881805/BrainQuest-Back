package com.project.demo.logic.entity;

import com.project.demo.logic.entity.conversation.Conversation;
import com.project.demo.logic.entity.user.User;
import jakarta.persistence.*;

@Entity
@Table(name = "game")
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "conversation_id", referencedColumnName = "id")
    private Conversation conversation;

    @ManyToOne
    @JoinColumn(name = "winner", referencedColumnName = "userId")
    private User winner;

    private int pointsEarnedPlayer1;
    private int pointsEarnedPlayer2;

    public Game() {
    }

    public Game(Conversation conversation, User winner, int pointsEarnedPlayer1, int pointsEarnedPlayer2) {
        this.conversation = conversation;
        this.winner = winner;
        this.pointsEarnedPlayer1 = pointsEarnedPlayer1;
        this.pointsEarnedPlayer2 = pointsEarnedPlayer2;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Conversation getConversation() {
        return conversation;
    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }

    public User getWinner() {
        return winner;
    }

    public void setWinner(User winner) {
        this.winner = winner;
    }

    public int getPointsEarnedPlayer1() {
        return pointsEarnedPlayer1;
    }

    public void setPointsEarnedPlayer1(int pointsEarnedPlayer1) {
        this.pointsEarnedPlayer1 = pointsEarnedPlayer1;
    }

    public int getPointsEarnedPlayer2() {
        return pointsEarnedPlayer2;
    }

    public void setPointsEarnedPlayer2(int pointsEarnedPlayer2) {
        this.pointsEarnedPlayer2 = pointsEarnedPlayer2;
    }

    @Override
    public String toString() {
        return "Game{" +
                "id=" + id +
                ", conversation=" + conversation +
                ", winner=" + winner +
                ", pointsEarnedPlayer1=" + pointsEarnedPlayer1 +
                ", pointsEarnedPlayer2=" + pointsEarnedPlayer2 +
                '}';
    }
}
