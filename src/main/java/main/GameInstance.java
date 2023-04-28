package main;

import main.dto.Card;
import main.dto.CardValue;
import main.dto.Player;
import main.service.GameService;
import main.view.GameView;

import java.util.LinkedList;
import java.util.List;

public class GameInstance {
    private final List<Player> players = new LinkedList<>();
    private final List<Card> deckOfCards = new LinkedList<>();
    private final GameView gameView;

    GameInstance() {
        gameView = new GameView();
    }

    void startGame() {
        initializeGame();
        runGame();
    }

    private void initializeGame() {
        createDeck();
        createPlayers();
    }

    private void createDeck() {
        for (int numberOfCardDuplicates = 0; numberOfCardDuplicates < 4; numberOfCardDuplicates++) {
            for (CardValue cardValue : CardValue.values()) {
                deckOfCards.add(new Card(cardValue));
            }
        }
    }

    private void createPlayers() {
        int numberOfPlayers = gameView.getNumberOfPlayers();
        for (int createdPlayers = 0; createdPlayers < numberOfPlayers; createdPlayers++) {
            players.add(new Player(createdPlayers));
        }
    }

    private void runGame() {
        GameService gameService = new GameService(deckOfCards, players);
        gameService.startGame();
    }
}
