package ch.epfl.tchu.game;

import java.util.List;

import java.util.List;

/**
 * Une carte.
 *
 * @author Barrada Soufiane(310890)
 * @author Zaaraoui Imane (314994)
 */
public enum Card {
    BLACK("wagon noir", Color.BLACK), VIOLET("wagon violet", Color.VIOLET), BLUE("wagon bleu", Color.BLUE),
    GREEN("wagon vert", Color.GREEN), YELLOW("wagon jaune", Color.YELLOW), ORANGE("wagon orange", Color.ORANGE),
    RED("wagon rouge", Color.RED), WHITE("wagon blanc", Color.WHITE), LOCOMOTIVE("locomotive", null);
    //liste immuable contenant la totalité des valeurs du type énuméré,
    //dans leur ordre de définition
    public static final List<Card> ALL = List.of(Card.values());
    //le nombre de valeurs du type énuméré
    public static final int COUNT = ALL.size();
    //contient les cartes wagon, dans l'ordre de définition,  de BLACK à WHITE.
    public static final List<Card> CARS = ALL.subList(0, COUNT - 1);
    private String type;
    private Color couleur;

    /**
     * construit une carte caractérisé par sa couleur
     * et une chaine de caractère représentant son type
     *
     * @param String type
     * @param Color  couleur
     */

    private Card(String type, Color couleur) {
        this.type = type;
        this.couleur = couleur;
    }

    /**
     * @param Color color
     * @return le type de carte wagon correspondant à la couleur donnée.
     */
    public static Card of(Color color) {
        switch (color) {
            case BLACK:
                return Card.BLACK;

            case VIOLET:
                return Card.VIOLET;
            case BLUE:
                return Card.BLUE;
            case GREEN:
                return Card.GREEN;
            case YELLOW:
                return Card.YELLOW;
            case ORANGE:
                return Card.ORANGE;
            case RED:
                return Card.RED;
            case WHITE:
                return Card.WHITE;
            default:
                throw new IllegalArgumentException();// A REVOIR
        }
    }

    /**
     * retourne la couleur du type de carte  s'il s'agit d'un type wagon,
     * ou null s'il s'agit du type locomotive
     *
     * @return
     */
    public Color color() {
        return couleur;
    }

}
