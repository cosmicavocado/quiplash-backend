package com.game.witticism.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.game.witticism.custom.Response;
import com.game.witticism.exception.InformationExistsException;
import com.game.witticism.exception.InformationNotFoundException;
import com.game.witticism.model.Game;
import com.game.witticism.model.Player;
import com.game.witticism.model.Prompt;
import com.game.witticism.repository.GameRepository;
import com.game.witticism.repository.PlayerRepository;
import com.game.witticism.repository.PromptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.logging.Logger;

@Service
public class GameService {
    private static final Logger LOGGER = Logger.getLogger(GameService.class.getName());
    private GameRepository gameRepository;
    private PlayerRepository playerRepository;
    private PromptRepository promptRepository;
    private ArrayList<Prompt> drawn;
    private Game currentGame;
    private static final Random RNG = new Random();
    private String gameCode;

    @Autowired
    public void setGameRepository(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }
    @Autowired
    public void setPlayerRepository(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }
    @Autowired
    public void setPromptRepository(PromptRepository promptRepository) {
        this.promptRepository = promptRepository;
    }

    // CREATE GAME
    public Game createGame(String hostName, String code) {
        Game game = gameRepository.findByCode(code);
        // if game already exists
        if(game != null) {
            // check if game is active
            if(game.isActive()) {
                throw new InformationExistsException("The game with code " + code + " is already an active game.");
            }
        } else {
            // create new game and set fields
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
            // create new Player
            host = new Player(hostName);
            LOGGER.info("New player with name " + host.getName() + " created.");
        }
        // set player host to true
        host.setHost(true);
        // set host game to current game
        host.setGame(game);
        // save host to db
        playerRepository.save(host);
        LOGGER.info("Host with name " + host.getName() + " created a game with code " + game.getCode());
        return game;
    }

    // ADD PLAYER
    public void addPlayer(String playerName, String code) throws Exception{
        // check if game exists
        Game game = gameRepository.findByCode(code);
        if(game == null) {
            throw new InformationNotFoundException("Game with code " + code + " not found.");
        }
        // make sure max players have not been exceeded
        if(game.getPlayers().size() == 8) {
            throw new Exception("Must have 3-8 players to play.");
        }
        // check if player w/ name is already in game
        Player player = playerRepository.findByNameAndGameId(playerName, game.getId());
        if (player != null) {
            // throw player exists
            throw new InformationExistsException("Player with name " + playerName + " is already in this game.");
        } else {
            // look for player by name
            player = playerRepository.findByName(playerName);
            if(player != null) {
                // update player record in db
                player.setGame(game);
                player.setHost(false);
            } else {
                // add new player to the db
                player = new Player(playerName);
                player.setHost(false);
            }
        }
        // update players list
        game.setPlayers(game.getPlayers());
        player.setGame(game);
        // update models in db
        gameRepository.save(game);
        playerRepository.save(player);
        currentGame = game;
    }

    // START GAME
    public Game startGame(String code) throws Exception {
        Game game = gameRepository.findByCode(code);
        // if game doesn't exist
        if(game == null) {
            throw new InformationNotFoundException("Game with code " + code + " not found.");
        }
        // if less than min players
        if(game.getPlayers().size() < 3) {
            throw new Exception("Must have 3-8 players to play.");
        }
        // start game
        game.setRound(1);
        // get prompts
//        prompts = (ArrayList<Prompt>) promptRepository.findAll();
        // save game
        gameRepository.save(game);
        currentGame = game;
        return game;
    }

    public List<Player> getPlayers(Long gameId) {
        List<Player> players = playerRepository.findByGameId(gameId);
        for(Player player : players){
            System.out.println(player.getName());
        }
        return players;
    }

    // GET GAME
    public Game getGame(String code) {
        return gameRepository.findByCode(code);
    }

    // DRAW PROMPTS (filter for discards later)
    public String getPrompts(String code) throws JsonProcessingException {
        // get current game
        Game game = gameRepository.findByCode(code);
        // get current players
        List<Player> players = game.getPlayers();
        // num of prompts
        long deckSize = promptRepository.count();
        // temp list to keep track of cards this round
        List<Prompt> tmpList = new ArrayList<>(players.size());
        // for each player
        players.forEach(player -> {
            // get random num
            int rng = RNG.nextInt((int)deckSize);
            // get random prompt
            Optional<Prompt> randPrompt = promptRepository.findById((long) rng);
            // temp store these prompts
            tmpList.add(randPrompt.get());
        });
        // instantiate Jackson Obj Mapper
        ObjectMapper mapper = new ObjectMapper();
        // map prompts to json string
        String json = mapper.writeValueAsString(tmpList);
        System.out.println(json);
//        return mapper.writeValueAsString(tmpList);
        return json;
    }

    // UPDATE DISCARDS

    // GET RESPONSES
    public Response getResponse(Response response) {
        return response;
    }

    // VOTE

    // GET SCORES

    // END GAME
}