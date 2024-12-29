package ch.epfl.tchu.net;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.regex.Pattern;
/**
 * l'ensemble des serdes du jeux
 * @author Barrada Soufiane(310890)
 * @author Zaaraoui Imane (314994)
 */
public final class Serdes {
    /**
     * sérialise et des désirialise des entiers
     */
    public static final Serde<Integer> SERDE_INTEGER = Serde.of((Integer i) -> Integer.toString(i)  , (String s) ->  Integer.valueOf(s));
    /**
     * sérialise et des désirialise des chaines de carctères
     */
    public static final Serde<String> SERDE_STRING = Serde.of((String s) -> {
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(s.getBytes(StandardCharsets.UTF_8));
    }, (String s)->{
        Base64.Decoder decoder = Base64.getDecoder();
        return new String(decoder.decode(s),StandardCharsets.UTF_8);
    });
    /**
     * sérialise et désirialise les identités des joueurs
     */
    public static final Serde<PlayerId> SERDE_PLAYERID = Serde.oneOf(PlayerId.ALL);
    /**
     * sérialise et désirialise le type d'action du joueur
     */
    public static final Serde<Player.TurnKind> SERDE_TURNKIND = Serde.oneOf(Player.TurnKind.ALL);
    /**
     * sérialise et des désirialise des cartes
     */
    public static final Serde<Card> SERDE_CARD = Serde.oneOf(Card.ALL);
    /**
     * sérialise et désirialise des routes
     */
    public static final Serde<Route> SERDE_ROUTE = Serde.oneOf(ChMap.routes());
    /**
     * sérialise et désirialise des tickets
     */
    public static final Serde<Ticket> SERDE_TICKET = Serde.oneOf(ChMap.tickets());
    /**
     * sérialise et désirialise des listes de cheines de caractères
     */
    public static final Serde<List<String>> SERDE_LISTSTRING = Serde.listOf( SERDE_STRING, ",");
    /**
     * sérialise et désirialise des listes de cartes
     */
    public static final Serde<List<Card>> SERDE_LISTCARD = Serde.listOf(SERDE_CARD, ",");
    /**
     * sérialise et désirialise de routes
     */
    public static final Serde<List<Route>> SERDE_LISTROUTES = Serde.listOf(SERDE_ROUTE, ",");
    /**
     * sérialise et désirialise des multiensembles de cartes
     */
    public static final Serde<SortedBag<Card>> SERDE_BAGCARD = Serde.bagOf(SERDE_CARD, ",");
    /**
     * sérialise et désirialise des multiensembles de tickets
     */
    public static final Serde<SortedBag<Ticket>> SERDE_BAGTICKET = Serde.bagOf(SERDE_TICKET, ",");
    /**
     * sérialise et désirialise des listes de multiensembles de cartes
     */
    public static final Serde<List<SortedBag<Card>>> SERDE_LISTBAGCARD= Serde.listOf(SERDE_BAGCARD, ";");
    /**
     * sérialise et désirialise l'état publique des cartes
     */
    public static final Serde<PublicCardState> SERDE_PUBLICCARDSTATE = Serde.of((PublicCardState p)->{
        StringJoiner j = new StringJoiner(";");
        j.add(SERDE_LISTCARD.serialize(p.faceUpCards()));
        j.add(SERDE_INTEGER.serialize(p.deckSize()));
        j.add(SERDE_INTEGER.serialize(p.discardsSize()));
        return j.toString();
    }, (String g)->{
        String[] chaines = g.split(Pattern.quote(";"), -1);
        return new PublicCardState(SERDE_LISTCARD.deserialize(chaines[0]), SERDE_INTEGER.deserialize(chaines[1]), SERDE_INTEGER.deserialize(chaines[2]));
    });
    /**
     * sérialise et désirialise l'état publique des joueurs
     */
    public static final Serde<PublicPlayerState> SERDE_PUBLICPLAYERSTATE = Serde.of((PublicPlayerState p)->{
        StringJoiner j = new StringJoiner(";");
        j.add(SERDE_INTEGER.serialize(p.ticketCount()));
        j.add(SERDE_INTEGER.serialize(p.cardCount()));
        j.add(SERDE_LISTROUTES.serialize(p.routes()));
        return j.toString();
    },(String g)->{
        String[] chaines = g.split(Pattern.quote(";"), -1);
        Preconditions.checkArgument(chaines.length==3);
        return new PublicPlayerState(SERDE_INTEGER.deserialize(chaines[0]), SERDE_INTEGER.deserialize(chaines[1]),SERDE_LISTROUTES.deserialize(chaines[2]));

    });
    /**
     * sérialise et désirialise l'état privée des joueurs
     */
    public static final Serde<PlayerState> SERDE_PLAYERSTATE = Serde.of((PlayerState p)->{
        StringJoiner j = new StringJoiner(";");
        j.add(SERDE_BAGTICKET.serialize(p.tickets()));
        j.add(SERDE_BAGCARD.serialize(p.cards()));
        j.add(SERDE_LISTROUTES.serialize(p.routes()));
        return j.toString();
    },  (String g)->{
        String[] chaines = g.split(Pattern.quote(";"), -1);
        Preconditions.checkArgument(chaines.length==3);
        return new PlayerState(SERDE_BAGTICKET.deserialize(chaines[0]),SERDE_BAGCARD.deserialize(chaines[1]), SERDE_LISTROUTES.deserialize(chaines[2]));
    } );
    /**
     * sérialise et désirialise l'état publique du jeux
     */
    public static final Serde<PublicGameState> SERDE_PUBLICGAMESTATE = Serde.of((PublicGameState g)->{
        StringJoiner j = new StringJoiner(":");
        j.add(SERDE_INTEGER.serialize(g.ticketsCount()));
        j.add(SERDE_PUBLICCARDSTATE.serialize(g.cardState()));
        j.add(SERDE_PLAYERID.serialize(g.currentPlayerId()));
        j.add(SERDE_PUBLICPLAYERSTATE.serialize(g.playerState(PlayerId.PLAYER_1)));
        j.add(SERDE_PUBLICPLAYERSTATE.serialize(g.playerState(PlayerId.PLAYER_2)));
        if(g.lastPlayer()==null) j.add("");
        else j.add(SERDE_PLAYERID.serialize(g.lastPlayer()));
        return j.toString();
    }, (String s)->{
        String[] chaines = s.split(Pattern.quote(":"), -1);
        Preconditions.checkArgument(chaines.length==6);
        PlayerId lastPlayer;
        if(chaines[5].equals("")) lastPlayer=null;
        else lastPlayer=SERDE_PLAYERID.deserialize(chaines[5]);
        return new PublicGameState(SERDE_INTEGER.deserialize(chaines[0]),SERDE_PUBLICCARDSTATE.deserialize(chaines[1]),SERDE_PLAYERID.deserialize(chaines[2])
                , Map.of(PlayerId.PLAYER_1 ,SERDE_PUBLICPLAYERSTATE.deserialize(chaines[3]),PlayerId.PLAYER_2, SERDE_PUBLICPLAYERSTATE.deserialize(chaines[4])), lastPlayer);

    });








}
