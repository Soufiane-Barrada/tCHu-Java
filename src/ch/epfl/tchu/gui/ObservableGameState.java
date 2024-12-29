package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.*;

/**
 * représente l'état observable d'une partie de tCHu.
 *
 * @author Barrada Soufiane(310890)
 * @author Zaaraoui Imane (314994)
 */
public class ObservableGameState {

    private final PlayerId player;

    private final IntegerProperty deckTickets;
    private final IntegerProperty deckCards;
    private final List<ObjectProperty<Card>> faceUpCards;
    private final Map<Route,ObjectProperty<PlayerId>> routeOwner;

    private final IntegerProperty playerTickets;
    private final IntegerProperty playerCards;
    private final IntegerProperty playerCars;
    private final IntegerProperty playerPoints;
    private final IntegerProperty otherPlayerTickets;
    private final IntegerProperty otherPlayerCards;
    private final IntegerProperty otherPlayerCars;
    private final IntegerProperty otherPlayerPoints;

    private final ObservableList<Ticket> tickets;
    private final Map<Card,IntegerProperty> cards;
    private final Map<Route,BooleanProperty> routeToTake;


    private PlayerState ps;
    private PublicGameState pgs;
    private static final List<Route> routesDouble= new LinkedList<>();

    /**
     * Constructeur
     * @param pID l'identité du joueur auquel va correspondre l'instance
     */
    public ObservableGameState(PlayerId pID) {
        this.player=pID;

        this.deckTickets = new SimpleIntegerProperty(0);
        this.deckCards = new SimpleIntegerProperty(0);
        this.faceUpCards = new ArrayList<>(List.of(new SimpleObjectProperty<>(),new SimpleObjectProperty<>(),
                new SimpleObjectProperty<>(),new SimpleObjectProperty<>(),new SimpleObjectProperty<>()));
        this.routeOwner= new HashMap<>();

        this.playerTickets =  new SimpleIntegerProperty(0);
        this.playerCards =  new SimpleIntegerProperty(0);
        this.playerCars =  new SimpleIntegerProperty(0);
        this.playerPoints =  new SimpleIntegerProperty(0);
        this.otherPlayerTickets =  new SimpleIntegerProperty(0);
        this.otherPlayerCards =  new SimpleIntegerProperty(0);
        this.otherPlayerCars =  new SimpleIntegerProperty(0);
        this.otherPlayerPoints =  new SimpleIntegerProperty(0);
        this.tickets = FXCollections.observableArrayList();
        this.cards =new HashMap<>();

        for(int i=0; i<Card.COUNT;i++){
            cards.put(Card.ALL.get(i),new SimpleIntegerProperty(0));
        }

        this.routeToTake =new HashMap<>();
        Route routePrecedente=null;
        for (Route route : ChMap.routes()){

            routeOwner.put(route,new SimpleObjectProperty<>(null));
            routeToTake.put(route,new SimpleBooleanProperty(false));
            if(route.id().charAt(route.id().length() - 1)=='2'){
                routesDouble.add(route);
                routesDouble.add(routePrecedente);
            }
             routePrecedente = route;

         }

    }

    /**
     *
     * @return le pourcentage de billets restant dans la pioche
     */
    public int getDeckTickets() {
        return deckTickets.get();
    }

    /**
     *
     * @return une propriété contenant le pourcentage de billets restant dans la pioche
     */
    public ReadOnlyIntegerProperty deckTicketsProperty() {
        return deckTickets;
    }

    /**
     *
     * @return  le pourcentage de cartes restant dans la pioche
     */
    public int getDeckCards() {
        return deckCards.get();
    }

    /**
     *
     * @return une propriété contenant le pourcentage de billets restant dans la pioche
     */
    public ReadOnlyIntegerProperty deckCardsProperty() {
        return deckCards;
    }

    /**
     *
     * @param slot emplacement de la carte face visible
     * @return la propriété de la carte face visible à l'emplacement slot
     */
    public ReadOnlyObjectProperty<Card> faceUpCardProperty(int slot) {
        return faceUpCards.get(slot);
    }

    /**
     *
     * @param route la route dont on veut la propriété
     * @return propriété de l'identité du propriétaire de la route @route
     */
    public ReadOnlyObjectProperty<PlayerId> routeOwnerProperty(Route route) {
        return routeOwner.get(route);
    }

    /**
     *
     * @return le nombre de tickets du joueur
     */
    public int getPlayerTickets() {
        return playerTickets.get();
    }

    /**
     *
     * @return la propriété du joueur contenant le nombre de billets
     */
    public ReadOnlyIntegerProperty playerTicketsProperty() {
        return playerTickets;
    }

    /**
     *
     * @return  le nombre de cartes du joueur
     */
    public int getPlayerCards() {
        return playerCards.get();
    }

    /**
     *
     * @return la propriété du joueur contenant le nombre de cartes
     */
    public ReadOnlyIntegerProperty playerCardsProperty() {
        return playerCards;
    }

    /**
     *
     * @return le nombre de wagons du joueur
     */
    public int getPlayerCars() {
        return playerCars.get();
    }

    /**
     *
     * @return la propriété du joueur contenant le nombre de wagons
     */
    public ReadOnlyIntegerProperty playerCarsProperty() {
        return playerCars;
    }

    /**
     *
     * @return le nombre de points du joueur
     */
    public int getPlayerPoints() {
        return playerPoints.get();
    }

    /**
     *
     * @return la propriété du joueur contenant son nombre de points
     */
    public ReadOnlyIntegerProperty playerPointsProperty() {
        return playerPoints;
    }

    /**
     *
     * @return le nombre de tickets du deuxieme joueur
     */
    public int getOtherPlayerTickets() {
        return otherPlayerTickets.get();
    }

    /**
     *
     * @return la propriété du deuxieme joueur contenant son nombre de tickets
     */
    public ReadOnlyIntegerProperty otherPlayerTicketsProperty() {
        return otherPlayerTickets;
    }

    /**
     *
     * @return le nombre de cards du deuxieme joueur
     */
    public int getOtherPlayerCards() {
        return otherPlayerCards.get();
    }

    /**
     *
     * @return la propriété du deuxieme joueur contenant son nombre de cartes
     */
    public ReadOnlyIntegerProperty otherPlayerCardsProperty() {
        return otherPlayerCards;
    }

    /**
     *
     * @return le nombre de wagons du deuxieme joueur
     */
    public int getOtherPlayerCars() {
        return otherPlayerCars.get();
    }

    /**
     *
     * @return la propriété du deuxieme joueur contenant son nombre de cartes
     */
    public ReadOnlyIntegerProperty otherPlayerCarsProperty() {
        return otherPlayerCars;
    }

    /**
     *
     * @return  le nombre de points du deuxieme joueur
     */
    public int getOtherPlayerPoints() {
        return otherPlayerPoints.get();
    }

    /**
     *
     * @return la propriété du deuxieme joueur contenant son nombre de points
     */
    public ReadOnlyIntegerProperty otherPlayerPointsProperty() {
        return otherPlayerPoints;
    }

    /**
     *
     * @return  la propriété contenant la liste des billets du joueur
     */
    public ObservableList<Ticket> getTickets() {
        return FXCollections.unmodifiableObservableList(tickets);
    }

    /**
     *
     * @param card le type de card
     * @return la propriété contenant le nombre de cartes type card que le joueur a en main
     */
    public ReadOnlyIntegerProperty cardsNumberProperty(Card card) {
        return cards.get(card);
    }

    /**
     *
     * @param route
     * @return propriété contenant la valeur booléenne indiquant si le joueur peut
     * s'emparer de la route @route
     */
    public ReadOnlyBooleanProperty routeTotakeProperty(Route route){
        return routeToTake.get(route);
    }

    /**
     * Cette méthode met à jour la totalité des propriétés décrites
     * dans cette classe en fonction des arguments passé à la methode
     * @param pgs la partie publique du jeu
     * @param ps  l'état complet du joueur
     */
    public void setState(PublicGameState pgs, PlayerState ps){
        this.pgs=pgs;
        this.ps=ps;
        deckTickets.set((int)((pgs.ticketsCount()/(double)ChMap.tickets().size())*100));
        deckCards.set((int)((pgs.cardState().deckSize()/(double)Constants.TOTAL_CARDS_COUNT)*100));

        for(int i=0;i<5;i++){
            faceUpCards.get(i).set(pgs.cardState().faceUpCard(i));

        }
        List<Route> routeNonPrises= new LinkedList<>(ChMap.routes());
        routeNonPrises.removeAll(pgs.claimedRoutes());
        for(Route route: routeNonPrises){
            routeOwner.get(route).set(null);
            if(player == pgs.currentPlayerId() && ps.canClaimRoute(route)){
                if(routesDouble.contains(route)){
                    String s=  route.id().substring(0, route.id().length() - 1);
                    List<Route> routeNonprisesPrime = new ArrayList<>(routeNonPrises);
                    routeNonprisesPrime.remove(route);
                    for(Route routePrime: routeNonprisesPrime) {
                        if(routePrime.id().contains(s)){
                            routeToTake.get(route).set(true);
                            break;
                        }
                        routeToTake.get(route).set(false);
                    }
                }else{
                    routeToTake.get(route).set(true);
                }

            }else{
                routeToTake.get(route).set(false);
            }
        }

        for(PlayerId playerId : PlayerId.ALL){

            for(Route route : pgs.claimedRoutes()){
                if(pgs.playerState(playerId).routes().contains(route)){

                   routeOwner.get(route).set(playerId);

                }
            }
        }

        playerTickets.set(ps.ticketCount());
        playerCards.set(ps.cardCount());
        playerCars.set(ps.carCount());
        playerPoints.set(ps.claimPoints());
        otherPlayerTickets.set(pgs.playerState(player.next()).ticketCount());
        otherPlayerCards.set(pgs.playerState(player.next()).cardCount());
        otherPlayerCars.set(pgs.playerState(player.next()).carCount());
        otherPlayerPoints.set(pgs.playerState(player.next()).claimPoints());
        tickets.setAll(FXCollections.observableArrayList(ps.tickets().toList()));

        for(Card card : Card.ALL){
            cards.get(card).set(ps.cards().countOf(card));
        }

    }

    /**
     *
     * @return true ssi il est possible de tirer des billets, c-à-d si la pioche n'est pas vide
     */
    public boolean canDrawTickets(){
        return pgs.canDrawTickets();
    }

    /**
     *
     * @return true ssi il est possible de tirer des cartes
     */
    public boolean canDrawCards(){
        return pgs.canDrawCards();
    }

    /**
     * retourne la liste des ensembles de cartes que le joueur pourrait utiliser pour s'emparer de la route donnée
     *
     * @param route
     * @return la liste des ensembles de cartes pour s'emparer de route
     * @throws IllegalArgumentException si le joueur n'a pas assez de wagons pour s'emparer de la route
     */
    public List<SortedBag<Card>> possibleClaimCards(Route route){
        return ps.possibleClaimCards(route);
    }
}
