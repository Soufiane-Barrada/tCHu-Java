package ch.epfl.tchu.net;
import java.util.List;
/**
 * les types de messages que le serveur peut envoyer aux clients.
 * @author Barrada Soufiane(310890)
 * @author Zaaraoui Imane (314994)
 */
public enum MessageId {
    INIT_PLAYERS,
    RECEIVE_INFO,
    UPDATE_STATE,
    SET_INITIAL_TICKETS,
    CHOOSE_INITIAL_TICKETS,
    NEXT_TURN,
    CHOOSE_TICKETS,
    DRAW_SLOT,
    ROUTE,
    CARDS,
    CHOOSE_ADDITIONAL_CARDS;
    public final  static List<MessageId> ALL = List.of(MessageId.values());
    public final static int COUNT = ALL.size();


}
