package com.game.witticism.service;

import com.game.witticism.model.Game;
import com.game.witticism.model.Player;
import com.game.witticism.repository.GameRepository;
import com.game.witticism.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.logging.Logger;

@Service
public class GameService {
    private static final Logger LOGGER = Logger.getLogger(GameService.class.getName());
    private GameRepository gameRepository;
    private PlayerRepository playerRepository;

    @Autowired
    public void setGameRepository(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @Autowired
    public void setPlayerRepository(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    // create game
    public void createGame(String hostName, String code) {
        // create empty array list to hold all players
        ArrayList<Player> players = new ArrayList<>();
        // create host Player object and save to DB
        Player host = new Player(hostName);
        host.setHost(true);
        playerRepository.save(host);
        // add host to players list
        players.add(host);
        // create new game with code and host
        Game game = new Game(code, players);
        // save Game to DB
        gameRepository.save(game);
    }
}
