package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

/**
 * Une gare
 *
 * @author Barrada Soufiane(310890)
 * @author Zaaraoui Imane (314994)
 */
public final class Station {
    //L'identité de la gare
    private final int id;
    private final String name;

    /**
     * Construit une gare
     *
     * @param int id, String name
     * @throws IllegalArgumentException si le numéro
     *                                  d'identification est strictement négatif
     */
    public Station(int id, String name) {
        Preconditions.checkArgument(id >= 0);

        this.id = id;
        this.name = name;

    }

    /**
     * @return identité de la gare
     */
    public int id() {
        return id;
    }

    /**
     * @return nom de la gare
     */
    public String name() {
        return name;
    }

    /**
     * @return nom de la gare
     */
    public String toString() {
        return name;
    }


}
