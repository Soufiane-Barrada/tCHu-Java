package ch.epfl.tchu.game;

import java.util.List;

/**
 * Une couleur. Huit couleurs utilisées dans le jeu pour colorer les cartes
 * wagon et les routes
 *
 * @author Barrada Soufiane(310890)
 * @author Zaaraoui Imane (314994)
 */

public enum Color {

    BLACK("noir"), VIOLET("violet"), BLUE("bleu"), GREEN("vert"), YELLOW("jaune"), ORANGE("orange"), RED("rouge"),
    WHITE("blanc");

    // liste immuable contenant la totalité des valeurs du type énuméré,
    // dans leur ordre de définition
    public static final List<Color> ALL = List.of(Color.values());
    //le nombre de valeurs du type énuméré
    public static final int COUNT = ALL.size();
    private String couleur;

    private Color(String couleur) {
        this.couleur = couleur;
    }

}
