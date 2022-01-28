package com.game.witticism.custom;

import java.util.List;

public class Response {
    private int round;
    private Long promptId;
    private Long playerId;
    private String responseText;
    private List<Long> votes;

    public Response() {
    }

    public Response(Long promptId, Long playerId, String responseText) {
        this.promptId = promptId;
        this.playerId = playerId;
        this.responseText = responseText;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public Long getPromptId() {
        return promptId;
    }

    public void setPromptId(Long promptId) {
        this.promptId = promptId;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public String getResponseText() {
        return responseText;
    }

    public void setResponseText(String responseText) {
        this.responseText = responseText;
    }

    public List<Long> getVotes() {
        return votes;
    }

    public void setVotes(List<Long> votes) {
        this.votes = votes;
    }
}
