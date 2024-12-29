package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import ch.epfl.tchu.net.RemotePlayerProxy;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.ServerSocket;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class ServerMain extends Application {
    /**
     *
     * @param args les arguments du programme
     */
    public static void main(String[] args) { launch(args); }

    /**
     *
     * @param primaryStage le stage de l'application
     * @throws IOException si une I/O error apparait en ouvrant le socket.
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets());
        List<String> args = getParameters().getRaw();
        String joueur1;
        String joueur2;
        if(args.size()==2) {
            joueur1= args.get(0);
            joueur2= args.get(1);
        }
        else if(args.size()==1){
            joueur1= args.get(0);
            joueur2= "Charles";
        }
        else{
            joueur1= "Ada";
            joueur2= "Charles";
        }
        ServerSocket serverSocket= new ServerSocket(5108);
        RemotePlayerProxy remotePlayerProxy = new RemotePlayerProxy(serverSocket.accept());
        Map<PlayerId, Player> playersName = Map.of(PlayerId.PLAYER_1, new GraphicalPlayerAdapter(),PlayerId.PLAYER_2,
                 remotePlayerProxy);

        Map<PlayerId, String> names =
                Map.of(PlayerId.PLAYER_1, joueur1,PlayerId.PLAYER_2, "Charles");
        Random rng = new Random();
        new Thread(() -> Game.play(playersName, names, tickets, rng)).start();


    }
}
