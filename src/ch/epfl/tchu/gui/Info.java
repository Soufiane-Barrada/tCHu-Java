package ch.epfl.tchu.gui;

import java.util.List;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Trail;

/**
 * permet de générer les textes décrivant le déroulement de la partie
 *
 * @author Barrada Soufiane(310890)
 * @author Zaaraoui Imane (314994)
 */
public final class Info {
    private String playerName;

    /**
     * @param playerName
     */
    public Info(String playerName) {
        this.playerName = playerName;
    }

    /**
     * retourne le nom de la carte donnée
     *
     * @param card  la carte dont on veut le nom
     * @param count le nombre de cartes
     * @return le nom de la carte donnée
     */
    public static String cardName(Card card, int count) {
        switch (card) {

            case BLACK:
                return StringsFr.BLACK_CARD + StringsFr.plural(count);
            case VIOLET:
                return StringsFr.VIOLET_CARD + StringsFr.plural(count);
            case BLUE:
                return StringsFr.BLUE_CARD + StringsFr.plural(count);
            case GREEN:
                return StringsFr.GREEN_CARD + StringsFr.plural(count);
            case YELLOW:
                return StringsFr.YELLOW_CARD + StringsFr.plural(count);
            case ORANGE:
                return StringsFr.ORANGE_CARD + StringsFr.plural(count);
            case RED:
                return StringsFr.RED_CARD + StringsFr.plural(count);
            case WHITE:
                return StringsFr.WHITE_CARD + StringsFr.plural(count);
            case LOCOMOTIVE:
                return StringsFr.LOCOMOTIVE_CARD + StringsFr.plural(count);
            default:
                throw new IllegalArgumentException();
        }

    }

    /**
     * retourne un message déclarant que les joueurs playerNames
     * ont terminé la partie ex æqo en ayant chacun remporté points
     *
     * @param playerNames les noms des jours
     * @param points      points remporté par les 2 joueurs
     * @return message déclarant que les joueurs ont terminé la partie ex æqo en ayant chacun remporté les points donnés
     */
    public static String draw(List<String> playerNames, int points) {

        return String.format(StringsFr.DRAW, playerNames.get(0) + StringsFr.AND_SEPARATOR + playerNames.get(1), points);
    }

    /**
     * @return un message déclarant que le joueur this jouera en premier
     */
    public String willPlayFirst() {
        return String.format(StringsFr.WILL_PLAY_FIRST, playerName);
    }

    /**
     * @param count nombre de billets gardé
     * @return un message déclarant que le joueur a gardé count
     */
    public String keptTickets(int count) {
        return String.format(StringsFr.KEPT_N_TICKETS, playerName, count, StringsFr.plural(count));
    }

    /**
     * @return un message déclarant que le joueur peut jouer
     */
    public String canPlay() {
        return String.format(StringsFr.CAN_PLAY, playerName);
    }

    /**
     * @param count nombre de billets tirés
     * @return un message déclarant que le joueur a tiré count de billets
     */
    public String drewTickets(int count) {
        return String.format(StringsFr.DREW_TICKETS, playerName, count, StringsFr.plural(count));
    }

    /**
     * @return un message déclarant que le joueur a tiré une carte du sommet de la pioche
     */
    public String drewBlindCard() {
        return String.format(StringsFr.DREW_BLIND_CARD, playerName);
    }

    /**
     * @param card une carte disposée face visible
     * @return un message déclarant que le joueur a tiré card
     */
    public String drewVisibleCard(Card card) {
        return String.format(StringsFr.DREW_VISIBLE_CARD, playerName, cardName(card, 1));
    }

    /**
     * @param route la route dont le joueur s'est emparé
     * @param cards les cartes utilisées pour s'emparé de route
     * @return un message déclarant que le joueur s'est emparé de route donnée au moyen de cards
     */
    public String claimedRoute(Route route, SortedBag<Card> cards) {
        return String.format(StringsFr.CLAIMED_ROUTE, playerName, nomRoute(route), nomsCards(cards));
    }

    /**
     * @param route        route que le joueur veut prendre
     * @param initialCards les cartes initialement données
     * @return un message déclarant que le joueur désire s'emparer de  route
     * en utilisant initialCards
     */
    public String attemptsTunnelClaim(Route route, SortedBag<Card> initialCards) {

        return String.format(StringsFr.ATTEMPTS_TUNNEL_CLAIM, playerName, nomRoute(route), nomsCards(initialCards));
    }

    /**
     * @param drawnCards     les trois cartes additionnelles tiré par le joueur
     * @param additionalCost coût additionel du nombre de cartes donné
     * @return un message déclarant que le joueur a tiré drawnCards et qu'elles impliquent
     * un coût additionel du nombre de additionalCost
     */
    public String drewAdditionalCards(SortedBag<Card> drawnCards, int additionalCost) {
        if (additionalCost == 0) {
            return String.format(StringsFr.ADDITIONAL_CARDS_ARE, nomsCards(drawnCards)) +
                    StringsFr.NO_ADDITIONAL_COST;
        } else {
            return String.format(StringsFr.ADDITIONAL_CARDS_ARE, nomsCards(drawnCards)) +
                    String.format(StringsFr.SOME_ADDITIONAL_COST, additionalCost, StringsFr.plural(additionalCost));
        }
    }

    /**
     * @param route tunnel que la joueur n'a pas pris
     * @return un message déclarant que le joueur n'a pas pu (ou voulu) s'emparer de route
     */
    public String didNotClaimRoute(Route route) {
        return String.format(StringsFr.DID_NOT_CLAIM_ROUTE, playerName, nomRoute(route));
    }

    /**
     * @param carCount nombre de wagons restant au joueur. ( Inferieur ou égale à 2)
     * @return un message déclarant que le joueur n'a plus que carCount (et inférieur ou égale à 2) de wagons,
     * et que le dernier tour commence donc
     */
    public String lastTurnBegins(int carCount) {
        return String.format(StringsFr.LAST_TURN_BEGINS, playerName, carCount, StringsFr.plural(carCount));
    }

    /**
     * @param longestTrail est le plus long chemin ou l'un des plus que possede le joueur
     * @return le message déclarant que le joueur (this) obtient le bonus de fin de partie grâce a longestTrail
     */
    public String getsLongestTrailBonus(Trail longestTrail) {
        return String.format(StringsFr.GETS_BONUS, playerName, longestTrail.station1() + StringsFr.EN_DASH_SEPARATOR +
                longestTrail.station2());
    }

    /**
     * @param points      nombre de points du joueur gagnant
     * @param loserPoints nombre de points du joueur perdant
     * @ un message déclarant que le joueur this remporte la partie avec points,
     * son adversaire n'en ayant obtenu que loserPoints
     */
    public String won(int points, int loserPoints) {
        return String.format(StringsFr.WINS, playerName, points, StringsFr.plural(points), loserPoints,
                StringsFr.plural(loserPoints));
    }


    private static String nomRoute(Route route) {
        return route.station1().name() + StringsFr.EN_DASH_SEPARATOR + route.station2().name();
    }

    public static String nomsCards(SortedBag<Card> cards) {
        String s = "";
        int i = 0;
        for (Card c : cards.toSet()) {
            int n = cards.countOf(c);
            if (i == 0) {
                s = n + " " + cardName(c, n);
                i++;
            } else if (i == cards.toSet().size() - 1) {
                s += StringsFr.AND_SEPARATOR + n + " " + cardName(c, n);
            } else {
                s = s + "," + " " + n + " " + cardName(c, n);
                i++;
            }
        }
        return s;
    }


}
