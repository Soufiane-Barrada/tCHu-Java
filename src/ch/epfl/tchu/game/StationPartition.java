package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.List;

/**
 * StationPartition
 * immuable
 * représente une partition de gares
 *
 * @author Barrada Soufiane (310890)
 * @author Zaaraoui Imane (314994)
 */
public final class StationPartition implements StationConnectivity {

    private final int[] integerList;

    /**
     * constrructeur privéé
     * construit une partion de gare
     *
     * @param integerList
     */
    private StationPartition(int[] integerList) {

        this.integerList = integerList;
    }

    /**
     * @param s1
     * @param s2
     * @return vrai si les gares sont reliées par le réseau du joueur
     */
    @Override
    public boolean connected(Station s1, Station s2) {
        if (s1.id() > integerList.length - 1 || s2.id() > integerList.length - 1) {
            return s1.id() == s2.id();
        } else {
            return integerList[s1.id()] == integerList[s2.id()] ? true : false;
        }

    }

    /**
     * @author Barrada Soufiane (310890)
     * @author Zaaraoui Imane (314994)
     * représente un bâtisseur de partition de gare
     */
    public static final class Builder {

        private int[] integerListBuilder;
        private int stationCount;

        /**
         * construit un bâtisseur de partition d'un ensemble de gares dont l'identité est comprise
         * entre 0 (inclus) et stationCount (exclus),
         *
         * @param stationCount
         * @throws si stationCount est strictement négatif
         */
        public Builder(int stationCount) {

            Preconditions.checkArgument(stationCount >= 0);

            integerListBuilder = new int[stationCount];
            for (int iz = 0; iz < stationCount; iz++) {
                integerListBuilder[iz] = iz;
            }
            this.stationCount = stationCount;
        }

        /**
         * @param id
         * @return numéro d'identification du représentant du sous ensumble
         * contenant la gare du numéro d'identification "id"
         */
        private int representative(int id) {


            while (id != integerListBuilder[id]) {
                id = integerListBuilder[id];
            }
            return integerListBuilder[id];
        }

        /**
         * joint les sous-ensembles contenant les deux gares passées en argument, en mettant l'un des deux représentants comme
         * représentant du sous-ensemble joint
         *
         * @param s1
         * @param s2
         * @return le batisseur
         */
        public Builder connect(Station s1, Station s2) {

            integerListBuilder[representative(s1.id())] = representative(s2.id());
            return this;
        }

        /**
         * @return la partition aplatie des gares
         */
        public StationPartition build() {

            int m = 0;
            for (int iz = 0; iz < stationCount; iz++) {
                m = representative(iz);

                integerListBuilder[iz] = m;
            }
            return new StationPartition(integerListBuilder);
        }
    }

}
