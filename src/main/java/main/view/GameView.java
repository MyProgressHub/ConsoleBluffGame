package main.view;

import main.dto.Player;

import java.util.List;
import java.util.Scanner;

public class GameView {

    private static final int MINIMUM_NUMBER_OF_PLAYERS = 2;
    private static final int MAXIMUM_NUMBER_OF_PLAYERS = 5;

    private final Scanner scan = new Scanner(System.in);

    public int getNumberOfPlayers() {
        System.out.println("Choose number of players: 2-5");
        int numberOfPlayers = 0;
        do {
            if (scan.hasNextInt()) {
                numberOfPlayers = Integer.parseInt(scan.nextLine());
            } else {
                scan.nextLine();
                System.out.println("Invalid input, please enter an value 2-5");
            }
        } while (numberOfPlayers < MINIMUM_NUMBER_OF_PLAYERS || numberOfPlayers > MAXIMUM_NUMBER_OF_PLAYERS);
        return numberOfPlayers;
    }

    public void showPlayerHand(Player player) {
        System.out.println("Player: " + player.getId() + " turn, hand: ");
        System.out.print("---------------> ");
        player.getCardsInHand().forEach(System.out::print);
        System.out.println();
    }

    public void showPossibilityCardsSets() {
        System.out.println("We play with 24 cards, (N/T/J/Q/K/A) x 4  (pool of 6 value cards)");
        System.out.println("High Card e.g.'K'");
        System.out.println("Pair of Cards e.g.'JJ'");
        System.out.println("Two Pair of Cards e.g.'JJQQ'");
        System.out.println("Three of Cards e.g.'TTT'");
        System.out.println("Full, 3 + 2 e.g.'JJJQQ'");
        System.out.println("Four Of Kind e.g.'NNNN' \n");
    }

    public void showInformationAboutBid() {
        System.out.println("Make a bid");
        System.out.println("\n9-(N) \n10-(T) \nJack-(J) \nQueen-(Q) \nKing-(K) \nAs-(A) \n");
        System.out.println("e.g.  JJQQ - (JACK JACK QUEEN QUEEN)");
    }

    public void showPossibilityMoves() {
        System.out.println("You wanna bid or check previous player?");
        System.out.println("'1' for Bid");
        System.out.println("'2' for Check previous Player");
        System.out.println("'3' For help regarding to sets of Cards");
    }


    public int takePlayerMove() {
        int number = 0;
        do {
            if (number < 0)
                System.out.println("Please enter a positive number!");
            while (!scan.hasNextInt()) {
                System.out.println("That's not a number!");
                scan.nextLine();
            }
            number = Integer.parseInt(scan.nextLine());
        } while (number <= 0);
        return number;
    }

    public void showPreviousBid(Player player) {
        System.out.println("Previous bet was: ");
        player.getPlayersBid().getCards().forEach(System.out::print);
        System.out.println("\nMake a bet");
    }

    public void showEndGameInfo(int idWinningPlayer) {
        System.out.println("End the Game, Player win id: " + idWinningPlayer);
    }

    public void showIncreaseSlotsNumberMessage(int idPlayerToIncreaseSizeHand) {
        System.out.println("Bad Move for Player " + idPlayerToIncreaseSizeHand + ", Increase number of Slots Cards in hand");
    }

    public String getPlayerBid() {
        return scan.nextLine();
    }

    public void showInfoAboutIncorrectBid() {
        System.out.println("Wrong Bid, make it again");
    }

    public void previousPlayerDidNotBidYet() {
        System.out.println("Previous player didn't bid yet, make a bid");
    }

    public void showInfoAboutPreviousHighestBid() {
        System.out.println("Previous bid was the highest possible so now you have to check the previous Player");
    }

    public void showNewRound(List<Player> players) {
        System.out.println("\n\nNEW ROUND");
        players.forEach(player -> {
            System.out.println("Player AmountOfCards");
            System.out.print(player.getId() + "           " + player.getSlotsCardNumber());
            if (!player.isAlive()) {
                System.out.print("     DEAD");
            }
            System.out.println("\n");
        });
    }

    public void printInfoAboutPlayerLoose() {
        System.out.println("This player loose the game, because the slots card on hand is bigger than 5");
    }
}
