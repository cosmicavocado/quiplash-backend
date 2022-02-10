package com.game.witticism.model;

import javax.persistence.*;

@Entity
@Table(name = "player")
public class Player {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private boolean host;

    @Column(columnDefinition="TEXT")
    private String responses;

    @ManyToOne
    @JoinColumn(name="game_id")
    private Game game;

    public Player() {
    }

    public Player(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getHost() {
        return host;
    }

    public void setHost(boolean host) {
        this.host = host;
    }

    public String getResponses() {
        return responses;
    }

    public void setResponses(String responses) {
        this.responses = responses;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }
}
