package ch.epfl.tchu.game;

import java.util.List;

/**
 * Une couleur. Huit couleurs utilisées dans le jeu pour colorer les cartes
 * wagon et les routes
 *
 * @author Barrada Soufiane(310890)
 * @author Zaaraoui Imane (314994)
 */
public enum PlayerId {

    PLAYER_1(), PLAYER_2();


    public static final List<PlayerId> ALL = List.of(PlayerId.values());

    // liste immuable contenant la totalité des valeurs du type énuméré,
    // dans leur ordre de définition
    public static final int COUNT = ALL.size();

    /**
     * @return le PlayerId qui suit this ( dans l'ordre de ALL)
     */
    public PlayerId next() {
        return (this == PLAYER_1) ? PLAYER_2 : PLAYER_1;
    }
}
