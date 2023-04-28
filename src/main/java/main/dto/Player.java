package main.dto;

import main.service.PlayerBidService;

import java.util.LinkedList;
import java.util.List;

public class Player {
    private final List<Card> cardsInHand;
    private PlayerBidService playerBid;
    private final int id;
    private boolean isAlive;
    private int slotsCardNumber = 1;

    public Player(int id) {
        this.id = id;
        this.cardsInHand = new LinkedList<>();
        this.isAlive = true;
    }

    public List<Card> getCardsInHand() {
        return cardsInHand;
    }

    public void addCardToHand(Card card) {
        cardsInHand.add(card);
    }

    public int getCardsNumberInHand() {
        return cardsInHand.size();
    }

    public void clearTheHand() {
        cardsInHand.clear();
    }

    public PlayerBidService getPlayersBid() {
        return playerBid;
    }

    public void setPlayerBid(PlayerBidService playerBidService) {
        this.playerBid = playerBidService;
    }

    public void clearBid() {
        playerBid = null;
    }

    public int getId() {
        return id;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public int getSlotsCardNumber() {
        return slotsCardNumber;
    }

    public boolean increaseCardsSlot() {
        slotsCardNumber++;
        if (slotsCardNumber > 5) {
            isAlive = false;
        }
        return isAlive;
    }
}
