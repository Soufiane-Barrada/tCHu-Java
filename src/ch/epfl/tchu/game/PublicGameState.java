package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import ch.epfl.tchu.Preconditions;

/**
 * représente la partie publique de l'état d'une partie de tCHu
 *
 * @author Barrada Soufiane(310890)
 * @author Zaaraoui Imane (314994)
 */
public class PublicGameState {
    private int ticketsCount;
    private final PublicCardState cardState;
    private final PlayerId currentPlayerId;
    private final Map<PlayerId, PublicPlayerState> playerState;
    private final PlayerId lastPlayer;

    /**
     * construit la partie publique de l'état d'une partie de tCHu
     *
     * @param ticketsCount    la taille de la pioche de billets
     * @param cardState       l'état public des cartes wagon/locomotive
     * @param currentPlayerId le joueur courant
     * @param playerState     l'état public des joueurs
     * @param lastPlayer      l'identité du dernier joueur
     * @throws IllegalArgumentException la taille de la pioche est strictement négative ou si
     *                                  playerState ne contient pas exactement deux paires clef/valeur
     * @throws NullPointerException     si cardState ou currentPlayerId est nul
     */
    public PublicGameState(int ticketsCount, PublicCardState cardState, PlayerId currentPlayerId,
                           Map<PlayerId, PublicPlayerState> playerState, PlayerId lastPlayer) {

        Preconditions.checkArgument(ticketsCount >= 0);
        Preconditions.checkArgument(playerState.size() == 2);

        this.ticketsCount = ticketsCount;
        this.cardState = Objects.requireNonNull(cardState);
        ;
        this.currentPlayerId = Objects.requireNonNull(currentPlayerId);
        ;
        this.playerState = playerState;
        this.lastPlayer = lastPlayer;

    }

    /**
     * @return la taille de la pioche de billets
     */
    public int ticketsCount() {
        return ticketsCount;
    }

    /**
     * @return true ssi il est possible de tirer des billets, c-à-d si la pioche n'est pas vide
     */
    public boolean canDrawTickets() {
        if (ticketsCount != 0) return true;
        else return false;
    }

    /**
     * @return la partie publique de l'état des cartes wagon/locomotive
     */
    public PublicCardState cardState() {
        return cardState;
    }

    /**
     * @return true ssi il est possible de tirer des cartes
     */
    public boolean canDrawCards() {

        return cardState.discardsSize() + cardState.deckSize() >= 5;

    }

    /**
     * @return l'identité du joueur actuel
     */
    public PlayerId currentPlayerId() {
        return currentPlayerId;
    }

    /**
     * @param playerId l'identité du joueur
     * @return la partie publique de l'état du joueur playerId
     */
    public PublicPlayerState playerState(PlayerId playerId) {
        return playerState.get(playerId);
    }

    /**
     * @return la partie publique de l'état du joueur courant
     */
    public PublicPlayerState currentPlayerState() {
        return playerState.get(currentPlayerId);
    }

    /**
     * @return retourne la totalité des routes dont l'un ou l'autre des joueurs s'est emparé
     */
    public List<Route> claimedRoutes() {
        List<Route> l = new ArrayList<>();
        for (Route route : playerState.get(PlayerId.PLAYER_1).routes()) {
            l.add(route);
        }
        for (Route route : playerState.get(PlayerId.PLAYER_2).routes()) {
            l.add(route);
        }
        return l;
    }

    /**
     * @return retourne l'identité du dernier joueur, ou null si elle n'est pas encore connue car le dernier tour n'a pas commencé
     */
    public PlayerId lastPlayer() {

        return lastPlayer;
    }


}
