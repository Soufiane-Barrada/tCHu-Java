package ch.epfl.tchu.game;

import java.util.List;
import java.util.Map;

import ch.epfl.tchu.SortedBag;

/**
 * représente un joueur de tCHu
 *
 * @author Barrada Soufiane(310890)
 * @author Zaaraoui Imane (314994)
 */
public interface Player {
    /**
     * appelée au début de la partie pour communiquer au joueur sa propre identité ownId,
     * ainsi que les noms des différents joueurs, le sien inclus
     *
     * @param ownId       identité du joueur
     * @param playerNames une Map contenant les identités des différents joueurs et leurs noms
     */
    public abstract void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames);

    /**
     * appelée chaque fois qu'une information doit être communiquée au joueur au cours de la partie;
     * cette information est donnée sous la forme d'une chaîne de caractères
     *
     * @param info l'information qui doit etre communiquée
     */
    public abstract void receiveInfo(String info);

    /**
     * appelée chaque fois que l'état du jeu a changé,
     * pour informer le joueur de la composante publique de ce nouvel état, newState,
     * ainsi que de son propre état, ownState
     *
     * @param newState le nouvel état publique de la partie
     * @param ownState l'etat propre au jooeur
     */
    public abstract void updateState(PublicGameState newState, PlayerState ownState);

    /**
     * appelée au début de la partie pour communiquer au joueur les cinq billets tickets qui lui ont été distribués
     *
     * @param tickets les cinq buillets distribués au joueur
     */
    public abstract void setInitialTicketChoice(SortedBag<Ticket> tickets);

    /**
     * a ppelée au début de la partie pour demander au joueur lesquels des billets
     * qu'on lui a distribué initialement il garde
     *
     * @return les billets qu'il souhaite garder
     */
    public abstract SortedBag<Ticket> chooseInitialTickets();

    /**
     * appelée au début du tour d'un joueur, pour savoir quel type d'action il désire effectuer durant ce tour
     *
     * @return le type d'action que le joueur désire effectuer
     */
    public abstract TurnKind nextTurn();

    /**
     * appelée lorsque le joueur a décidé de tirer des billets supplémentaires en cours de partie,
     * afin de lui communiquer les billets tirés et de savoir lesquels il garde
     *
     * @param options les billets tirés
     * @return les billets que le joueur garde
     */
    public abstract SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options);

    /**
     * est appelée lorsque le joueur a décidé de tirer des cartes wagon/locomotive,
     * afin de savoir d'où il désire les tirer
     *
     * @return entre 0 et 4 inclus si il prend une des cartes visibles. -1 autrement
     */
    public abstract int drawSlot();

    /**
     * appelée lorsque le joueur a décidé de (tenter de) s'emparer d'une route,
     * afin de savoir de quelle route il s'agit
     *
     * @return
     */
    public abstract Route claimedRoute();

    /**
     * appelée lorsque le joueur a décidé de (tenter de) s'emparer d'une route,
     *
     * @return les cartes que le joueurs désire utiliser pour s'emparer de la route
     */
    public abstract SortedBag<Card> initialClaimCards();

    /**
     * appelée lorsque le joueur a décidé de tenter de s'emparer d'un tunnel
     * et que des cartes additionnelles sont nécessaires
     *
     * @param options les possibilités des combinaisons des cartes additionnels qu'il peut utiliser
     * @return les cartes additionnels que le joueur à choisi
     */
    public abstract SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options);


    /**
     * représente les trois types d'actions qu'un joueur de tCHu peut effectuer durant un tour
     *
     * @author Barrada Soufiane(310890)
     * @author Zaaraoui Imane (314994)
     */
    public enum TurnKind {
        DRAW_TICKETS, DRAW_CARDS, CLAIM_ROUTE;
        // liste immuable contenant la totalité des valeurs du type énuméré,
        // dans leur ordre de définition
        public static final List<TurnKind> ALL = List.of(TurnKind.values());
    }

}
