package com.game.witticism.custom;

public class Vote {
    private Response response;
    private Long playerId;

    public Vote() {
    }

    public Vote(Response response, Long playerId) {
        this.response = response;
        this.playerId = playerId;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }
}
