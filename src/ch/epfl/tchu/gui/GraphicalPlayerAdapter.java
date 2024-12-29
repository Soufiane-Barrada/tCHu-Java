package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.application.Platform;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
/**
 * adapte (patron Adapter) une instance de GraphicalPlayer
 * en une valeur de type Player
 * @author Barrada Soufiane(310890)
 * @author Zaaraoui Imane (314994)
 */


public final class GraphicalPlayerAdapter implements Player {

    private GraphicalPlayer graphicalPlayer;

    private final BlockingQueue<Integer> slotQueue;
    private final BlockingQueue<Route> routeQueue;
    private final BlockingQueue<SortedBag<Ticket>> ticketQueue;
    private final BlockingQueue<SortedBag<Card>> bagQueue;

    /**
     * construit un GraphicalPlayerAdapter
     */
    public GraphicalPlayerAdapter() {
        slotQueue = new ArrayBlockingQueue<>(1);
        routeQueue = new ArrayBlockingQueue<>(1);
        ticketQueue = new ArrayBlockingQueue<>(1);
        bagQueue = new ArrayBlockingQueue<>(1);
    }

    /**
     * construit sur le fil JavaFX l'instance du joueur graphique
     * @param ownId identité du joueur
     * @param playerNames une Map contenant les identités des différents joueurs et leurs noms
     */

    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        BlockingQueue<GraphicalPlayer> playerQueue = new ArrayBlockingQueue<>(1);
        Platform.runLater(() -> playerQueue.add(new GraphicalPlayer(ownId, playerNames)));
        graphicalPlayer = dequeue(playerQueue);
    }

    /**
     * appelle sur le fil JavaFX la méthode du même nom du joueur graphique
     * @param info l'information qui doit etre communiquée au joueurs
     */
    @Override
    public void receiveInfo(String info) {
        Platform.runLater(() -> graphicalPlayer.receiveInfo(info));
    }

    /**
     *appelle sur le fil JavaFX la méthode setState du joueur graphique
     * @param newState le nouvel état publique de la partie
     * @param ownState l'etat propre au jooeur
     */
    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        Platform.runLater(() -> graphicalPlayer.setState(newState, ownState));
    }

    /**
     *appelle, sur le fil JavaFX la méthode chooseTickets du joueur graphique
     * @param tickets les cinq buillets distribués au joueur
     */
    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        Platform.runLater(() -> graphicalPlayer.chooseTickets(tickets, ticketQueue::add));
    }

    /**
     * @return le premier élèment de la file contenant les multiensembles de tickets
     * la partie
     */
    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        return dequeue(ticketQueue);
    }

    /**
      * appelle sur le fil JavaFX la méthode startTurn du joueur graphique
      * @return le premier élèment de la file contenant le type de tour
     */
    @Override
    public TurnKind nextTurn() {
        BlockingQueue<TurnKind> turnkindQueue = new ArrayBlockingQueue<>(1);
        Platform.runLater(() -> graphicalPlayer.startTurn(
                () -> turnkindQueue.add(TurnKind.DRAW_TICKETS),
                (int d) -> {
                    turnkindQueue.add(TurnKind.DRAW_CARDS);
                    slotQueue.add(d);
                }, (Route route, SortedBag<Card> initialCards) -> {
                    turnkindQueue.add(TurnKind.CLAIM_ROUTE);
                    routeQueue.add(route);
                    bagQueue.add(initialCards);
                }));
        return dequeue(turnkindQueue);
    }

    /**
     *
     * @param options les billets tirés
     * @return le premier élément de la file contenant les multiensembles de tickets
     */
    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        Platform.runLater(() -> graphicalPlayer.chooseTickets(options, ticketQueue::add));
        return dequeue(ticketQueue);
    }

    /**
     * @return le premier élèment de la file contenant les emplacements des cartes
     * si elle n'est pas vide sinon bloque en attendant que le gestionnaire qu'on lui passe place
     * l'emplacement de la carte tirée dans la file qui est  retourné
     */
    @Override
    public int drawSlot() {
        if (slotQueue.isEmpty()) {
            Platform.runLater(() -> graphicalPlayer.drawCard(slotQueue::add));
        }
        return dequeue(slotQueue);
    }

    /**
     *
     * @return le premier élément de la file contenant les routes
     */
    @Override
    public Route claimedRoute() {
        return dequeue(routeQueue);
    }

    /**
     *
     * @return l'élément  placé dans la file contenant les multiensembles de cartes
     */
    @Override
    public SortedBag<Card> initialClaimCards() {
        return dequeue(bagQueue);
    }

    /**
     *  retourne l'élément  placé dans la file contenant les multiensembles de cartes
     * @param options les possibilités des combinaisons des cartes additionnels qu'il peut utiliser
     * @return l'élément  placé dans la file contenant les multiensembles de cartes
     */
    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        Platform.runLater(() -> graphicalPlayer.chooseAdditionalCards(options, bagQueue::add));
        return dequeue(bagQueue);
    }

    /**
     * retourne le premier élèment de cette file
     * @param queue file bloquante
     * @return le premier élèment de cette file
     */
    private static <T> T dequeue(BlockingQueue<T> queue) {
        try {
            return queue.take();
        } catch (InterruptedException e) {
            throw new Error();
        }
    }
}
