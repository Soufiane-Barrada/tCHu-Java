package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ch.epfl.tchu.Preconditions;

/**
 * Un trajet.
 *
 * @author Barrada Soufiane(310890)
 * @author Zaaraoui Imane (314994)
 */
public final class Trip {

    private final Station from;
    private final Station to;
    private final int points;

    /**
     * construit un nouveau trajet
     *
     * @param Station from
     * @param Station to
     * @param int     points
     * @throws NullPointerException     si l'une des deux gares est nulle
     * @throws IllegalArgumentException si le nombre de points est inférieur
     *                                  ou égal à 0
     */
    public Trip(Station from, Station to, int points) {
        Preconditions.checkArgument(points > 0);

        this.from = Objects.requireNonNull(from);
        this.to = Objects.requireNonNull(to);
        this.points = points;
    }

    /**
     * retourne la liste de tous les trajets possibles allant d'une des gares de la première liste (from)
     * à l'une des gares de la seconde liste (to)
     *
     * @param List<Station> from
     * @param List<Station> to
     * @param int           points
     * @return liste de tous les trajets possibles de from à to
     * @throws IllegalArgumentException si l'une des listes est vide,
     *                                  si le nombre de points n'est pas strictement positif
     */
    public static List<Trip> all(List<Station> from, List<Station> to, int points) {
        Preconditions.checkArgument(points > 0 && from.size() > 0 && to.size() > 0);
        List<Trip> trips = new ArrayList<Trip>();
        for (int i = 0; i < from.size(); i++) {
            for (int j = 0; j < to.size(); j++) {
                trips.add(new Trip(from.get(i), to.get(j), points));
            }
        }
        return trips;
    }

    /**
     * @return gare départ du trajet
     */
    public Station from() {
        return from;
    }

    /**
     * @return gare arrivée du trajet
     */
    public Station to() {
        return to;
    }

    /**
     * @return nombre de points du trajet
     */
    public int points() {
        return points;
    }

    /**
     * @param StationConnectivity connectivity
     * @return nombre de points du trajet pour
     * la connectivité donnée
     */
    public int points(StationConnectivity connectivity) {
        if (connectivity.connected(from, to)) {
            return points;
        } else
            return -points;
    }
}
