package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Ticket;
/**
 * contient cinq interfaces fonctionnelles imbriqués representant des gestionnaires d'actions
 *
 * @author Barrada Soufiane(310890)
 * @author Zaaraoui Imane (314994)
 */
public interface ActionHandlers {
    @FunctionalInterface
    interface DrawTicketsHandler{
        /**
         * appeler lorsque le joueur désire tirer des billets
         */
        void onDrawTickets();
    }
    @FunctionalInterface
    interface DrawCardHandler{
        /**
         * appeler lorsque le joueur désire tirer une carte de l'emplacement donné
         * @param d numéro d'emplacement de la carte. (entre -1 et 4, ou -1 pour la pioche)
         */
        void onDrawCard(int d);
    }
    @FunctionalInterface
    interface ClaimRouteHandler{
        /**
         * appeler lorsque le joueur désire s'emparer de la route donnée au moyen des cartes (initialCards) données
         * @param route la route dont on veut s'emparer
         * @param initialCards les cartes initiales utilisées pour s'emparer de la route
         */
        void onClaimRoute(Route route, SortedBag<Card> initialCards);
    }
    @FunctionalInterface
    interface ChooseTicketsHandler{
        /**
         * appeler lorsque le joueur a choisi de garder les billets (chosenTickets) suite à un tirage de billets
         * @param chosenTickets les billets à garder
         */
        void onChooseTickets( SortedBag<Ticket> chosenTickets);
    }
    @FunctionalInterface
    interface ChooseCardsHandler{
        /**
         * appeler lorsque le joueur a choisi d'utiliser les cartes données comme
         * cartes initiales ou additionnelles lors de la prise de possession d'une route
         * @param chosenCards les cartes choisies (le multiensemble peut etre vide s'il s'agit de carte additionnelles
         *                    lors de la prise de possession d'une route)
         */
        void onChooseCards( SortedBag<Card> chosenCards);
    }
}
