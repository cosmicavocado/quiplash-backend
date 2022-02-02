package com.game.witticism.controller;

import com.game.witticism.model.Player;
import com.game.witticism.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins="http://localhost:4200")
public class GameController {
    private static final Logger LOGGER = Logger.getLogger(GameController.class.getName());
    private GameService gameService;

    @Autowired
    public void setGameService(GameService gameService) { this.gameService = gameService; }

    // create game
    @PostMapping(path="/game/{code}/host/{hostName}")
    public void createGame(@PathVariable String hostName, @PathVariable String code) {
        LOGGER.info("Calling createGame from game controller.");
        gameService.createGame(hostName,code);
    }

    // add player
    @PostMapping(path="/game/{code}/join/{playerName}")
    public void addPlayer(@PathVariable String playerName, @PathVariable String code) throws Exception {
        LOGGER.info("Calling addPlayer from game controller.");
        gameService.addPlayer(playerName,code);
    }

    // start game
    @GetMapping(path="/game/start/{code}")
    public void startGame(@PathVariable String code) throws Exception {
        LOGGER.info("Calling startGame from game controller.");
        gameService.startGame(code);
    }

    // get players
    @GetMapping(path="/game/{gameId}/players")
    public List<Player> getPlayers(@PathVariable Long gameId) {
        LOGGER.info("Calling getPlayers from game controller.");
        return gameService.getPlayers(gameId);
    }
}
