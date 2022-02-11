package com.game.witticism.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.game.witticism.custom.Response;
import com.game.witticism.custom.Vote;
import com.game.witticism.model.Game;
import com.game.witticism.model.Player;
import com.game.witticism.model.Prompt;
import com.game.witticism.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@CrossOrigin(origins="http://localhost:4200")
@RestController
@RequestMapping("/api")
public class GameController {
    private static final Logger LOGGER = Logger.getLogger(GameController.class.getName());
    private GameService gameService;

    @Autowired
    public void setGameService(GameService gameService) { this.gameService = gameService; }

    // create game
    @PostMapping(path="/game/{code}/host/{hostName}")
    public Game createGame(@PathVariable String hostName, @PathVariable String code) {
        LOGGER.info("Calling createGame from game controller.");
        return gameService.createGame(hostName,code);
    }

    // add player
    @PostMapping(path="/game/{code}/join/{playerName}")
    public void addPlayer(@PathVariable String playerName, @PathVariable String code) throws Exception {
        LOGGER.info("Calling addPlayer from game controller.");
        gameService.addPlayer(playerName,code);
    }

    // start game
    @GetMapping(path="/game/{code}/start")
    public Game startGame(@PathVariable String code) throws Exception {
        LOGGER.info("Calling startGame from game controller.");
        return gameService.startGame(code);
    }

    // get game
    @GetMapping(path="/game/{code}")
    public Game getGame(@PathVariable String code) {
        return gameService.getGame(code);
    }

    // get player
    @GetMapping(path="/game/{gameId}/player/{name}")
    public Player getPlayer(@PathVariable String name, @PathVariable Long gameId) {
        return gameService.getPlayer(name,gameId);
    }

    // get players
    @GetMapping(path="/game/{gameId}/players")
    public List<Player> getPlayers(@PathVariable Long gameId) {
        LOGGER.info("Calling getPlayers from game controller.");
        return gameService.getPlayers(gameId);
    }

    // get prompts
    @GetMapping(path="/game/{code}/draw")
    public Prompt getPrompt(@PathVariable String code) throws JsonProcessingException {
        LOGGER.info("Calling getPrompts from gameController");
        return gameService.getPrompt(code);
    }

    // submit response
    @PostMapping("/game/response")
    public String sendResponse(@RequestBody Response response) throws JsonProcessingException {
        LOGGER.info("Calling sendResponse from game controller");
        return gameService.sendResponse(response);
    }

    @GetMapping(path="/game/{code}/update")
    public Game checkGame(@PathVariable String code) throws JsonProcessingException {
        LOGGER.info("Calling updateGame from game controller.");
        return gameService.checkGame(code);
    }

    @GetMapping(path="/game/{code}/responses/{promptId}")
    public ArrayList<Response> getResponses(@PathVariable String code, @PathVariable Long promptId) {
        LOGGER.info("Calling getResponses from game controller.");
        return gameService.getResponses(code, promptId);
    }

    @PostMapping(path="/game/{code}/vote/{playerId}")
    public Vote sendVote(@PathVariable String code, @PathVariable Long playerId, @RequestBody Response response) throws JsonProcessingException {
        LOGGER.info("Calling sendVote from game controller.");
        return gameService.sendVote(code,playerId,response);
    }
}
