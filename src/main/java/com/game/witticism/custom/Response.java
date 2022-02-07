package com.game.witticism.custom;

public class Response {
    private Long promptId;
    private Long playerId;
    private String responseText;

    public Response() {
    }

    public Response(Long promptId, Long playerId, String responseText) {
        this.promptId = promptId;
        this.playerId = playerId;
        this.responseText = responseText;
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
}
