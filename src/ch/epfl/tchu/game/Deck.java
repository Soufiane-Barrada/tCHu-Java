package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.SortedBag.Builder;

/**
 * Un tas de carte
 *
 * @param <C>
 */
public final class Deck<C extends Comparable<C>> {

    private final List<C> cards;

    /**
     * Constructeur privée
     *
     * @param cards La liste des cards du tas
     */
    private Deck(List<C> cards) {
        this.cards = cards;

    }

    /**
     * Methode de constructon public
     *
     * @param cards Le multiensemble de cartes qu'on veut comme tas.
     * @param rng   Générateur de nombres aléatoires rng
     * @return retourne un tas de cartes ayant les mêmes cartes que le multiensemble cards,
     * mélangées au moyen du générateur de nombres aléatoires rng
     */
    public static <C extends Comparable<C>> Deck<C> of(SortedBag<C> cards, Random rng) {

        List<C> l = cards.toList();
        Collections.shuffle(l, rng);
        return new Deck<C>(l);

    }

    /**
     * retourne la taille du tas
     *
     * @return la taille du tas
     */
    public int size() {
        return cards.size();
    }

    /**
     * retourne vrai ssi le tas est vide
     *
     * @return true si le tas est vide
     */
    public boolean isEmpty() {
        if (cards.size() == 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @return la carte au sommet du tas
     * @throws IllegalArgumentException si le tas est vide
     */
    public C topCard() {
        Preconditions.checkArgument(this.cards.size() != 0);
        return cards.get(0);
    }

    /**
     * @return un tas identique au récepteur (this) mais sans la carte au sommet
     * @throws IllegalArgumentException si le tas est vide
     */
    public Deck<C> withoutTopCard() {
        Preconditions.checkArgument(cards.size() > 0);
        List<C> l = new ArrayList<>(cards);

        if (l.size() > 1) {
            return new Deck<C>(l.subList(1, l.size()));
        } else {
            l.remove(0);
            return new Deck<C>(l);
        }

/**
 * @param count taille du tas voulu (entre 0 et taille du tas)
 * @throws IllegalArgumentException si count n'est pas compris entre 0 (inclus) et la taille du tas (incluse),
 * @return un multiensemble contenant les count cartes se trouvant au sommet du tas
 */
    }

    public SortedBag<C> topCards(int count) {
        Preconditions.checkArgument(count >= 0 && count <= cards.size());
        if (count == 0) {
            return new SortedBag.Builder<C>().build();
        } else if (count == 1) {
            return SortedBag.of(1, cards.get(0));

        } else {
            Builder<C> b = new SortedBag.Builder<C>();
            for (int i = 0; i <= count - 1; i++) {
                b.add(cards.get(i));
            }
            return b.build();
        }

    }

    /**
     * @param count (entre 0 et taille du tas)
     * @return un tas identique au récepteur (this) mais sans les count cartes du sommet
     * @throws IllegalArgumentException si count n'est pas compris entre 0 (inclus) et la taille du tas (incluse).
     */
    public Deck<C> withoutTopCards(int count) {
        Preconditions.checkArgument(count >= 0 && count <= cards.size());
        if (count == cards.size()) {
            return new Deck<C>(List.of());
        } else {
            List<C> l = new ArrayList<>(cards);

            return new Deck<C>(l.subList(count, l.size()));
        }


    }

    /**
     * @return la liste des cartes
     */
    public List<C> cards() {
        return cards;
    }


}
