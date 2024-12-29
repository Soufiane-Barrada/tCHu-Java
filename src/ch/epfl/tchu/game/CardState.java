package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

/**
 * représente l'état des cartes qui ne sont pas en main des joueurs
 *
 * @author Barrada Soufiane(310890)
 * @author Zaaraoui Imane (314994)
 */
public final class CardState extends PublicCardState {

    private Deck<Card> pioche;
    private SortedBag<Card> defausse;

    /**
     * Constructeur privé
     */
    private CardState(List<Card> faceUpCards, Deck<Card> pioche, SortedBag<Card> defausse) {

        super(faceUpCards, pioche.size(), defausse.size());
        this.pioche = pioche;
        this.defausse = defausse;

    }

    /**
     * construit un état dans lequel les 5 cartes disposées faces visibles sont les 5 premières du tas donné,
     * la pioche est constituée des cartes du tas restantes, et la défausse est vide
     *
     * @param deck tas de cartes
     * @throws IllegalArgumentException si le tas donné contient moins de 5 cartes
     */
    public static CardState of(Deck<Card> deck) {
        Preconditions.checkArgument(deck.size() >= Constants.FACE_UP_CARDS_COUNT);
        return new CardState(deck.topCards(5).toList(), deck.withoutTopCards(5), SortedBag.<Card>of());

    }

    /**
     * @param slot indew de la carte face visible qu'on veux remplacer
     * @return un état ou l'on a que l'ensemble de cartes est identique au récepteur sauf pour
     * la carte face visible d'index slot remplacée par une de la pioche.
     * @throws IllegalArgumentException si la pioche est vide
     */
    public CardState withDrawnFaceUpCard(int slot) {
        Preconditions.checkArgument(slot >= 0 && slot < Constants.FACE_UP_CARDS_COUNT);
        List<Card> faceUpCopy = new ArrayList<>(faceUpCards());
        faceUpCopy.set(slot, pioche.topCard());
        return new CardState(faceUpCopy, pioche.withoutTopCard(), defausse);

    }

    /**
     * @return la carte se trouvant au sommet de la pioche
     * @throws IllegalArgumentException si la pioche est vide
     */
    public Card topDeckCard() {
        Preconditions.checkArgument(pioche.size() > 0);
        return pioche.topCard();
    }

    /**
     * retourne un ensemble de cartes identique au récepteur (this),
     * mais sans la carte se trouvant au sommet de la pioche
     *
     * @return un ensemble de cartes identique au récepteur (this), mais sans la carte se trouvant au sommet de la pioche
     * @throws IllegalArgumentException si la pioche est vide
     */
    public CardState withoutTopDeckCard() {
        Preconditions.checkArgument(pioche.size() > 0);
        return new CardState(faceUpCards(), pioche.withoutTopCard(), defausse);

    }


    /**
     * reconstitue une nouvelle pioche
     *
     * @param rng
     * @return un nouveau etat ou nous avons un nouveau deck à partir de la defausse mélangée
     * @throws IllegalArgumentException si la pioche du récepteur n'est pas vide
     */
    public CardState withDeckRecreatedFromDiscards(Random rng) {

        Preconditions.checkArgument(pioche.size() == 0);
        return new CardState(faceUpCards(), Deck.of(defausse, rng), SortedBag.<Card>of());

    }

    /**
     * Ajoute des un ensemble de carte à la défausse
     *
     * @param additionalDiscards l'ensemble de carte à ajouter à la défausse
     * @return un ensemble de cartes identique au récepteur (this),
     * mais avec les cartes données ajoutées à la défausse
     */
    public CardState withMoreDiscardedCards(SortedBag<Card> additionalDiscards) {

        return new CardState(faceUpCards(), pioche, defausse.union(additionalDiscards));

    }


}
