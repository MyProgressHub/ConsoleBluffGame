package main.dto;

public class Card {
    private final CardValue cardValue;

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Card)) {
            return false;
        }
        Card card = (Card) obj;
        return card.getCardValue() == this.getCardValue() && card.getCardValue().equals(this.getCardValue());
    }

    public Card(CardValue status) {
        this.cardValue = status;
    }

    public CardValue getCardValue() {
        return cardValue;
    }

    public int getCardStrength() {
        return cardValue.getStrength();
    }

    @Override
    public String toString() {
        return cardValue + ", ";
    }
}
