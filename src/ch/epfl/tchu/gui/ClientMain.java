package ch.epfl.tchu.gui;

import ch.epfl.tchu.net.RemotePlayerClient;
import javafx.application.Application;
import javafx.stage.Stage;
import java.util.List;
/**
 * le programme principal du client tCHu
 *
 * @author Barrada Soufiane(310890)
 * @author Zaaraoui Imane (314994)
 */
public class ClientMain extends Application {

    private final static int DEFAULT_PORT = 5108;
    private final static String DEFAULT_HOSTNAME = "localhost";

    /**
     *
     * @param args les arguments du programme
     */
    public static void main(String[] args) { launch(args); }

    /**
     * démarrre le client
     * @param primaryStage le stage de l'application
     */
    @Override
    public void start(Stage primaryStage) {
        List<String> args = getParameters().getRaw();
        RemotePlayerClient client;
        if(args.size()==0){
             client = new RemotePlayerClient(new GraphicalPlayerAdapter(), DEFAULT_HOSTNAME, DEFAULT_PORT);
        } else if (args.size() == 1) {
            String hostname = args.get(0);
            client = new RemotePlayerClient(new GraphicalPlayerAdapter(), hostname, DEFAULT_PORT);
        }else {
            String hostname = args.get(0);
            int port = Integer.parseInt(args.get(1));
            client = new RemotePlayerClient(new GraphicalPlayerAdapter(), hostname, port);
        }
        new Thread(client::run).start();
    }
}
