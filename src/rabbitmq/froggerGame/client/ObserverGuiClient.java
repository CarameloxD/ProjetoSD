/**
 * <p>
 * Title: Projecto SD</p>
 * <p>
 * Description: Projecto apoio aulas SD</p>
 * <p>
 * Copyright: Copyright (c) 2011</p>
 * <p>
 * Company: UFP </p>
 *
 * @author Rui Moreira
 * @version 2.0
 */
package rabbitmq.froggerGame.client;

import com.rabbitmq.client.BuiltinExchangeType;
import org.json.JSONObject;
import rabbitmq.froggerGame.frogger.Main;
import rabbitmq.froggerGame.server.Game;
import rabbitmq.util.RabbitUtils;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author rjm
 */
public class ObserverGuiClient extends javax.swing.JFrame {

    private Observer observer;

    /**
     * Creates new form ChatClientFrame
     *
     * @param args
     */
    public ObserverGuiClient(String args[]) throws IOException {

        Logger.getLogger(this.getClass().getName()).log(Level.INFO, " After initComponents()...");

        RabbitUtils.printArgs(args);

        //Read args passed via shell command
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        String exchangeName = args[2];
        //String room=args[3];
        String user = args[3];
        //String general=args[5];
        String game = args[4];
        String gameDifficulty = args[5];

        Main f = null;
        try {
            f = new Main(transformDifficulty(gameDifficulty));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //2. Create the _05_observer object that manages send/receive of messages to/from rabbitmq
        try {
            observer = new Observer(f, host, port, "guest", "guest", user, game, "cycleTraffic", "froggerMoves", exchangeName, BuiltinExchangeType.FANOUT, "UTF-8");
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, " After initObserver()...");

        f.setObserver(observer);

        observer.sendMessage("initialize" + observer.getUsername());
        while (observer.getReceivedMessage() == null || !observer.getReceivedMessage().contains("initialize0")) {
            System.out.println(observer.getReceivedMessage());
        }

        f.run();

    }


    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) throws IOException {
        new ObserverGuiClient(args);
    }

    public int transformDifficulty(String d) {
        int num;
        switch (d) {
            case "easy":
                num = 1;
                break;
            case "normal":
                num = 2;
                break;
            case "hard":
                num = 3;
                break;
            default:
                System.out.println("Invalid Option");
                num = 0;
        }
        return num;
    }
}
