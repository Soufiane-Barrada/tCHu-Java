package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.*;

/**
 * PlayerState
 * classe immuable
 * représente l'état complet d'un joueur
 *
 * @author Barrada Soufiane (310890)
 * @author Zaaraoui Imane (314994)
 */
public final class PlayerState extends PublicPlayerState {

    private final SortedBag<Ticket> tickets;
    private final SortedBag<Card> cards;

    /**
     * construit l'état d'un joueur
     *
     * @param tickets
     * @param cards
     * @param routes
     */
    public PlayerState(SortedBag<Ticket> tickets, SortedBag<Card> cards, List<Route> routes) {

        super(tickets.size(), cards.size(), routes);
        this.tickets = tickets;
        this.cards = cards;
    }

    /**
     * retourne l'état initial d'un joueur auquel les cartes initiales données ont été distribuées
     *
     * @param initialCards
     * @return l'état initial d'un joueur
     * @throws si le nombre de cartes initiales ne vaut pas 4,
     */
    public static PlayerState initial(SortedBag<Card> initialCards) {
        Preconditions.checkArgument(initialCards.size() == 4);
        return new PlayerState(SortedBag.<Ticket>of(), initialCards, new ArrayList<Route>());
    }

    /**
     * @return les billets du joueur
     */
    public SortedBag<Ticket> tickets() {
        return tickets;
    }

    /**
     * @return les cartes wagon/locomotive du joueur,
     */
    public SortedBag<Card> cards() {
        return cards;
    }

    /**
     * retourne un état identique au récepteur,
     * si ce n'est que le joueur possède en plus les billets newTickets,
     *
     * @param newTickets
     * @return état du joueur avec newTickets en plus
     */
    public PlayerState withAddedTickets(SortedBag<Ticket> newTickets) {
        return new PlayerState(tickets.union(newTickets), cards, routes());
    }

    /**
     * un état identique au récepteur, si ce n'est que le joueur possède
     * en plus une carte additionnelle,
     *
     * @param card
     * @return playerState avec la carte card card en plus
     */
    public PlayerState withAddedCard(Card card) {

        return new PlayerState(tickets, cards.union(SortedBag.of(card)), routes());
    }

    /**
     * un état identique au récepteur, si ce n'est que le joueur possède
     * des cates additionnelles
     *
     * @param additionalCards
     * @return playerState avec les cartes  additionalCards en plus
     */
    public PlayerState withAddedCards(SortedBag<Card> additionalCards) {
        return new PlayerState(tickets, cards.union(additionalCards), routes());
    }

    /**
     * retourne vrai ssi le joueur peut s'emparer de la route donnée en argument
     *
     * @param route
     * @return vrai ssi le joueur peut s'emparer de route
     */
    public boolean canClaimRoute(Route route) {

        if (route.length() <= carCount()) {
            List<SortedBag<Card>> list = possibleClaimCards(route);
            return list.size() >= 1;
        }
        return false;
    }

    /**
     * retourne la liste des ensembles de cartes que le joueur pourrait utiliser pours'emparer de la route donnée
     *
     * @param route
     * @return la liste des ensembles de cartes pour s'emparer de route
     * @throws IllegalArgumentException si le joueur n'a pas assez de wagons pour s'emparer de la route,
     */
    public List<SortedBag<Card>> possibleClaimCards(Route route) {
        Preconditions.checkArgument(carCount() >= route.length());
        List<SortedBag<Card>> listCartes = route.possibleClaimCards();
        List<SortedBag<Card>> listPossible = new ArrayList<>(listCartes);
        for (SortedBag<Card> bag : listCartes) {
            if (!cards.contains(bag)) {
                listPossible.remove(bag);
            }

        }
        return listPossible;
    }

    /**
     * retourne la liste des ensembles de cartes pour s'emparer d'un tunnel trié par ordre
     * croissant du nombre de cartes locomotives,
     *
     * @param additionalCardsCount
     * @param initialCards         cartes initiales
     * @param drawnCards           cartes tirées
     * @return la liste des ensembles de cartes pour s'emparer d'un tunnel
     * @throws IllegalArgumentException si le nombre de cartes additionnelles n'est pas compris entre 1 et 3 (inclus),
     *                                  si l'ensemble des cartes initiales est vide ou contient plus de 2 types de cartes différents, ou
     *                                  si l'ensemble des cartes tirées ne contient pas exactement 3 cartes,
     */
    public List<SortedBag<Card>> possibleAdditionalCards(int additionalCardsCount, SortedBag<Card> initialCards,
                                                         SortedBag<Card> drawnCards) {
        Preconditions.checkArgument(additionalCardsCount >= 1 && additionalCardsCount <= 3
                && !(initialCards.isEmpty()) && initialCards.toMap().size() <= 2 && drawnCards.size() == 3);

        SortedBag.Builder<Card> usableCardsBuilder = new SortedBag.Builder<Card>();

        SortedBag<Card> usableCards;


        for (Card card : cards) {
            if (initialCards.contains(card) || card == Card.LOCOMOTIVE) {
                usableCardsBuilder.add(card);
            }
        }
        usableCards = usableCardsBuilder.build().difference(initialCards);

        List<SortedBag<Card>> listSousEnsemble;
        if (usableCards.size() >= additionalCardsCount) {
            listSousEnsemble = new ArrayList<>(usableCards.subsetsOfSize(additionalCardsCount));

            listSousEnsemble.sort(Comparator.comparingInt(cs -> cs.countOf(Card.LOCOMOTIVE)));
        } else {
            listSousEnsemble = List.of();
        }

        return listSousEnsemble;
    }

    public PlayerState withClaimedRoute(Route route, SortedBag<Card> claimCards) {
        List<Route> rouutes = new ArrayList<>(routes());
        rouutes.add(route);
        return new PlayerState(tickets, cards.difference(claimCards), rouutes);
    }

    /**
     * @return nombre de points obtenus par les billets
     */
    public int ticketPoints() {

        int maxID = 0;
        for (Route r : routes()) {
            if (maxID < r.station1().id() || maxID < r.station2().id()) {
                maxID = r.station1().id() > r.station2().id() ? r.station1().id() : r.station2().id();
            }
        }
        StationPartition sp;
        StationPartition.Builder spb = new StationPartition.Builder(maxID + 1);
        int points = 0;


        for (Route r : routes()) {
            spb.connect(r.station1(), r.station2());
        }
        sp = spb.build();
        for (Ticket t : tickets) {
            points += t.points(sp);
        }
        return points;
    }

    /**
     * @return totalité des points obtenus à la fin de la partie,
     */
    public int finalPoints() {
        return claimPoints() + ticketPoints();
    }

}


