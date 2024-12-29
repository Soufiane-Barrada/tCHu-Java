package ch.epfl.tchu.game;

import java.util.ArrayList;

import java.util.List;

/**
 * Un chemin.
 *
 * @author Barrada Soufiane(310890)
 * @author Zaaraoui Imane (314994)
 */
public final class Trail {
    private final Station stationDeDepart;
    private final Station stationArrivee;
    private final List<Route> routes;
    private final int nbRoutes;

    /**
     * Construit un chemin avec la liste des routes données ainsi que la station de
     * départ et la station d'arrivée et le nombre de routes qui constituent le
     * chemin
     *
     * @param List<Route> routes, Station2 stationArrivee, IllegalArgumentException,
     *                    Station2 stationDeDeppart,Station2 stationArrivee, int
     *                    nbRoutes
     */
    private Trail(List<Route> routes, Station stationDeDeppart, Station stationArrivee, int nbRoutes) {
        this.routes = routes;
        this.stationDeDepart = stationDeDeppart;
        this.stationArrivee = stationArrivee;
        this.nbRoutes = nbRoutes;
    }

    /**
     * @param List<Route> routes,
     * @return le plus long chemin du résaux de routes
     */

    public static Trail longest(List<Route> routes) {
        if (routes.size() == 0) {
            return new Trail(routes, null, null, 0);
        }
        List<Trail> cs = new ArrayList<Trail>();
        List<Route> rs;
        List<Route> copyOfRoutes;
        List<Trail> allPossibleTrails = new ArrayList<Trail>();
        for (int i = 0; i <= routes.size() - 1; i++) {
            // copyOfRoutes=copyArray(routes);
            // copyOfRoutes.remove(routes.get(i));
            allPossibleTrails
                    .add(new Trail(List.of(routes.get(i)), routes.get(i).station1(), routes.get(i).station2(), 1));
            allPossibleTrails
                    .add(new Trail(List.of(routes.get(i)), routes.get(i).station2(), routes.get(i).station1(), 1));
            cs.add(new Trail(List.of(routes.get(i)), routes.get(i).station1(), routes.get(i).station2(), 1));
            cs.add(new Trail(List.of(routes.get(i)), routes.get(i).station2(), routes.get(i).station1(), 1));
        }
        while (cs.size() != 0) {
            List<Trail> csPrime = new ArrayList<Trail>();

            for (int i = 0; i <= cs.size() - 1; i++) {
                rs = new ArrayList<>(routes);
                rs.removeAll(cs.get(i).routes);
                List<Route> toRemove = new ArrayList<Route>();
                for (int j = 0; j <= rs.size() - 1; j++) {
                    if (!(rs.get(j).station1().equals(cs.get(i).stationArrivee)
                            || rs.get(j).station2().equals(cs.get(i).stationArrivee))) {
                        toRemove.add(rs.get(j));
                    }
                    for (int l = 0; l <= cs.get(i).routes.size() - 1; l++) {
                        if (cs.get(i).routes.get(l).equals(rs.get(j))) {
                            toRemove.add(rs.get(j));
                        }
                    }
                }

                rs.removeAll(toRemove);
                for (int j = 0; j <= rs.size() - 1; j++) {
                    copyOfRoutes = new ArrayList<>(cs.get(i).routes);
                    copyOfRoutes.add(rs.get(j));
                    csPrime.add(new Trail(copyOfRoutes, cs.get(i).stationDeDepart,
                            rs.get(j).stationOpposite(cs.get(i).stationArrivee), cs.get(i).nbRoutes + 1));
                    allPossibleTrails.add(new Trail(copyOfRoutes, cs.get(i).stationDeDepart,
                            rs.get(j).stationOpposite(cs.get(i).stationArrivee), cs.get(i).nbRoutes + 1));
                }

            }
            cs = csPrime;

        }
        int max = 0;
        int indiceMax = 0;
        for (int i = 0; i <= allPossibleTrails.size() - 1; i++) {
            if (allPossibleTrails.get(i).length() >= max) {
                max = allPossibleTrails.get(i).length();
                indiceMax = i;
            }

        }
        return allPossibleTrails.get(indiceMax);
    }


    /**
     * retourne un nombre entier qui représente la longueur du chemin
     *
     * @return la longueur du chemin
     */
    public int length() {
        int k = 0;
        for (int i = 0; i <= nbRoutes - 1; i++) {
            k += routes.get(i).length();

        }
        return k;

    }

    /**
     * retourne la station de départ du chemin ou null si le chemin est de longueur
     * zéro
     *
     * @return la station de départ
     */
    public Station station1() {
        if (this.nbRoutes == 0)
            return null;
        else
            return stationDeDepart;
    }

    /**
     * retourne la station d'arrivée du chemin ou null si le chemin est de longueur
     * zéro
     *
     * @return la station d'arrivée
     */
    public Station station2() {
        if (this.nbRoutes == 0)
            return null;
        else
            return stationArrivee;
    }

    /**
     * vérifie d'abord si la longueur du chemin est zéro retourne une représentation
     * textuelle du chemin
     *
     * @return la String qui représente le chemin
     */
    @Override
    public String toString() {
        if (nbRoutes == 0) {
            return "Chemin Vide";
        }

        List<String> s = new ArrayList<String>();
        s.add(stationDeDepart.name());
        Station station = stationDeDepart;
        for (int i = 0; i <= routes.size() - 2; i++) {
            station = routes.get(i).stationOpposite(station);
            s.add(station.name());

        }
        s.add(stationArrivee.name());

        String text = "";
        for (int i = 0; i < s.size() - 1; i++) {
            //text = String.format(" - ", text, s.get(i));
            text += s.get(i) + " - ";
        }
        text += s.get(s.size() - 1);
        text += " (" + this.length() + ")";
        return text;

    }

}
