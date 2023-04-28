package main.dto;

public enum CardValue {
    NINE(1, 'N'),
    TEN(2, 'T'),
    JACK(3, 'J'),
    QUEEN(4, 'Q'),
    KING(5, 'K'),
    AS(6, 'A');
    private final int strength;
    private final char charCard;

    CardValue(int strength, char charCard) {
        this.charCard = charCard;
        this.strength = strength;
    }

    public char getCharCard() {
        return charCard;
    }

    int getStrength() {
        return strength;
    }
}
