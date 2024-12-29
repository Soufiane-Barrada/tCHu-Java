package ch.epfl.tchu.game;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.SortedBag.Builder;

/**
 * représente l'état d'une partie de tCHu
 *
 * @author Barrada Soufiane(310890)
 * @author Zaaraoui Imane (314994)
 */
public final class GameState extends PublicGameState {
    private final Deck<Ticket> tickets;
    private final Map<PlayerId, PlayerState> playerState;
    private final CardState cardState;

    private GameState(Deck<Ticket> tickets, CardState cardState, PlayerId currentPlayerId,
                      Map<PlayerId, PlayerState> playerState, PlayerId lastPlayer) {

        super(tickets.size(), cardState, currentPlayerId, Map.copyOf(playerState), lastPlayer);
        this.tickets = tickets;
        this.cardState = cardState;
        this.playerState = Map.copyOf(playerState);


    }

    /**
     * crée l'état initial d'une partie de tCHu dans laquelle la pioche
     * des billets contient les billets donnés et la pioche des cartes contient les cartes Constants.ALL_CARDS
     *
     * @param tickets la pioche des billets
     * @param rng     générateur aléatoire
     * @return l'état initial d'une partie de tCHu
     */
    public static GameState initial(SortedBag<Ticket> tickets, Random rng) {

        Deck<Card> dc = Deck.of(Constants.ALL_CARDS, rng);
        Map<PlayerId, PlayerState> m = new EnumMap<>(PlayerId.class);


        for (PlayerId id : PlayerId.ALL) {
            m.put(id, PlayerState.initial(dc.topCards(4)));
            dc = dc.withoutTopCards(4);
        }


        CardState cardState = CardState.of(dc);
        int premJou = rng.nextInt(PlayerId.COUNT);

        return (premJou == 0) ? new GameState(Deck.of(tickets, rng), cardState, PlayerId.PLAYER_1, m, null) :
                new GameState(Deck.of(tickets, rng), cardState, PlayerId.PLAYER_2, m, null);
    }

    /**
     * @param playerId l'identité du joueur
     * @return l'etat complet du joueur d'identité playerId
     */
    public PlayerState playerState(PlayerId playerId) {
        return playerState.get(playerId);
    }

    /**
     * @return l'état complet du joueur playerId
     */
    public PlayerState currentPlayerState() {
        return playerState.get(currentPlayerId());
    }

    /**
     * @param count nombre de tickets à piocher
     * @return les count billets du sommet de la pioche
     * @throws IllegalArgumentException si count n'est pas compris entre 0 et la taille de la pioche (inclus)
     */
    public SortedBag<Ticket> topTickets(int count) {
        Preconditions.checkArgument(count >= 0 && count <= ticketsCount());
        return tickets.topCards(count);
    }

    /**
     * @param count nombre de billets qu'on veut éliminer
     * @return retourne un état identique au récepteur, mais sans les count billets du sommet de la pioche
     * @throws IllegalArgumentException si count n'est pas compris entre 0 et la taille de la pioche (inclus)
     */
    public GameState withoutTopTickets(int count) {
        Preconditions.checkArgument(count >= 0 && count <= ticketsCount());
        return new GameState(tickets.withoutTopCards(count), cardState, currentPlayerId(), playerState, lastPlayer());

    }

    /**
     * @return la carte au sommet de la pioche
     * @throws IllegalArgumentException si la pioche est vide
     */
    public Card topCard() {
        Preconditions.checkArgument(cardState.deckSize() != 0);
        return cardState.topDeckCard();
    }

    /**
     * @return retourne un état identique au récepteur mais sans la carte au sommet de la pioche
     * @throws IllegalArgumentException
     */
    public GameState withoutTopCard() {
        Preconditions.checkArgument(cardState.deckSize() != 0);
        return new GameState(tickets, cardState.withoutTopDeckCard(), currentPlayerId(), playerState, lastPlayer());
    }

    /**
     * @param discardedCards cartes à ajoutées à la défausse
     * @return retourne un état identique au récepteur mais avec les cartes discardedCards ajoutées à la défausse
     */
    public GameState withMoreDiscardedCards(SortedBag<Card> discardedCards) {
        return new GameState(tickets, cardState.withMoreDiscardedCards(discardedCards),
                currentPlayerId(), playerState, lastPlayer());
    }

    /**
     * si la pioche de cartes est vide,
     * la methode recrée un GameState avec des cartes à partir de la défausse,
     * mélangée au moyen du générateur aléatoire rng
     *
     * @param rng générateur aléatoire
     * @return un état identique au récepteur sauf si la pioche de cartes est vide
     */
    public GameState withCardsDeckRecreatedIfNeeded(Random rng) {
        return cardState.deckSize() == 0 ? new GameState(tickets, cardState.withDeckRecreatedFromDiscards(rng),
                currentPlayerId(), playerState, lastPlayer()) : this;
    }

    /**
     * @param playerId      identité du joueur
     * @param chosenTickets les billets choisies
     * @return retourne un état identique au récepteur mais dans lequel
     * les billets chosenTickets ont été ajoutés à la main du joueur playerId
     */
    public GameState withInitiallyChosenTickets(PlayerId playerId, SortedBag<Ticket> chosenTickets) {
        Preconditions.checkArgument(playerState.get(playerId).ticketCount() == 0);
        Map<PlayerId, PlayerState> m = new EnumMap<>(playerState);
        m.put(playerId, m.get(playerId).withAddedTickets(chosenTickets));
        return new GameState(tickets, cardState, currentPlayerId(), m, lastPlayer());
    }

    /**
     * @param drawnTickets  les billets tirés de la pioche
     * @param chosenTickets les billets que le joueur choisi de garder
     * @return retourne un état identique au récepteur, mais dans lequel le joueur courant a
     * tiré les billets drawnTickets du sommet de la pioche, et choisi de garder ceux contenus dans chosenTicket
     */
    public GameState withChosenAdditionalTickets(SortedBag<Ticket> drawnTickets, SortedBag<Ticket> chosenTickets) {
        Preconditions.checkArgument(drawnTickets.contains(chosenTickets));
        Map<PlayerId, PlayerState> m = new EnumMap<>(playerState);
        m.put(currentPlayerId(), m.get(currentPlayerId()).withAddedTickets(chosenTickets));
        var t = tickets.withoutTopCards(drawnTickets.size());
        return new GameState(t, cardState, currentPlayerId(), m, lastPlayer());
    }

    /**
     * @param slot emplacement de la carte face retournée
     * @return qui retourne un état identique au récepteur si ce n'est que la carte face retournée à
     * l'emplacement slot a été placée dans la main du joueur courant, et remplacée par celle au sommet de la pioche
     */
    public GameState withDrawnFaceUpCard(int slot) {
        Map<PlayerId, PlayerState> m = new EnumMap<>(playerState);
        m.put(currentPlayerId(), m.get(currentPlayerId()).withAddedCard(cardState.faceUpCard(slot)));
        return new GameState(tickets, cardState.withDrawnFaceUpCard(slot), currentPlayerId(), m, lastPlayer());
    }

    /**
     * @return retourne un état identique au récepteur si ce n'est que la carte
     * du sommet de la pioche a été placée dans la main du joueur courant
     * @throws IllegalArgumentException s'il n'est pas possible de tirer des cartes
     */
    public GameState withBlindlyDrawnCard() {
        Map<PlayerId, PlayerState> m = new EnumMap<>(playerState);
        m.put(currentPlayerId(), m.get(currentPlayerId()).withAddedCard(cardState.topDeckCard()));
        return new GameState(tickets, cardState.withoutTopDeckCard(), currentPlayerId(), m, lastPlayer());
    }

    /**
     * @param route route dont le joueur s'est emparé
     * @param cards la cartes utilisés par le joueur pour s'emparer de la route
     * @return retourne un état identique au récepteur mais
     * dans lequel le joueur courant s'est emparé de la route route au moyen des cartes cards
     */
    public GameState withClaimedRoute(Route route, SortedBag<Card> cards) {
        Map<PlayerId, PlayerState> m = new EnumMap<>(playerState);
        m.put(currentPlayerId(), m.get(currentPlayerId()).withClaimedRoute(route, cards));

        return new GameState(tickets, cardState.withMoreDiscardedCards(cards), currentPlayerId(), m, lastPlayer());
    }

    /**
     * @return vrai ssi le dernier tour commence
     */
    public boolean lastTurnBegins() {
        return lastPlayer() == null && playerState.get(currentPlayerId()).carCount() <= 2;

    }

    /**
     * termine le tour du joueur courant
     *
     * @return retourne un état identique au récepteur si ce n'est que le
     * joueur courant est celui qui suit le joueur courant actuel
     */
    public GameState forNextTurn() {
        return lastTurnBegins() == true ? new GameState(tickets, cardState, currentPlayerId().next(),
                playerState, currentPlayerId()) :

                new GameState(tickets, cardState, currentPlayerId().next(), playerState, lastPlayer());

    }


}
