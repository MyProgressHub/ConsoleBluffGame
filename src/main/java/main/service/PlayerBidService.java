package main.service;

import main.dto.Card;
import main.dto.CardValue;
import main.dto.CardsSets;

import java.util.ArrayList;
import java.util.List;

import static main.dto.CardsSets.FOUR_OF_A_KIND;
import static main.dto.CardsSets.TWO_PAIRS;

public class PlayerBidService {
    private static final int FOUR_CARDS = 4;
    private static final int FIRST_CARD_IN_BID = 0;
    private static final int FOURTH_CARD_IN_BID = 3;
    private static final int MAXIMUM_UNIQUE_CARDS_IN_BID = 2;
    private static final int MINIMUM_UNIQUE_CARDS_IN_BID = 1;

    private CardsSets cardsSets;
    private Card firstUniqueCardFromBid;
    private Card secondUniqueCardFromBid;

    PlayerBidService(String bidCurrentlyPlayer) {
        setCardsSetsFromBid(bidCurrentlyPlayer);
        setCardsFromStringBid(bidCurrentlyPlayer);
    }

    CardsSets getCardsSets() {
        return cardsSets;
    }

    Card getFirstUniqueCard() {
        return firstUniqueCardFromBid;
    }

    Card getSecondUniqueCardFromBid() {
        return secondUniqueCardFromBid;
    }

    private void setCardsSetsFromBid(String bidCurrentlyPlayer) {
        for (CardsSets strength : CardsSets.values()) {
            if (bidCurrentlyPlayer.length() == FOUR_CARDS) {
                setCardsSetsFromFourCardsBid(bidCurrentlyPlayer);
                break;
            } else if (bidCurrentlyPlayer.length() == strength.getAmountOfCard()) {
                cardsSets = strength;
                break;
            }
        }
    }

    private void setCardsSetsFromFourCardsBid(String bidCurrentlyPlayer) {
        char firstCharInBid = bidCurrentlyPlayer.charAt(FIRST_CARD_IN_BID);
        if (bidCurrentlyPlayer.charAt(FOURTH_CARD_IN_BID) == firstCharInBid) {
            cardsSets = FOUR_OF_A_KIND;
        } else {
            cardsSets = TWO_PAIRS;
        }
    }

    private void setCardsFromStringBid(String bidCurrentlyPlayer) {
        switch (cardsSets) {
            case HIGH_CARD, PAIR, THREE_OF_A_KIND, FOUR_OF_A_KIND -> setUniqueCardsFromBid(bidCurrentlyPlayer, MINIMUM_UNIQUE_CARDS_IN_BID);
            case FULL_HOUSE, TWO_PAIRS -> setUniqueCardsFromBid(bidCurrentlyPlayer, MAXIMUM_UNIQUE_CARDS_IN_BID);
        }
    }

    private void setUniqueCardsFromBid(String bidCurrentlyPlayer, int uniqueCardAmountInBid) {
        firstUniqueCardFromBid = new Card(returnCardValueFromBid(bidCurrentlyPlayer.charAt(FIRST_CARD_IN_BID)));
        if (uniqueCardAmountInBid == MAXIMUM_UNIQUE_CARDS_IN_BID) {
            secondUniqueCardFromBid = new Card(returnCardValueFromBid(bidCurrentlyPlayer.charAt(FOURTH_CARD_IN_BID)));
        } else {
            secondUniqueCardFromBid = null;
        }
    }

    private CardValue returnCardValueFromBid(char singleCharFromBet) {
        for (CardValue cardValue : CardValue.values()) {
            if (singleCharFromBet == cardValue.getCharCard()) {
                return cardValue;
            }
        }
        throw new NullPointerException("Null char in player Bet ");
    }


    public List<Card> getCards() {
        List<Card> cardsInBid = null;
        switch (cardsSets) {
            case HIGH_CARD, PAIR, THREE_OF_A_KIND, FOUR_OF_A_KIND -> cardsInBid = getCardsForOneUniqueCardInBid();
            case TWO_PAIRS -> cardsInBid = getCardsForTwoPairs();
            case FULL_HOUSE -> cardsInBid = getCardsForFullHouse();
        }
        return cardsInBid;
    }

    private List<Card> getCardsForOneUniqueCardInBid() {
        List<Card> cardsInBid = new ArrayList<>();
        for (int addedCard = 0; addedCard < cardsSets.getAmountOfCard(); addedCard++) {
            cardsInBid.add(firstUniqueCardFromBid);
        }
        return cardsInBid;
    }

    private List<Card> getCardsForTwoPairs() {
        List<Card> cardsInBid = new ArrayList<>();
        for (int addedCard = 0; addedCard < MAXIMUM_UNIQUE_CARDS_IN_BID; addedCard++) {
            cardsInBid.add(firstUniqueCardFromBid);
            cardsInBid.add(secondUniqueCardFromBid);
        }
        return cardsInBid;
    }

    private List<Card> getCardsForFullHouse() {
        List<Card> cardsInBid = new ArrayList<>();
        cardsInBid.add(firstUniqueCardFromBid);
        for (int addedCard = 0; addedCard < MAXIMUM_UNIQUE_CARDS_IN_BID; addedCard++) {
            cardsInBid.add(firstUniqueCardFromBid);
            cardsInBid.add(secondUniqueCardFromBid);
        }
        return cardsInBid;
    }
}
