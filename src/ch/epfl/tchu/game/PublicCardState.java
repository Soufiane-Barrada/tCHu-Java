package ch.epfl.tchu.game;

import java.util.List;
import java.util.Objects;

import ch.epfl.tchu.Preconditions;

/**
 * représente une partie de l'état des cartes qui ne sont pas en main des joueurs
 */
public class PublicCardState {

    private final List<Card> faceUpCards;
    private final int deckSize;
    private final int discardsSize;

    /**
     * construit un état public des cartes
     *
     * @param faceUpCards  les cartes face visible
     * @param deckSize     la taille de la pioche
     * @param discardsSize taille de la défausse
     */
    public PublicCardState(List<Card> faceUpCards, int deckSize, int discardsSize) {
        Preconditions.checkArgument(faceUpCards.size() == Constants.FACE_UP_CARDS_COUNT);
        Preconditions.checkArgument(deckSize >= 0);
        Preconditions.checkArgument(discardsSize >= 0);
        this.faceUpCards = List.copyOf(faceUpCards);
        this.deckSize = deckSize;
        this.discardsSize = discardsSize;

    }

    /**
     * @return le nombre total de cartes qui ne sont pas en main des joueurs
     */
    public int totalSize() {
        return deckSize + (faceUpCards.size()) + discardsSize;
    }

    /**
     * retourne les 5 cartes face visible, sous la forme d'une liste comportant exactement 5 éléments
     *
     * @return retourne les 5 cartes face visibl
     */
    public List<Card> faceUpCards() {
        return List.copyOf(faceUpCards);
    }

    /**
     * retourne la carte face visible à l'index donné
     *
     * @param slot
     * @return la carte face visible à l'index donné
     */
    public Card faceUpCard(int slot) {
        if ( ! (slot >= 0 && slot < Constants.FACE_UP_CARDS_COUNT)) {
            throw new IndexOutOfBoundsException();
        }
        return faceUpCards.get(Objects.checkIndex(slot, faceUpCards.size()));
    }

    /**
     * retourne la taille de la pioche
     *
     * @return la taille de la pioche
     */
    public int deckSize() {
        return deckSize;
    }

    /**
     * retourne vrai ssi la pioche est vide
     *
     * @return true ssi la pioche est vide
     */
    public boolean isDeckEmpty() {
        return deckSize == 0;
    }

    /**
     * retourne la taille de la défausse
     *
     * @return la taille de la défausse
     */
    public int discardsSize() {
        return discardsSize;
    }


}
