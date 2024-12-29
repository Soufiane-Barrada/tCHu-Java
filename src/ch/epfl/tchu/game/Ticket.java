package ch.epfl.tchu.game;

import java.util.List;
import java.util.TreeSet;

import ch.epfl.tchu.Preconditions;

/**
 * Un billet.
 *
 * @author Barrada Soufiane(310890)
 * @author Zaaraoui Imane (314994)
 */

public final class Ticket implements Comparable<Ticket> {
    //liste des trajets
    private final List<Trip> trips;
    private final String text;

    /**
     * Construit un billet
     *
     * @param List<Trip> trips
     * @throws IllegalArgumentException si la liste des trajets
     *                                  est vide, ou si toutes les gares de départ des trajets n'ont pas le même nom
     */
    public Ticket(List<Trip> trips) {
        Preconditions.checkArgument(trips.size() > 0);
        String name = trips.get(0).from().name();
        for (int i = 0; i < trips.size(); i++) {
            Preconditions.checkArgument(trips.get(i).from().name().equals(name));
        }
        this.trips = trips;
        text = computeText();
    }

    /**
     * construit un billet constitué d'un unique trajet
     *
     * @param Station from
     * @param Station to
     * @param int     points
     */
    public Ticket(Station from, Station to, int points) {
        this(List.of(new Trip(from, to, points)));

    }

    /**
     * @return représentation textuelle du billet
     */
    public String text() {
        return text;
    }

    /**
     * calcule représentation textuelle du billet
     *
     * @return représentation textuelle
     */
    private String computeText() {
        String text = trips.get(0).from().name() + " - ";
        TreeSet<String> s = new TreeSet<>();

        if (trips.size() == 1) {
            text += String.format(trips.get(0).to().name() + "%s" + trips.get(0).points() + "%s", " (", ")");

        } else {
            for (int i = 0; i < trips.size(); i++) {
                s.add(trips.get(i).to().name() + " (" + trips.get(i).points() + ")");
            }
            text += "{" + String.join(", ", s) + "}";
        }
        return text;
    }

    /**
     * @param StationConnectivity connectivity
     * @returne nombre de points que vaut le billet
     */
    public int points(StationConnectivity connectivity) {
        int n = trips.get(0).points(connectivity);
        int max = n;

        for (int i = 1; i <= trips.size() - 1; i++) {
            n = trips.get(i).points(connectivity);
            if (n > max) {
                max = n;
            }
        }
        return max;
    }

    /**
     * compare le billet this à celui passé en argument
     *
     * @param Ticket that
     * @return entier strictement négatif si this est strictement plus petit que that,
     * un entier strictement positif si this est strictement plus grand que that,
     * et zéro si les deux sont égaux
     */
    public int compareTo(Ticket that) {
        return text.compareTo(that.text);
    }

    /**
     * @return représentation textuelle du billet
     */
    @Override
    public String toString() {
        return text;
    }
}
