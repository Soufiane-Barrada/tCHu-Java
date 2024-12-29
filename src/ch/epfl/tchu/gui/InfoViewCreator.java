package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.PlayerId;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import java.util.Map;

/**
 *
 * @author Barrada Soufiane(310890)
 * @author Zaaraoui Imane (314994)
 */
 class InfoViewCreator {
    private InfoViewCreator() {
    }

    /**
     *
     * @param player l'identité du joueur auquel l'interface correspond
     * @param playerNames la table associative des noms des joueurs
     * @param obs l'état de jeu observable
     * @param infos une liste (observable) contenant les informations sur le déroulement de la partie,
     *             sous la forme d'instances de Text
     * @return la vue des informations
     */
    public static Node createInfoView(PlayerId player, Map<PlayerId, String> playerNames,
                                      ObservableGameState obs, ObservableList<Text> infos) {

        VBox vueInfo = new VBox();
        vueInfo.getStylesheets().setAll("info.css", "colors.css");
        VBox stats = new VBox();
        stats.setId("player-stats");
        TextFlow statistiques = new TextFlow();
        TextFlow statistiques2 = new TextFlow();
        statistiques.getStyleClass().setAll(player.name());
        statistiques2.getStyleClass().setAll(player.next().name());
        Circle cercle = new Circle();
        Circle cercle2= new Circle();
        cercle.getStyleClass().setAll("filled");
        cercle.setRadius(5);
        cercle2.getStyleClass().setAll("filled");
        cercle2.setRadius(5);
        Text statsPlayer = new Text(String.format(StringsFr.PLAYER_STATS, playerNames.get(player),
                obs.getPlayerTickets()
                , obs.getPlayerCards(), obs.getPlayerCars(), obs.getPlayerPoints()));
        Text statsOtherPlayer = new Text(String.format(StringsFr.PLAYER_STATS,playerNames.get(player.next()),
                obs.getOtherPlayerTickets()
                , obs.getOtherPlayerCards(), obs.getOtherPlayerCars(), obs.getOtherPlayerPoints()));
        statistiques.getChildren().setAll(cercle, statsPlayer);

        statsPlayer.textProperty().bind(Bindings.format(StringsFr.PLAYER_STATS,  playerNames.get(player),
                obs.playerTicketsProperty()
                , obs.playerCardsProperty(), obs.playerCarsProperty(), obs.playerPointsProperty()));


        statistiques2.getChildren().setAll(cercle2, statsOtherPlayer);

        statsOtherPlayer.textProperty().bind(Bindings.format(StringsFr.PLAYER_STATS, playerNames.get(player.next()),
                obs.otherPlayerTicketsProperty()
                , obs.otherPlayerCardsProperty(), obs.otherPlayerCarsProperty(), obs.otherPlayerPointsProperty()));
        stats.getChildren().setAll(statistiques, statistiques2);
        Separator separateur = new Separator();
        separateur.setOrientation(Orientation.HORIZONTAL);
        TextFlow messages = new TextFlow();
        messages.setId("game-info");
        for (Text text : infos) {
            messages.getChildren().add(text);
        }

        Bindings.bindContent(messages.getChildren(),infos);

        vueInfo.getChildren().addAll(stats,separateur,messages);


        return vueInfo;
    }
}
