package com.game.witticism.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.game.witticism.custom.Response;
import com.game.witticism.model.Game;
import com.game.witticism.model.Player;
import com.game.witticism.model.Prompt;
import com.game.witticism.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    // get players
    @GetMapping(path="/game/{gameId}/players")
    public List<Player> getPlayers(@PathVariable Long gameId) {
        LOGGER.info("Calling getPlayers from game controller.");
        return gameService.getPlayers(gameId);
    }

    // get prompts
    @GetMapping(path="/game/{code}/draw")
    public Prompt getPrompt(@PathVariable String code) {
        LOGGER.info("Calling getPrompts from gameController");
        return gameService.getPrompt(code);
    }

    // submit response
    @PostMapping("/game/response")
    public String sendResponse(@RequestBody Response response) throws JsonProcessingException {
        LOGGER.info("Calling sendResponse from gameController");
        return gameService.sendResponse(response);
    }
}
