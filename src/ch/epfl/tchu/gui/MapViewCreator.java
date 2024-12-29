package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Route;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;


import java.util.List;
/**
 *
 * @author Barrada Soufiane(310890)
 * @author Zaaraoui Imane (314994)
 */
 class MapViewCreator {
    private MapViewCreator() {
    }

    /**
     * permet de créer la vue de la carte
     * @param obs l'état du jeu observable
     * @param crh  propriété contenant le gestionnaire d'action à utiliser lorsque le joueur désire s'emparer d'une route
     * @param cc sélectionneur de cartes
     * @return la vue de la carte
     */
    public static Node createMapView(ObservableGameState obs
             , ObjectProperty<ActionHandlers.ClaimRouteHandler> crh,CardChooser cc) {


        Image map = new Image("map.png");
        ImageView fond = new ImageView(map);
        Pane carte = new Pane(fond);

        carte.getStylesheets().setAll("map.css", "colors.css");

        for (Route route : ChMap.routes()) {
            Group routeGroup = new Group();
            routeGroup.setId(route.id());
            if (route.color() != null) {
                routeGroup.getStyleClass().setAll("route", route.level().name(), route.color().name());
            } else {
                routeGroup.getStyleClass().setAll("route", route.level().name(), "NEUTRAL");
            }
            for (int i = 0; i < route.length(); i++) {
                Group casee = new Group();
                casee.setId(route.id() + "_" + (i + 1));
                Rectangle voie = new Rectangle();
                voie.getStyleClass().setAll("track", "filled");
                voie.setWidth(36);
                voie.setHeight(12);
                Group wagon = new Group();
                wagon.getStyleClass().setAll("car");
                Rectangle wagonRectangle = new Rectangle();
                wagonRectangle.getStyleClass().setAll("filled");
                wagonRectangle.setWidth(36);
                wagonRectangle.setHeight(12);
                Circle wagonCircle1 = new Circle();
                Circle wagonCircle2 = new Circle();
                wagonCircle1.setRadius(3);
                wagonCircle1.setCenterX(12);
                wagonCircle1.setCenterY(6);
                wagonCircle2.setRadius(3);
                wagonCircle2.setCenterX(24);
                wagonCircle2.setCenterY(6);
                wagon.getChildren().addAll(wagonRectangle, wagonCircle1, wagonCircle2);
                routeGroup.getChildren().add(casee);
                casee.getChildren().addAll(voie, wagon);


            }
            obs.routeOwnerProperty(route).addListener((o,ov,nv)->{if(nv!=null){
                routeGroup.getStyleClass().add(nv.name());
            }});

            routeGroup.disableProperty().bind(obs.routeTotakeProperty(route).not().or(crh.isNull()));
            carte.getChildren().add(routeGroup);
            routeGroup.setOnMouseClicked(e ->{
                if(obs.possibleClaimCards(route).size()==1){
                    crh.get().onClaimRoute(route,obs.possibleClaimCards(route).get(0));
                }else{
                    ActionHandlers.ChooseCardsHandler chooseCardsH =
                            chosenCards -> crh.get().onClaimRoute(route, chosenCards);
                    cc.chooseCards(obs.possibleClaimCards(route), chooseCardsH);
                }
            });



        }


        return carte;
    }
    /**
     *
     * @author Barrada Soufiane(310890)
     * @author Zaaraoui Imane (314994)
     */
    @FunctionalInterface
    public interface CardChooser {
        /**
         * Lorsque le joueur désire s'emparer d'une route
         * @param options Les possibilités qui s'offrent au joueur
         * @param handler destiné à être utilisé lorsque le joueur a fait son choix
         */
        void chooseCards(List<SortedBag<Card>> options,
                         ActionHandlers.ChooseCardsHandler handler);
    }
}













