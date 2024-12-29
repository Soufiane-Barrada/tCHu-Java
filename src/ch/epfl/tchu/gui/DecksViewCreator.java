package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Constants;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.scene.Group;
import javafx.scene.Node;

import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

/**
 *
 * @author Barrada Soufiane(310890)
 * @author Zaaraoui Imane (314994)
 */
  class DecksViewCreator {
    private DecksViewCreator() {
    }

    /**
     *
     * @param obs l'etat de jeu observable
     * @return retourne la vue de la main
     */
    public static Node createHandView(ObservableGameState obs) {
        HBox vue = new HBox();
        vue.getStylesheets().addAll("decks.css", "colors.css");
        ListView billets = new ListView<>(obs.getTickets());
        billets.setId("tickets");
        HBox hand = new HBox();
        hand.setId("hand-pane");
        vue.getChildren().addAll(billets, hand);
        for (Card card : Card.ALL) {
            StackPane carte = new StackPane();
            if (card != Card.LOCOMOTIVE) {
                carte.getStyleClass().addAll(card.color().name(), "card");
            } else {
                carte.getStyleClass().addAll("NEUTRAL", "card");
            }
            Rectangle couleur1 = new Rectangle();
            couleur1.getStyleClass().addAll("outside");
            couleur1.setHeight(90);
            couleur1.setWidth(60);
            Rectangle couleur2 = new Rectangle();
            couleur2.getStyleClass().addAll("filled", "inside");
            couleur2.setHeight(70);
            couleur2.setWidth(40);
            Rectangle couleur3 = new Rectangle();
            couleur3.getStyleClass().addAll("train-image");
            couleur3.setHeight(70);
            couleur3.setWidth(40);
            Text compteur = new Text();
            compteur.getStyleClass().addAll("count");
            carte.getChildren().addAll(couleur1, couleur2, couleur3, compteur);
            hand.getChildren().add(carte);

            ReadOnlyIntegerProperty count = obs.cardsNumberProperty(card);

            carte.visibleProperty().bind(Bindings.greaterThan(count,0));
            compteur.textProperty().bind(Bindings.convert(count));
            compteur.visibleProperty().bind(Bindings.greaterThan(count,1));
        }
        return vue;
    }

    /**
     *
     * @param obs  l'état de jeu observable
     * @param dth  un gestionnaire de tirage de billets
     * @param dch  un gestionnaire de tirage de cartes
     * @return la vue des cartes
     */
    public static Node createCardsView(ObservableGameState obs,
                                       ObjectProperty<ActionHandlers.DrawTicketsHandler> dth, ObjectProperty<ActionHandlers.DrawCardHandler> dch ) {


        VBox pioches = new VBox();
        pioches.setId("card-pane");
        pioches.getStylesheets().addAll("decks.css", "colors.css");
        Button billetPioche = new Button("Billets");
        billetPioche.getStyleClass().addAll("gauged");
        Button cardPioche = new Button("Cartes");
        cardPioche.getStyleClass().addAll("gauged");
        pioches.getChildren().add(billetPioche);
        billetPioche.disableProperty().bind(dth.isNull());
        cardPioche.disableProperty().bind(dch.isNull());
        billetPioche.setOnMouseClicked(event -> dth.get().onDrawTickets());
        cardPioche.setOnMouseClicked(e-> dch.get().onDrawCard(-1));

        for (int i = 0; i < Constants.FACE_UP_CARDS_COUNT; i++) {
            StackPane carte = new StackPane();


            if(obs.faceUpCardProperty(i).get()==null){
                carte.getStyleClass().addAll("card");
            }
            else if (obs.faceUpCardProperty(i).get() != Card.LOCOMOTIVE) {
                carte.getStyleClass().addAll(obs.faceUpCardProperty(i).get().color().name(), "card");
            } else {
                carte.getStyleClass().addAll("NEUTRAL", "card");
            }
            Rectangle couleur1 = new Rectangle();
            couleur1.getStyleClass().addAll("outside");
            couleur1.setHeight(90);
            couleur1.setWidth(60);
            Rectangle couleur2 = new Rectangle();
            couleur2.getStyleClass().addAll("filled", "inside");
            couleur2.setHeight(70);
            couleur2.setWidth(40);
            Rectangle couleur3 = new Rectangle();
            couleur3.getStyleClass().addAll("train-image");
            couleur3.setHeight(70);
            couleur3.setWidth(40);
            carte.getChildren().addAll(couleur1, couleur2, couleur3);
            pioches.getChildren().add(carte);
            obs.faceUpCardProperty(i).addListener((o,ov,nv)->{
                if (nv != Card.LOCOMOTIVE) {
                    carte.getStyleClass().setAll(nv.color().name(), "card");
                } else {
                    carte.getStyleClass().addAll("NEUTRAL", "card");
                }

            });
            int l=i;
            carte.setOnMouseClicked(event -> dch.get().onDrawCard(l));

        }
        pioches.getChildren().add(cardPioche);

        Group jauge = new Group();

        Rectangle r1 = new Rectangle();
        r1.getStyleClass().addAll("background");
        r1.setWidth(50);
        r1.setHeight(5);

        Rectangle r2 = new Rectangle();
        r2.getStyleClass().addAll("foreground");
        r2.setWidth(50 * obs.getDeckCards()/100);
        r2.setHeight(5);
        jauge.getChildren().addAll(r1, r2);
        cardPioche.setGraphic(jauge);
        r2.widthProperty().bind(obs.deckCardsProperty().multiply(50).divide(100));

        Group jauge2 = new Group();

        Rectangle r10 = new Rectangle();
        r10.getStyleClass().addAll("background");
        r10.setWidth(50);
        r10.setHeight(5);

        Rectangle r20 = new Rectangle();
        r20.getStyleClass().addAll("foreground");
        r20.setWidth(50 * obs.getDeckTickets()/100);
        r20.setHeight(5);
        jauge2.getChildren().addAll(r10, r20);
        billetPioche.setGraphic(jauge2);
        r20.widthProperty().bind(obs.deckTicketsProperty().multiply(50).divide(100));

        return pioches;
    }

}
