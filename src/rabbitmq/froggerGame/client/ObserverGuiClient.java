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
    public ObserverGuiClient(String args[]) {
        try {
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, " After initComponents()...");

            RabbitUtils.printArgs(args);

            //Read args passed via shell command
            String host=args[0];
            int port=Integer.parseInt(args[1]);
            String exchangeName=args[2];
            //String room=args[3];
            String user=args[3];
            //String general=args[5];
            String game=args[4];
            int gameDifficulty= Integer.parseInt(args[5]);

            Main f = new Main(gameDifficulty);

            //2. Create the _05_observer object that manages send/receive of messages to/from rabbitmq
            this.observer= new Observer(f, host, port, "guest", "guest", user, game,exchangeName, BuiltinExchangeType.FANOUT, "UTF-8");
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, " After initObserver()...");

            f.setObserver(this.observer);

            f.run();

        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }



    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                int expectedArgs = 4;
                if (args.length >= expectedArgs) {
                    new ObserverGuiClient(args).setVisible(true);
                } else {
                    Logger.getLogger(ObserverGuiClient.class.getName()).log(Level.INFO, "check args.length < "+expectedArgs+"!!!" );
                }
            }
        });
    }

}
