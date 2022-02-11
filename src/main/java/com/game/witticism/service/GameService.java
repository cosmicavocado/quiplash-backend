package com.game.witticism.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.game.witticism.custom.Response;
import com.game.witticism.custom.Vote;
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

import java.util.*;
import java.util.logging.Logger;

@Service
public class GameService {
    private static final Logger LOGGER = Logger.getLogger(GameService.class.getName());
    private GameRepository gameRepository;
    private PlayerRepository playerRepository;
    private PromptRepository promptRepository;
    private ArrayList<Prompt> deck;
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
            // create new game and initialize fields
            game = new Game(code);
            game.setActive(true);
            game.setRound(0);
            game.setResponseCount(0);
            game.setVoteCount(0);
            game.setPrompts("");
            game.setCurrPrompt("");
            game.setVotes("");
            game.setStage("join");

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
        host.setResponses("");
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
            } else {
                // add new player to the db
                player = new Player(playerName);
            }
            player.setHost(false);
            player.setResponses("");
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
        // set stage
        game.setStage("response");
        // deck
        deck = (ArrayList<Prompt>) promptRepository.findAll();
        // list to hold drawn prompts
        ArrayList<Prompt> drawnPrompts = new ArrayList<>();
        // loop drawing from deck
        for(int i=0; i<3; i++) {
            int n = RNG.nextInt(deck.size());
            Prompt prompt = deck.get(n);
            drawnPrompts.add(prompt);
            deck.remove(n);
        }
        Prompt currPrompt = drawnPrompts.get(0);

        // mapper
        ObjectMapper mapper = new ObjectMapper();
        // write all prompts as string
        String promptsStr = mapper.writeValueAsString(drawnPrompts);
        game.setPrompts(promptsStr);
        // write current prompt as string
        String currPromptStr = mapper.writeValueAsString(currPrompt);
        game.setCurrPrompt(currPromptStr);
        // save game
        gameRepository.save(game);
        return game;
    }

    // GET PLAYER
    public Player getPlayer(String name, Long gameId) {
        return playerRepository.findByNameAndGameId(name,gameId);
    }

    // GET LIST OF PLAYERS
    public List<Player> getPlayers(Long gameId) {
        return playerRepository.findByGameId(gameId);
    }

    // GET GAME
    public Game getGame(String code) {
        LOGGER.info("Calling getGame from controller.");
        return gameRepository.findByCode(code);
    }

    // DRAW PROMPT
    public Prompt getPrompt(String code) throws JsonProcessingException {
        // get current game
        Game game = gameRepository.findByCode(code);
        // get string to map
        String currPromptStr = game.getCurrPrompt();
        // mapper
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(currPromptStr, new TypeReference<>(){});
    }

    // SEND RESPONSE
    public String sendResponse(Response response) throws JsonProcessingException {
        LOGGER.info("Calling send response");
        // get player
        Long playerId = response.getPlayerId();
        Player player = playerRepository.getById(playerId);
        // get game
        Game game = player.getGame();
        // mapper
        ObjectMapper mapper = new ObjectMapper();
        // list
        List<Response> resp = new ArrayList<>();
        // get responses
        String responses = player.getResponses();
        // check responses length
        if(!responses.equals("")) {
            // read responses String into Response list
            resp = mapper.readValue(responses, new TypeReference<>(){});
        }
        // add new response
        resp.add(response);
        // convert response to string
        String jsonResp = mapper.writeValueAsString(resp);
        // update responses in player
        player.setResponses(jsonResp);
        // count responses
        int count = game.getResponseCount();
        game.setResponseCount(count+1);
        // save updates
        playerRepository.save(player);
        gameRepository.save(game);
        // return resp string
        return jsonResp;
    }

    // UPDATE GAME
    public Game checkGame(String code) throws JsonProcessingException {
        Game game = gameRepository.findByCode(code);
        int numResponses = game.getResponseCount();
        int currRound = game.getRound();
        int numPlayers = game.getPlayers().size();

        // mapper
        ObjectMapper mapper = new ObjectMapper();

        // if all players have responded
        if (numResponses == numPlayers * currRound) {
            // update stage
            game.setStage("vote");
        }

        // if all players have voted
        if (game.getStage().equals("vote") && game.getVoteCount() == numPlayers * currRound) {
            game.setStage("score");
        }

        // scoring / set up next round
        if (game.getStage().equals("score")) {
            // list to hold prompts
            ArrayList<Prompt> prompts;
            // get prompts as string
            String promptsStr = game.getPrompts();
            // read string into list
            prompts = mapper.readValue(promptsStr, new TypeReference<>(){});
            // pull another prompt
            Prompt prompt = prompts.get(currRound-1);
            // read into string
            String promptStr = mapper.writeValueAsString(prompt);
            // update current prompt
            game.setCurrPrompt(promptStr);

            // update round
            game.setRound(currRound+1);
        }
        // save changes
        gameRepository.save(game);
        return game;
    }

    // GET ALL RESPONSES
    public ArrayList<Response> getResponses(String code, Long promptId) {
        ObjectMapper mapper = new ObjectMapper();
        Game game = gameRepository.findByCode(code);
        List<Player> players = game.getPlayers();
        ArrayList<Response> temp = new ArrayList<>();
        players.forEach(player -> {
            try {
                ArrayList<Response>responses = mapper.readValue(player.getResponses(), new TypeReference<>(){});
                responses.stream().filter(resp -> (resp.getPromptId().equals(promptId))).forEach(temp::add);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });
        return temp;
    }

    // VOTE
    public Vote sendVote(String code, Long playerId, Response response) throws JsonProcessingException {
        // get game
        Game game = gameRepository.findByCode(code);
        // create vote
        Vote vote = new Vote(response, playerId);
        // get votes string
        String voteStr = game.getVotes();
        // define mapper
        ObjectMapper mapper = new ObjectMapper();
        // list to hold votes
        ArrayList<Vote> votesList = new ArrayList<>();
        if (!voteStr.equals("")) {
            votesList = mapper.readValue(voteStr, new TypeReference<>(){});
        }
        // add new vote
        votesList.add(vote);
        // back to string
        voteStr = mapper.writeValueAsString(votesList);
        // update game
        game.setVotes(voteStr);
        game.setVoteCount(game.getVoteCount()+1);
        gameRepository.save(game);
        // update player score
        Long winner = response.getPlayerId();
        Player player = playerRepository.getById(winner);
        player.setScore(player.getScore() + 10);
        playerRepository.save(player);
        return vote;
    }

    // GET SCORES

    // END GAME
}