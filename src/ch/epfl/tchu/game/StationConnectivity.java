package ch.epfl.tchu.game;

/**
 * Une connectivité du réseau.
 *
 * @author Barrada Soufiane (310890)
 * @author Zaaraoui Imane (314994)
 */


public interface StationConnectivity {
    /***
     * méthode publique et abstraite
     * @param s1
     * @param s2
     * @return vrai si les gares sont reliées par le réseau du joueur.
     */
    public abstract boolean connected(Station s1, Station s2);


}
