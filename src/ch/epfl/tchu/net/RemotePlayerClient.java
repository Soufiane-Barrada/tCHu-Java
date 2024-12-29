package ch.epfl.tchu.net;

import ch.epfl.tchu.game.Player;
import ch.epfl.tchu.game.PlayerId;

import java.io.*;
import java.net.Socket;
import java.util.Map;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.US_ASCII;
/**
 *  client de joueur distant
 *
 * @author Barrada Soufiane(310890)
 * @author Zaaraoui Imane (314994)
 */
public class RemotePlayerClient {
    private final Player player;
    private final String nom;
    private final int port;
    /**
     * construit le client de joueur distant
     * @param player le joueur auquel l'accès distant est fourni
     * @param nom nom de ce joueur
     * @param port le port pour ce connecter au mandataire
     */
    public RemotePlayerClient(Player player, String nom, int port) {
        this.player = player;
        this.nom = nom;
        this.port = port;

    }


    private static void send(BufferedWriter w, String content) throws IOException {
        w.write(content);
        w.write('\n');
        w.flush();
    }
    /**
     * désérialise les arguments du message du mandataire et appelle la méthode correspondante du joueur
     * renvoie au mandataire une réponse si cette méthode retourne un résultat
     * @throws UncheckedIOException si une IOException est levée
     */
    public void run() {
        try (Socket s = new Socket(nom, port);
             BufferedReader r =
                     new BufferedReader(
                             new InputStreamReader(s.getInputStream(),
                                     US_ASCII));
             BufferedWriter w =
                     new BufferedWriter(
                             new OutputStreamWriter(s.getOutputStream(),
                                     US_ASCII))) {
            String line = r.readLine();
            while (line != null) {
                String[] stringTab = line.split(Pattern.quote(" "), -1);

                switch (MessageId.valueOf(stringTab[0])) {
                    case INIT_PLAYERS:
                        player.initPlayers(Serdes.SERDE_PLAYERID.deserialize(stringTab[1]),
                                Map.of(PlayerId.PLAYER_1, Serdes.SERDE_LISTSTRING.deserialize(stringTab[2]).get(0),
                                        PlayerId.PLAYER_2, Serdes.SERDE_LISTSTRING.deserialize(stringTab[2]).get(1)));

                        break;
                    case RECEIVE_INFO:
                        player.receiveInfo(Serdes.SERDE_STRING.deserialize(stringTab[1]));
                        break;
                    case UPDATE_STATE:
                        player.updateState(Serdes.SERDE_PUBLICGAMESTATE.deserialize(stringTab[1]),
                                Serdes.SERDE_PLAYERSTATE.deserialize(stringTab[2]));
                        break;
                    case SET_INITIAL_TICKETS:
                        player.setInitialTicketChoice(Serdes.SERDE_BAGTICKET
                                .deserialize(stringTab[1]));
                        break;
                    case CHOOSE_INITIAL_TICKETS:
                        send(w, Serdes.SERDE_BAGTICKET.serialize(player.chooseInitialTickets()));
                        break;
                    case NEXT_TURN:
                        send(w, Serdes.SERDE_TURNKIND.serialize(player.nextTurn()));
                        break;
                    case CHOOSE_TICKETS:
                        send(w, Serdes.SERDE_BAGTICKET.serialize(player.chooseTickets(
                                Serdes.SERDE_BAGTICKET.deserialize(stringTab[1]))));
                        break;
                    case DRAW_SLOT:
                        send(w, Serdes.SERDE_INTEGER.serialize(player.drawSlot()));
                        break;
                    case ROUTE:
                        send(w, Serdes.SERDE_ROUTE.serialize(player.claimedRoute()));
                        break;
                    case CARDS:
                        send(w, Serdes.SERDE_BAGCARD.serialize(player.initialClaimCards()));
                        break;
                    case CHOOSE_ADDITIONAL_CARDS:
                        send(w, Serdes.SERDE_BAGCARD.serialize(player.chooseAdditionalCards(
                                Serdes.SERDE_LISTBAGCARD.deserialize(stringTab[1]))));
                        break;

                }


                line = r.readLine();
            }


        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

    }

}
