package com.game.witticism.controller;

import com.game.witticism.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@RestController
@RequestMapping("/api")
public class GameController {
    private static final Logger LOGGER = Logger.getLogger(GameController.class.getName());
    private GameService gameService;

    @Autowired
    public void setGameService(GameService gameService) { this.gameService = gameService; }

    // create game
    @PostMapping(path="/host/{hostName}/{code}")
    public void createGame(@PathVariable String hostName, @PathVariable String code) {
        LOGGER.info("Calling createGame from game controller.");
        gameService.createGame(hostName,code);
    }
}
