package com.project.demo.logic.entity.game;

import com.project.demo.logic.entity.conversation.Conversation;
import com.project.demo.logic.entity.gameType.GameType;
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

    @ManyToOne
    @JoinColumn(name = "game_type", referencedColumnName = "id")
    private GameType gameType;


    private boolean isOngoing; //determina si el juego esta activo o no, un jugazdor no puede estar en varios juegos activos al mismo tiempo del mismo tipo

    private int pointsEarnedPlayer1;
    private int pointsEarnedPlayer2;

    public Game(Long id, Conversation conversation, User winner, GameType gameType, boolean isOngoing, int pointsEarnedPlayer1, int pointsEarnedPlayer2) {
        this.id = id;
        this.conversation = conversation;
        this.winner = winner;
        this.gameType = gameType;
        this.isOngoing = isOngoing;
        this.pointsEarnedPlayer1 = pointsEarnedPlayer1;
        this.pointsEarnedPlayer2 = pointsEarnedPlayer2;
    }

    public GameType getGameType() {
        return gameType;
    }

    public void setGameType(GameType gameType) {
        this.gameType = gameType;
    }

    public boolean isOngoing() {
        return isOngoing;
    }

    public void setOngoing(boolean ongoing) {
        isOngoing = ongoing;
    }

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
