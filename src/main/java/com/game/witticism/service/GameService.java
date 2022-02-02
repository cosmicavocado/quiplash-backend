package com.game.witticism.service;

import com.game.witticism.exception.InformationExistsException;
import com.game.witticism.exception.InformationNotFoundException;
import com.game.witticism.model.Game;
import com.game.witticism.model.Player;
import com.game.witticism.repository.GameRepository;
import com.game.witticism.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
        Game game = gameRepository.findByCode(code);
        // if game already exists
        if(game != null) {
            // check if game is active
            if(game.isActive()) {
                throw new InformationExistsException("The game with code " + code + " is already an active game.");
            }
        } else {
            // create new game with given code
            game = new Game(code);
            game.setActive(true);
            game.setRound(0);
            // save Game to db
            gameRepository.save(game);
        }
        // check if host already exists in db
        Player host = playerRepository.findByName(hostName);
        // if host does not exist
        if(host == null) {
            // create new Player host
            host = new Player(hostName);
            LOGGER.info("New player with name " + host.getName() + " created.");
        }
        // set player host to true
        host.setHost(true);
        // set host game to current game
        host.setGame(game);
        // save host to db
        playerRepository.save(host);

        System.out.println("Host with name " + host.getName() + " created a game with code " + game.getCode());
    }

    // add player to game
    public void addPlayer(String playerName, String code) {
        // check if game exists
        Game game = gameRepository.findByCode(code);
        if(game == null) {
            throw new InformationNotFoundException("Game with code " + code + " does not exist.");
        }
        // check if player with name is already in game
        Player player = playerRepository.findByName(playerName);
        if (player != null) {
            // check if player with this name is in current game
            if(player.getGame().getId().equals(game.getId())) {
                // throw player exists
                throw new InformationExistsException("Player with name " + playerName + " is already in this game.");
            }
            else {
                // update player record in db
                player.setGame(game);
                player.setHost(false);
            }
        }
        else {
            // else add new player to the db
            player = new Player(playerName);
            player.setHost(false);
        }
        // save player to db
        playerRepository.save(player);
        // get player list from game
        List<Player> players = game.getPlayers();
        // add player to list
        players.add(player);
        // update game
        game.setPlayers(players);
        // save changes to db
        gameRepository.save(game);
    }

    // start game
    public void startGame(String code) {
        Game game = gameRepository.findByCode(code);
        if(game == null) {
            throw new InformationNotFoundException("Game with code " + code + " not found.");
        }
    }
}
