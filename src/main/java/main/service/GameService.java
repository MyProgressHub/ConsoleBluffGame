package main.service;

import main.dto.Card;
import main.dto.Player;
import main.view.GameView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import static main.dto.CardValue.AS;
import static main.dto.CardsSets.FOUR_OF_A_KIND;

public class GameService {

    private static final int MAKE_BID = 1;
    private static final int CHECK_PREVIOUS_BID = 2;
    private static final int SHOW_POSSIBILITY_HAND_STRENGTH = 3;

    private final List<Player> players;
    private final List<Card> deckOfCards;
    private final GameView gameView = new GameView();

    private boolean isFirstBidInTurn;
    private boolean isPreviousBidWasHighestPossible;
    private int idPlayerWhichStartRound = 0;

    public GameService(List<Card> deckOfCards, List<Player> players) {
        this.deckOfCards = deckOfCards;
        this.players = players;
    }

    public void startGame() {
        do {
            prepareNextRound();
            doGameRound();
        } while (getNumberAlivePlayers() > 1);
        int wonPlayerId = getAlivePlayers().get(0).getId();
        gameView.showEndGameInfo(wonPlayerId);
    }

    private void prepareNextRound() {
        players.forEach(Player::clearBid);
        isFirstBidInTurn = true;
        isPreviousBidWasHighestPossible = false;
        Collections.shuffle(deckOfCards);
        removeCardsFromHands();
        dealCardsForEachPlayers();
        gameView.showNewRound(players);
    }

    private void removeCardsFromHands() {
        getAlivePlayers()
                .forEach(player -> {
                    if (!player.getCardsInHand().isEmpty()) {
                        player.clearTheHand();
                    }
                });
    }

    private void dealCardsForEachPlayers() {
        AtomicInteger nextCardToDraw = new AtomicInteger(0);
        do {
            getAlivePlayers().forEach(player -> {
                if (player.getCardsNumberInHand() < player.getSlotsCardNumber()) {
                    player.addCardToHand(deckOfCards.get(nextCardToDraw.getAndIncrement()));
                }
            });
        } while (nextCardToDraw.get() != getAmountOfCardToDeal());
    }

    private void doGameRound() {
        doTheRoundOfBids(idPlayerWhichStartRound);
        idPlayerWhichStartRound = getIdPlayerForNextRound(idPlayerWhichStartRound);
    }

    private void doTheRoundOfBids(int currentlyPlayerTurn) {
        makePlayerMove(currentlyPlayerTurn);
    }

    private void makePlayerMove(int currentlyPlayerTurn) {
        showHandForPlayer(currentlyPlayerTurn);
        int playerMove;

        do {
            playerMove = getPlayerMove();
            switch (playerMove) {
                case MAKE_BID -> {
                    biddingPlayerCard(currentlyPlayerTurn);
                    currentlyPlayerTurn = getIdPlayerForNextRound(currentlyPlayerTurn);
                    makePlayerMove(currentlyPlayerTurn);
                }
                case CHECK_PREVIOUS_BID -> checkPreviousPlayerBid(currentlyPlayerTurn);
                case SHOW_POSSIBILITY_HAND_STRENGTH -> gameView.showPossibilityCardsSets();
            }
        } while (!validatePlayerMove(playerMove));
    }

    private int getPlayerMove() {
        int playerMove;
        if (isFirstBidInTurn) {
            gameView.showInformationAboutBid();
            playerMove = MAKE_BID;
        } else if (isPreviousBidWasHighestPossible) {
            gameView.showInfoAboutPreviousHighestBid();
            playerMove = CHECK_PREVIOUS_BID;
        } else {
            gameView.showPossibilityMoves();
            playerMove = gameView.takePlayerMove();
        }
        return playerMove;
    }

    private void biddingPlayerCard(int actualPlayerTurn) {
        PlayerBidService playerBidService;
        String playerBid;
        boolean isBidCorrectly = true;
        boolean isBidBiggerThanPrevious;

        do {
            showPreviousBid(actualPlayerTurn);
            do {
                if (!isBidCorrectly) {
                    gameView.showInfoAboutIncorrectBid();
                }
                playerBid = gameView.getPlayerBid();
                String patternBidRegex = "[NTJQKA]|(N{2,3}|T{2,3}|J{2,3}|Q{2,3}|K{2,3}|A{2,3})?(N{2}|T{2}|J{2}|Q{2}|K{2}|A{2})?";
                Pattern pattern = Pattern.compile(patternBidRegex);
                isBidCorrectly = pattern.matcher(playerBid).matches();
            } while (!isBidCorrectly);

            playerBidService = new PlayerBidService(playerBid);

            if (!isFirstBidInTurn) {
                isBidBiggerThanPrevious = isBidBiggerThanPrevious(playerBidService, actualPlayerTurn);

            } else {
                isBidBiggerThanPrevious = true;
                isFirstBidInTurn = false;
            }

        } while (!isBidBiggerThanPrevious);
        getPlayerById(actualPlayerTurn).setPlayerBid(playerBidService);
        isPreviousBidWasHighestPossible = isBidHighestPossible(playerBidService);
    }

    private boolean isBidHighestPossible(PlayerBidService playerBidService) {
        return playerBidService.getCardsSets() == FOUR_OF_A_KIND && playerBidService.getFirstUniqueCard().getCardValue() == AS;
    }

    private boolean isBidBiggerThanPrevious(PlayerBidService playerBidService, int actualPlayerTurn) {
        Player previousPlayer = getPlayerById(getPreviousPlayerId(actualPlayerTurn));
        PlayerBidService previousPlayerBid = previousPlayer.getPlayersBid();
        if (playerBidService.getCardsSets().getStrength() > previousPlayerBid.getCardsSets().getStrength()) {
            return true;
        } else if (playerBidService.getCardsSets().getStrength() < previousPlayerBid.getCardsSets().getStrength()) {
            return false;
        } else {
            return isCurrentBidHigher(playerBidService, previousPlayerBid);
        }
    }

    private boolean isCurrentBidHigher(PlayerBidService currentlyBid, PlayerBidService previousBid) {
        boolean isHigher = false;
        int currentlyFirstCardValue = currentlyBid.getFirstUniqueCard().getCardStrength();
        int previousFirstCardValue = previousBid.getFirstUniqueCard().getCardStrength();
        int currentlySecondCardValue = 0;
        int previousSecondCardValue = 0;

        if (previousBid.getSecondUniqueCardFromBid() != null) { //TODO Optional
            currentlySecondCardValue = currentlyBid.getSecondUniqueCardFromBid().getCardStrength();
            previousSecondCardValue = previousBid.getSecondUniqueCardFromBid().getCardStrength();
        }

        if (currentlyFirstCardValue > previousFirstCardValue) {
            isHigher = true;
        } else if (currentlyFirstCardValue == previousFirstCardValue) {
            isHigher = currentlySecondCardValue > previousSecondCardValue;
        }
        return isHigher;
    }

    private void showPreviousBid(int actualPlayerTurn) {
        int previousPlayerId = getPreviousPlayerId(actualPlayerTurn);
        if (hasPlayerAlreadyBid(previousPlayerId)) {
            gameView.showPreviousBid(getPlayerById(previousPlayerId));
        } else {
            gameView.previousPlayerDidNotBidYet();
        }
    }

    private boolean hasPlayerAlreadyBid(int previousPlayerId) {
        return getPlayerById(previousPlayerId).getPlayersBid() != null;
    }

    private int getPreviousPlayerId(int actualPlayerId) {
        int previousPlayerId = actualPlayerId;
        do {
            previousPlayerId--;
            if (previousPlayerId < 0) {
                previousPlayerId = players.size() - 1;
            }
        } while (!players.get(previousPlayerId).isAlive());
        return previousPlayerId;
    }


    private void checkPreviousPlayerBid(int actualIdPlayer) {
        Player previousPlayer = getPlayerById(getPreviousPlayerId(actualIdPlayer));
        Player currentPlayer = getPlayerById(actualIdPlayer);
        PlayerBidService previousPlayerBidService = previousPlayer.getPlayersBid();
        List<Card> cardsPoolOnTable = new ArrayList<>();
        int idPlayerToIncreaseCardSlots;

        getAlivePlayers()
                .forEach(alivePlayer -> cardsPoolOnTable.addAll(alivePlayer.getCardsInHand()));

        boolean isPreviousBidOnBoard = checkIsPreviousBidOnBoard(previousPlayerBidService, cardsPoolOnTable);

        if (isPreviousBidOnBoard) {
            idPlayerToIncreaseCardSlots = currentPlayer.getId();
        } else {
            idPlayerToIncreaseCardSlots = previousPlayer.getId();
        }
        boolean isAlive = getPlayerById(idPlayerToIncreaseCardSlots).increaseCardsSlot();
        gameView.showIncreaseSlotsNumberMessage(idPlayerToIncreaseCardSlots);
        if (!isAlive) {
            gameView.printInfoAboutPlayerLoose();
        }
    }

    private static boolean checkIsPreviousBidOnBoard(PlayerBidService previousPlayerBidService, List<Card> cardsPoolOnTable) {
        List<Card> previousBid = previousPlayerBidService.getCards();
        List<Card> cardsPoolOnTableCopy = new ArrayList<>(cardsPoolOnTable);

        cardsPoolOnTableCopy.removeAll(previousBid);
        return cardsPoolOnTableCopy.size() == cardsPoolOnTable.size() - previousBid.size();
    }

    private Player getPlayerById(int whichPlayerShouldStart) {
        return players.get(whichPlayerShouldStart);
    }

    private void showHandForPlayer(int whichPlayerShouldStart) {
        gameView.showPlayerHand(getPlayerById(whichPlayerShouldStart));
    }

    private int getAmountOfCardToDeal() {
        return getAlivePlayers().stream().mapToInt(Player::getSlotsCardNumber).sum();
    }

    private List<Player> getAlivePlayers() {
        return players.stream()
                .filter(Player::isAlive)
                .toList();
    }

    private int getNumberAlivePlayers() {
        return getAlivePlayers().size();
    }

    private boolean validatePlayerMove(int playerChoose) {
        return (playerChoose == MAKE_BID || playerChoose == CHECK_PREVIOUS_BID);
    }

    private int getIdPlayerForNextRound(int idPlayerWhichStartRound) {
        do {
            idPlayerWhichStartRound = returnNextPlayerIdRegardingToPlayerAmount(idPlayerWhichStartRound);
        } while (!getPlayerById(idPlayerWhichStartRound).isAlive());
        return idPlayerWhichStartRound;
    }

    private int returnNextPlayerIdRegardingToPlayerAmount(int idPlayerWhichStartRound) {
        final int idCountedFromOne = idPlayerWhichStartRound + 1;

        return idCountedFromOne == players.size() ? 0 : idPlayerWhichStartRound + 1;
    }
}
