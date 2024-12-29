package ch.epfl.tchu.net;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.regex.Pattern;
/**
 * représente un objet sérialisant et désérialisant des valeurs d'un type donné.
 * @param <T>
 * @author Barrada Soufiane(310890)
 * @author Zaaraoui Imane (314994)
 */
public interface Serde<T> {
    /**
     * retourne la chaîne correspondante à l'objet à sérialiser
     * @param ser l'objet à sérialiser
     * @return la chaîne correspondante à cet objet
     */
     String serialize(T ser);
    /**
     * retourne l'objet correspondant à la chaîne à desérialiser
     * @param dser chaîne à désérialiser
     * @return l'objet correspondant
     */
     T deserialize(String dser);
    /**
     * retourne serde correspondant aux fonctions de sérialisation et de désérialisation
     * @param ser fonction de sérialisation
     * @param dser fonction de désérialisation
     * @return serde correspondant aux deux fonctions
     */
     static <S> Serde<S> of(Function<S, String> ser, Function<String, S> dser) {
        return new Serde<>() {
            @Override
            public String serialize(S seri) {
                return ser.apply(seri);
            }

            @Override
            public S deserialize(String seri) {

                return dser.apply(seri);
            }
        };
    }
    /**
     * retourne le serde correspondant à une liste de valeurs énuméré
     * @param l la liste de toutes les valeurs de valeurs énuméré
     * @return le serde correspondant à cette liste
     */
     static <S> Serde<S> oneOf(List<S> l) {

        return new Serde<>() {
            @Override
            public String serialize(S s) {
                Preconditions.checkArgument(l.indexOf((s)) != -1);
                return String.valueOf(l.indexOf(s));
            }

            @Override
            public S deserialize(String d) {
                return l.get(Integer.parseInt(d));
            }
        };
    }
    /**
     * retourne un serde (dé)sérialisant des listes de valeurs (dé)sérialisées par le serde donné
     * @param ss un serde
     * @param cs caractère de séparation
     * @return serde (dé)sérialisant des listes de valeurs (dé)sérialisées par le serde ss
     */
     static <S> Serde<List<S>> listOf(Serde<S> ss, String cs) {
        return new Serde<>() {
            @Override
            public String serialize(List<S> s) {

                StringJoiner j = new StringJoiner(cs);
                for (S list : s) {
                    j.add(ss.serialize(list));

                }
                return j.toString();
            }

            @Override
            public List<S> deserialize(String d) {
                List<S> listDeserialized = new ArrayList<>();

                if (!d.isEmpty()) {
                    String[] chaines = d.split(Pattern.quote(cs), -1);
                    for (String s : chaines) {
                        listDeserialized.add(ss.deserialize(s));

                    }
                }
                return listDeserialized;

            }
        };
    }
    /**
     * retourne un serde (dé)sérialisant des multiensembles de valeurs (dé)sérialisées par le serde donné
     * @param ss  un serde
     * @param cs caractère de séparation
     * @return serde (dé)sérialisant des multiensembles de valeurs (dé)sérialisées par le serde ss
     */

     static <S extends Comparable<S>> Serde<SortedBag<S>> bagOf(Serde<S> ss, String cs) {
        Serde<List<S>> listSerde = Serde.listOf(ss, cs);
        return Serde.of(
                sortedBag -> listSerde.serialize(sortedBag.toList()),
                str -> SortedBag.of(listSerde.deserialize(str))
        );
    }
}


