package com.codingame.game.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.codingame.game.engine.Constants.INITIAL_HAND_SIZE;

/**
 * Created by aCat on 2018-03-24.
 */
public class Gamer {
    public final int id;
    public int bonusManaTurns;
    public ArrayList<Card> hand;
    public ArrayList<Card> deck;
    public ArrayList<CreatureOnBoard> board;
    public ArrayList<CreatureOnBoard> graveyard;
    public int health;
    public int maxMana;
    public int currentMana;
    public int nextTurnDraw;
    public int drawValueToShow;
    public int healthLostThisTurn;

    public ArrayList<Action> performedActions;
    public final int handLimit;

    // todo rest

    public Gamer(int id, List<Card> deck) {
        this.id = id;
        this.hand = new ArrayList<>();
        this.deck = new ArrayList<>(deck);
        this.board = new ArrayList<>();
        this.graveyard = new ArrayList<>();
        this.performedActions = new ArrayList<>();
        this.health = Constants.INITIAL_HEALTH;
        this.maxMana = id == 1 && Constants.SECOND_PLAYER_MANA_BONUS_TURNS > 0 ? 1 : 0;
        this.currentMana = this.maxMana;
        this.nextTurnDraw = 1;
        this.drawValueToShow = this.nextTurnDraw;

        bonusManaTurns = id == 1 ? Constants.SECOND_PLAYER_MANA_BONUS_TURNS : 0;

        handLimit = Constants.MAX_CARDS_IN_HAND + (id == 0 ? 0 : Constants.SECOND_PLAYER_MAX_CARD_BONUS);
        DrawCards(INITIAL_HAND_SIZE + (id == 0 ? 0 : Constants.SECOND_PLAYER_CARD_BONUS), 0);
    }

    private void DamagePlayerWithEmptyDeck() {
        health -= Constants.EMPTY_DECK_DAMAGE;
    }

    public void DrawCards(int n, int playerturn) {
        for (int i = 0; i < n; i++) {
            if (deck.isEmpty() || playerturn >= Constants.PLAYER_TURNLIMIT) {
                DamagePlayerWithEmptyDeck();
                continue;
            }

            if (hand.size() == handLimit)
                break; // additional draws are simply wasted

            Card c = deck.remove(0);
            hand.add(c);
        }
    }


    public void ModifyHealth(int mod) {
        health += mod;
        if (mod >= 0)
            return;
        healthLostThisTurn += mod;
    }

    public void removeFromBoard(int creatureIndex) {
        graveyard.add(board.remove(creatureIndex));
    }

    public String toDescriptiveString(boolean reverse) {
        String line1 = String.format("[Player %d] Health: %d     Mana: %d/%d", id, health, currentMana, maxMana);
        String line2 = String.format("Cards in hand: %d   In deck: %d   Next turn draw: %d", hand.size(), deck.size(), nextTurnDraw);

        ArrayList<String> inhand = new ArrayList<>();
        inhand.add("Hand:");
        for (Card c : hand)
            inhand.add((c.cost <= this.currentMana ? " * " : "   ") + c.toDescriptiveString());

        ArrayList<String> onboard = new ArrayList<>();
        onboard.add("Board:");
        for (CreatureOnBoard c : board)
            onboard.add((c.canAttack ? " * " : "   ") + c.toDescriptiveString());

        ArrayList<String> description = new ArrayList<>();
        description.add(line1);
        description.add(line2);
        description.add(String.join("\n", inhand));
        description.add(String.join("\n", onboard));
        if (reverse)
            Collections.reverse(description);

        return String.join("\n", description);
    }

    // todo
    public String toString() {
        return super.toString();
    }

    public String getPlayerInput() {
        return health + " " +
                maxMana + " " +
                deck.size() + " " +
                drawValueToShow;
    }
}
