package main.dto;

public enum CardsSets {
    HIGH_CARD(1, 1),
    PAIR(2, 2),
    TWO_PAIRS(3, 4),
    THREE_OF_A_KIND(4, 3),
    FULL_HOUSE(5, 5),
    FOUR_OF_A_KIND(6, 4);

    CardsSets(int strength, int amountOfCard) {
        this.strength = strength;
        this.amountOfCard = amountOfCard;
    }

    private final int strength;

    private final int amountOfCard;

    public int getAmountOfCard() {
        return amountOfCard;
    }

    public int getStrength() {
        return strength;
    }
}
