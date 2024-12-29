package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.List;

/**
 * PublicPlayerState
 * immuable
 * représente la partie publique de l'état d'un joueur
 *
 * @author Barrada Soufiane (310890)
 * @author Zaaraoui Imane (314994)
 */
public class PublicPlayerState {

    private final int ticketCount;
    private final int cardCount;
    private final List<Route> routes;
    private final int carCount;
    private final int constructionPoints;

    /**
     * construit l'état publique d'un joueur
     *
     * @param ticketCount
     * @param cardCount
     * @param routes
     * @throws IllegalArgumentException si le nombre de billets ou le nombre de cartes est strictement négatif
     */
    public PublicPlayerState(int ticketCount, int cardCount, List<Route> routes) {

        Preconditions.checkArgument(ticketCount >= 0 && cardCount >= 0);

        this.ticketCount = ticketCount;
        this.cardCount = cardCount;
        this.routes = List.copyOf(routes);
        int routesLength = 0;
        int points = 0;
        for (Route r : routes) {
            routesLength += r.length();
            points += r.claimPoints();
        }
        carCount = Constants.INITIAL_CAR_COUNT - routesLength;
        constructionPoints = points;
    }

    /**
     * @return nombre de billets que possède le joueur
     */
    public int ticketCount() {
        return ticketCount;
    }

    /**
     * @return nombre de cartes que possède le joueur
     */
    public int cardCount() {
        return cardCount;
    }

    /**
     * @return les routes dont le joueur s'est emparé
     */
    public List<Route> routes() {
        return routes;
    }

    /**
     * @return nombre de wagons que possède le joueur
     */
    public int carCount() {

        return carCount;
    }

    /**
     * @return nombre de points de construction obtenus par le joueur.
     */
    public int claimPoints() {

        return constructionPoints;
    }
}
