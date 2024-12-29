package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import static java.nio.charset.StandardCharsets.US_ASCII;
/**
 * Mandataire du joueur distant.
 *
 * @author Barrada Soufiane(310890)
 * @author Zaaraoui Imane (314994)
 */

public class RemotePlayerProxy implements Player {

    private final BufferedReader r;
    private final BufferedWriter w;

    /**
     * @param socket socket utililisé pour communication via le réseau
     */
    public RemotePlayerProxy(Socket socket) throws IOException {
        this.w = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), US_ASCII));
        this.r =  new BufferedReader(new InputStreamReader(socket.getInputStream(), US_ASCII));
    }
    /**
     * serialise les arguments et envoie le texte du message via le réseau
     * @param ownId identité du joueur
     * @param playerNames une Map contenant les identités des différents joueurs et leurs noms
     */
    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        StringJoiner sj = new StringJoiner(" ");
        sj.add(Serdes.SERDE_PLAYERID.serialize(ownId));
        sj.add(Serdes.SERDE_LISTSTRING.serialize
                (List.of(playerNames.get(PlayerId.PLAYER_1), playerNames.get(PlayerId.PLAYER_2))));
        send(MessageId.INIT_PLAYERS.name(), sj.toString());
    }
    /**
     * serialise les arguments et envoie le texte du message via le réseau
     * @param info l'information qui doit etre communiquée
     */
    @Override
    public void receiveInfo(String info) {
        send(MessageId.RECEIVE_INFO.name(), Serdes.SERDE_STRING.serialize(info));
    }
    /**
     * serialise les arguments et envoie le texte du message via le réseau
     * @param newState le nouvel état publique de la partie
     * @param ownState l'etat propre au jooeur
     */
    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        StringJoiner sj = new StringJoiner(" ");
        sj.add(Serdes.SERDE_PUBLICGAMESTATE.serialize(newState));
        sj.add(Serdes.SERDE_PLAYERSTATE.serialize(ownState));
        send(MessageId.UPDATE_STATE.name(), sj.toString());
    }
    /**
     * serialise les arguments et envoie le texte du message via le réseau
     * @param tickets les cinq buillets distribués au joueur
     */
    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        send(MessageId.SET_INITIAL_TICKETS.name(), Serdes.SERDE_BAGTICKET.serialize(tickets));
    }
    /**
     * Envoie un message via le réseau pour appeler la même méthode du player hosted
     *
     * @see Player#chooseInitialTickets()
     */
    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        send(MessageId.CHOOSE_INITIAL_TICKETS.name(), "");
        return Serdes.SERDE_BAGTICKET.deserialize(receive());
    }
    /**
     * Envoie un message via le réseau pour appeler la même méthode du player hosted
     * @see Player#nextTurn()
     */
    @Override
    public TurnKind nextTurn() {
        send(MessageId.NEXT_TURN.name(), "");
        return Serdes.SERDE_TURNKIND.deserialize(receive());
    }
    /**
     * Envoie un message via le réseau pour appeler la même méthode du player hosted
     *
     * @see Player#chooseTickets(SortedBag<Ticket> )
     */
    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        send(MessageId.CHOOSE_TICKETS.name(), Serdes.SERDE_BAGTICKET.serialize(options));
        return Serdes.SERDE_BAGTICKET.deserialize(receive());
    }
    /**
     * Envoie un message via le réseau pour appeler la même méthode du player hosted
     *
     * @see Player#drawSlot()
     */
    @Override
    public int drawSlot() {
        send(MessageId.DRAW_SLOT.name(), "");
        return Serdes.SERDE_INTEGER.deserialize(receive());
    }
    /**
     * Envoie un message via le réseau pour appeler la même méthode du player hosted
     *
     * @see Player#claimedRoute()
     */
    @Override
    public Route claimedRoute() {
        send(MessageId.ROUTE.name(), "");
        return Serdes.SERDE_ROUTE.deserialize(receive());
    }
    /**
     * Envoie un message via le réseau pour appeler la même méthode du player hosted
     *
     * @see Player#initialClaimCards()
     */
    @Override
    public SortedBag<Card> initialClaimCards() {
        send(MessageId.CARDS.name(), "");
        return Serdes.SERDE_BAGCARD.deserialize(receive());
    }
    /**
     * Envoie un message via le réseau pour appeler la même méthode du player hosted
     *
     * @see Player#chooseAdditionalCards(List<SortedBag<Card>> )
     */
    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        send(MessageId.CHOOSE_ADDITIONAL_CARDS.name(), Serdes.SERDE_LISTBAGCARD.serialize(options));
        return Serdes.SERDE_BAGCARD.deserialize(receive());
    }

    private void send(String nom, String arguments) {

        try  {
            StringJoiner sj = new StringJoiner(" ");
            sj.add(nom);
            sj.add(arguments);
            sj.add("\n");
            w.write(sj.toString());
            w.flush();

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private String receive() {
        try  {
            return r.readLine();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
