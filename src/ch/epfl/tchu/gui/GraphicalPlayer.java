package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.StringConverter;
import java.util.List;
import java.util.Map;
import static javafx.application.Platform.isFxApplicationThread;

/**
 * représente l'interface graphique d'un joueur de tCHu
 *
 * @author Barrada Soufiane(310890)
 * @author Zaaraoui Imane (314994)
 */
public class GraphicalPlayer {

    private final ObservableGameState observableGameState;
    private final ObservableList<Text> infos;
    private final Stage stage;
    private final ObjectProperty<ActionHandlers.DrawTicketsHandler> dtHandler;
    private final ObjectProperty<ActionHandlers.DrawCardHandler> dcHandler;
    private final ObjectProperty<ActionHandlers.ClaimRouteHandler> crHandler;

    /**
     * construit l'interface graphique de tCHu
     * @param player l'identité du joueur auquel l'instance correspond
     * @param playerNames  table associative des noms des joueurs.
     */
    public GraphicalPlayer(PlayerId player, Map<PlayerId, String> playerNames) {
        assert isFxApplicationThread();
        observableGameState = new ObservableGameState(player);
        infos = FXCollections.observableArrayList();
        dtHandler = new SimpleObjectProperty<>(null);
        dcHandler = new SimpleObjectProperty<>(null);
        crHandler = new SimpleObjectProperty<>(null);
        Node mapView = MapViewCreator.createMapView(observableGameState, crHandler
                , this::chooseClaimCards);
        Node handView = DecksViewCreator.createHandView(observableGameState);
        Node cardsView = DecksViewCreator.createCardsView(observableGameState, dtHandler, dcHandler);
        Node infoView = InfoViewCreator.createInfoView(player, playerNames, observableGameState, infos);
        BorderPane mainPane = new BorderPane(mapView, null, cardsView, handView, infoView);
        Scene scene = new Scene(mainPane);
        stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("tchu " + '\u2014'+" "  + playerNames.get(player));
        stage.show();

    }

    /**
     * Cette méthode met à jour la totalité des propriétés
     * en fonction des arguments passé à la methode
     * @param pgs la partie publique du jeu
     * @param ps l'état complet du joueur
     */
    public void setState(PublicGameState pgs, PlayerState ps) {
        assert isFxApplicationThread();
        observableGameState.setState(pgs, ps);
    }

    /**
     *  ajoutent le message donné au bas des informations
     *  sur le déroulement de la partie, qui sont présentées
     *  dans la partie inférieure de la vue des informations
     * @param message message sur le déroulement de la partie
     */
    public void receiveInfo(String message) {
        assert isFxApplicationThread();

        if (infos.size() == 5) {
            infos.remove(0);
        }
            infos.add( new Text(message));

    }

    /**
     * appeler pour commencer un tour
     * @param dth gestionnaire d'action
     * @param dch gestionnaire d'action
     * @param crh gestionnaire d'action
     */
    public void startTurn(ActionHandlers.DrawTicketsHandler dth, ActionHandlers.DrawCardHandler dch,
                          ActionHandlers.ClaimRouteHandler crh) {
        assert isFxApplicationThread();


        dtHandler.set(observableGameState.canDrawTickets() ? () -> {
            dth.onDrawTickets();
            handlersToNull();
        } : null);
        dcHandler.set(observableGameState.canDrawCards() ? d -> {
            dch.onDrawCard(d);
            handlersToNull();
        } : null);
        crHandler.set((route, initialCards) -> {
            crh.onClaimRoute(route, initialCards);
            handlersToNull();
        });
    }

    /**
     *
     * @param choixBillets multiensemble contenant cinq ou trois billets que le joueur peut choisir
     * @param cth  un gestionnaire de choix de billets
     */
    public void chooseTickets(SortedBag<Ticket> choixBillets, ActionHandlers.ChooseTicketsHandler cth) {
        assert isFxApplicationThread();

        ListView<Ticket> listView = new ListView<>(FXCollections.observableArrayList(choixBillets.toList()));
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        Button button = new Button("Choisir");


        button.disableProperty().bind(Bindings.size(listView.getSelectionModel().getSelectedItems())
                .lessThan(choixBillets.size() - 2));


        Stage stagePanneau = panneauDeSelection(String.format(StringsFr.CHOOSE_TICKETS, choixBillets.size() > 3 ? 3 : 1,
                choixBillets.size() > 3 ? "s" : ""), listView, button);
        button.setOnAction(event -> {
            stagePanneau.hide();
            cth.onChooseTickets(SortedBag.of(listView.getSelectionModel().getSelectedItems()));

        });


    }

    /**
     *  autorise le joueur a choisir une carte wagon/locomotive,
     *  soit l'une des cinq dont la face est visible, soit celle du sommet de la pioche
     * @param dch un gestionnaire de tirage de carte
     */
    public void drawCard(ActionHandlers.DrawCardHandler dch) {
        assert isFxApplicationThread();

        dcHandler.set(d -> {
            dch.onDrawCard(d);
            handlersToNull();
        });
    }

    /**
     *
     * @param initialCards liste de multiensembles de cartes, qui sont les cartes initiales
     *                     que le joueur peut utiliser pour s'emparer d'une route
     * @param cch gestionnaire de choix de cartes
     */
    public void chooseClaimCards(List<SortedBag<Card>> initialCards, ActionHandlers.ChooseCardsHandler cch) {
        assert isFxApplicationThread();
        ListView<SortedBag<Card>> listView = new ListView<>(FXCollections.observableArrayList(initialCards));
        listView.setCellFactory(v ->
                new TextFieldListCell<>(new CardBagStringConverter()));
        Button button = new Button("Choisir");


        button.disableProperty().bind(Bindings.size(listView.getSelectionModel().getSelectedItems())
                .isNotEqualTo(1));


        Stage stagePanneau = panneauDeSelection(StringsFr.CHOOSE_CARDS, listView, button);
        button.setOnAction(event -> {
            stagePanneau.hide();
            cch.onChooseCards(SortedBag.of(listView.getSelectionModel().getSelectedItem()));

        });

    }

    /**
     *
     * @param additionalCards une liste de multiensembles de cartes, qui sont les cartes additionnelles
     *                        que le joueur peut utiliser pour s'emparer d'un tunnel
     * @param cch un gestionnaire de choix de cartes
     */
    public void chooseAdditionalCards(List<SortedBag<Card>> additionalCards, ActionHandlers.ChooseCardsHandler cch) {
        assert isFxApplicationThread();

        ListView<SortedBag<Card>> listView = new ListView<>(FXCollections.observableArrayList(additionalCards));
        listView.setCellFactory(v ->
                new TextFieldListCell<>(new CardBagStringConverter()));
        Button button = new Button("Choisir");


        Stage stagePanneau = panneauDeSelection(StringsFr.CHOOSE_ADDITIONAL_CARDS, listView, button);
        button.setOnAction(event -> {
            stagePanneau.hide();
            if(listView.getSelectionModel().getSelectedItem()==null) cch.onChooseCards(SortedBag.of());
            else cch.onChooseCards(listView.getSelectionModel().getSelectedItem());

        });

    }

    private Stage panneauDeSelection(String texte, ListView listView, Button button) {
        Stage stagePanneau = new Stage(StageStyle.UTILITY);
        stagePanneau.initOwner(this.stage);
        stagePanneau.initModality(Modality.WINDOW_MODAL);
        VBox panneau = new VBox();
        Scene scene = new Scene(panneau);
        scene.getStylesheets().setAll("chooser.css");
        TextFlow textesFlow = new TextFlow();
        Text textIntro = new Text(texte);
        textesFlow.getChildren().add(textIntro);
        panneau.getChildren().setAll(textesFlow, listView, button);
        stagePanneau.setScene(scene);
        stagePanneau.show();
        stagePanneau.setOnCloseRequest(Event::consume);



        return stagePanneau;
    }

    private void handlersToNull(){
        dcHandler.set(null);
        crHandler.set(null);
        dtHandler.set(null);
    }

    private static class CardBagStringConverter extends StringConverter<SortedBag<Card>> {

        @Override
        public String toString(SortedBag<Card> object) {

            return Info.nomsCards(object);
        }

        @Override
        public SortedBag<Card> fromString(String string) {
            throw new UnsupportedOperationException();
        }
    }
}
