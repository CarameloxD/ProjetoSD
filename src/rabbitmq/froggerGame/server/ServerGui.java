package rabbitmq.froggerGame.server;

import com.rabbitmq.client.BuiltinExchangeType;
import rabbitmq.froggerGame.client.Observer;
import rabbitmq.froggerGame.client.ObserverGuiClient;
import rabbitmq.froggerGame.frogger.Main;
import rabbitmq.util.RabbitUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerGui extends javax.swing.JFrame {
    private Server server;

    public ServerGui(String args[]) throws IOException, TimeoutException {

        Logger.getLogger(this.getClass().getName()).log(Level.INFO, " After initComponents()...");

        RabbitUtils.printArgs(args);

        //Read args passed via shell command
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        String exchangeName = args[2];
        String game = args[3];

        try {
            server = new Server(host, port, "guest", "guest", game, "cycleTraffic", "froggerMoves", exchangeName, BuiltinExchangeType.FANOUT, "UTF-8");
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String args[]) throws IOException, TimeoutException {
        new ServerGui(args);
    }
}
