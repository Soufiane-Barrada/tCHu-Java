package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

/**
 * Une route.
 *
 * @author Barrada Soufiane (310890)
 * @author Zaaraoui Imane (314994)
 */

public final class Route {
    /**
     * Niveau du chemin Route
     * qui peut etre soit chemin en surface(OVERGROUND) soit un chemin
     * en tunnel (UNDERGROUND)
     */
    public enum Level {
        /*
         * … valeurs du type énuméré
         * */
        OVERGROUND,
        UNDERGROUND;

    }

    // tableau contenat le nombre de point attribué
    // à chaque longueur de route
    // points de construction
    private static final int[] tabPoints = {1, 2, 4, 7, 10, 15};
    //L'identité de la route
    private final String id;
    private final Station station1;
    private final Station station2;
    private final int length;
    private final Level level;
    private final Color color;

    /**
     * Construit une route avec
     *
     * @param String id, Station station1, Station station2,
     *               int length, Level level, Color color
     * @throws IllegalArgumentException si les stations de la route
     *                                  sont différentes ou si la longueur de la route n'est pas comprise
     *                                  entre 0 et 6
     * @throws NullPointerException     si l'un de ces attributs ou plus station1, station2 ,length , level
     *                                  est null
     */
    public Route(String id, Station station1, Station station2, int length, Level level, Color color) {
        Preconditions.checkArgument(!(station1.equals(station2)));
        Preconditions.checkArgument(length >= Constants.MIN_ROUTE_LENGTH && length <= Constants.MAX_ROUTE_LENGTH);
        this.id = Objects.requireNonNull(id);
        this.station1 = Objects.requireNonNull(station1);
        this.station2 = Objects.requireNonNull(station2);
        this.length = Objects.requireNonNull(length);
        this.level = Objects.requireNonNull(level);
        this.color = color;

    }

    /**
     * retourne la liste des deux gares de la route,
     * dans l'ordre dans lequel elles ont été passées au constructeur
     *
     * @return list des stations associées à la route
     */
    public List<Station> stations() {
        List<Station> list = new ArrayList<Station>();
        list.add(station1);
        list.add(station2);
        return list;
    }

    /**
     * retourne la gare de la route qui n'est pas celle donnée en argument,
     *
     * @param Station station
     * @return l'autre station associée à la route
     * @throws IllegalArgumentException la gare station
     *                                  n'est ni la première ni la seconde gare de la route
     */
    public Station stationOpposite(Station station) {
        Preconditions.checkArgument(station1.equals(station) || station2.equals(station));
        if (station1.equals(station)) return station2;
        else return station1;
        
      }

    /**
     * retourne la liste de tous les ensembles de cartes qui pourraient être joués pour
     * s'emparer de la route dans l'ordre croissant de nombre de cartes locomotive, puis par couleur
     *
     * @return liste  de tous les ensembles de cartes qui pourraient être joués
     * pour prendre la route
     */
    public List<SortedBag<Card>> possibleClaimCards() {
        List<SortedBag<Card>> list = new ArrayList<SortedBag<Card>>();
        if (level.equals(level.OVERGROUND) && !(color == null)) {
            list.add(SortedBag.of(length, Card.of(color)));

        }
        if (level.equals(level.UNDERGROUND) && !(color == null)) {
            list.add(SortedBag.of(length, Card.of(color)));
            for (int i = 1; i <= length; i++) {

                list.add(SortedBag.of(length - (i), Card.of(color), i, Card.LOCOMOTIVE));

            }

        }
        if (level.equals(level.OVERGROUND) && color == null) {
            for (int i = 0; i <= Color.COUNT - 1; i++) {
                list.add(SortedBag.of(length, Card.CARS.get(i)));
            }
        }
        if (level.equals(level.UNDERGROUND) && color == null) {
            for (int i = 0; i <= Color.COUNT - 1; i++) {
                list.add(SortedBag.of(length, Card.CARS.get(i)));
            }
            for (int i = 1; i <= length; i++) {
                for (int j = 0; j <= Card.CARS.size() - 1; j++) {
                    if (i == length) {
                        list.add(SortedBag.of(i, Card.LOCOMOTIVE));
                        break;
                    } else {
                        list.add(SortedBag.of(length - (i), Card.CARS.get(j), i, Card.LOCOMOTIVE));
                    }
                }
            }
        }
        return list;

    }

    /**
     * retourne le nombre de cartes additionnelles à jouer pour s'emparer de la route (en tunnel), sachant que le joueur
     * a initialement posé les cartes claimCards et que les trois cartes tirées du sommet de la pioche sont drawnCards
     *
     * @param claimCards
     * @param drawnCards
     * @return nomdre des cartes additionnelles
     * @throws IllegalArgumentException si la route à laquelle on l'applique n'est pas un tunnel, ou
     * @throws drawnCards               ne contient pas exactement 3 cartes
     */
    public int additionalClaimCardsCount(SortedBag<Card> claimCards, SortedBag<Card> drawnCards) {
        Preconditions.checkArgument((this.level).equals(Level.UNDERGROUND));
        Preconditions.checkArgument(drawnCards.size() == Constants.ADDITIONAL_TUNNEL_CARDS);
        int k = 0;
        for (int i = 0; i <= Constants.ADDITIONAL_TUNNEL_CARDS - 1; i++) {
            for (int j = 0; j <= claimCards.size() - 1; j++) {
                if (drawnCards.get(i) == claimCards.get(j)) {
                    k++;
                    break;
                }
                if (drawnCards.get(i) == Card.LOCOMOTIVE) {
                    k++;
                    break;
                }
            }
        }
        return k;

    }

    /**
     * retourne  le nombre de points de construction
     *
     * @return nombre points de construction
     */
    public int claimPoints() {
        return tabPoints[length - 1];
    }

    /**
     * @return identité de la route
     */
    public String id() {
        return id;
    }

    /**
     * @return première gare de la route
     */
    public Station station1() {
        return station1;
    }

    /**
     * @return deuxième gare de la route
     */
    public Station station2() {
        return station2;
    }

    /**
     * @return longueur de la route
     */
    public int length() {
        return length;
    }

    /*retourne le niveau auquel se trouve la route
     * @return niveau associé à la route
     */
    public Level level() {
        return level;
    }

    public Color color() {
        return color;
    }

}
