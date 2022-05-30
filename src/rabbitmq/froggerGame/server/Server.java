package rabbitmq.froggerGame.server;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.*;
import rabbitmq.froggerGame.client.Observer;
import rabbitmq.froggerGame.frogger.Car;
import rabbitmq.froggerGame.frogger.Main;
import rabbitmq.util.RabbitUtils;
import rmi.froggerGame.client.ObserverRI;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {
    //Reference for gui

    //Preferences for exchange...
    private final Channel channelToRabbitMq;
    private final String exchangeName;
    private final BuiltinExchangeType exchangeType;
    //private final String[] exchangeBindingKeys;
    private final String[] exchangeBindingKeys;
    private final String messageFormat;

    //Store received message to be get by gui
    private String receivedMessage;

    private final String game;
    private final String cycleTraffic;
    private final String froggerMoves;

    private int nplayers;

    public Server(String host, int port, String user, String pass, String game, String cycleTraffic, String froggerMoves, String exchangeName, BuiltinExchangeType exchangeType, String messageFormat) throws IOException, TimeoutException {

        this.exchangeName = exchangeName;
        this.exchangeType = exchangeType;

        Connection connection = RabbitUtils.newConnection2Server(host, port, user, pass);
        this.channelToRabbitMq = RabbitUtils.createChannel2Server(connection);

        //String[] bindingKeys={"",""};
        //this.exchangeBindingKeys=bindingKeys;
        this.messageFormat = messageFormat;
        this.game = game;
        this.cycleTraffic = cycleTraffic;
        this.froggerMoves = froggerMoves;
        this.nplayers = 0;

        String[] bindingKeys = {};
        this.exchangeBindingKeys = bindingKeys;
        bindExchangeToChannelRabbitMQ();
        attachConsumerToChannelExchangeWithKey();
    }

    private void bindExchangeToChannelRabbitMQ() throws IOException {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Declaring Exchange '" + this.exchangeName + "Server" + "' with type " + this.exchangeType);
        this.channelToRabbitMq.exchangeDeclare(exchangeName + "Server", exchangeType);
        this.channelToRabbitMq.exchangeDeclare(exchangeName, BuiltinExchangeType.FANOUT);
    }

    /**
     * Creates a Consumer associated with an unnamed queue.
     */
    public void attachConsumerToChannelExchangeWithKey() {
        try {
            String queueName = this.channelToRabbitMq.queueDeclare().getQueue();

            this.channelToRabbitMq.queueBind(queueName, exchangeName, "");

            Logger.getLogger(this.getClass().getName()).log(Level.INFO, " Created consumerChannel bound to Exchange " + this.exchangeName + "...");

            /* Use a DeliverCallback lambda function instead of DefaultConsumer to receive messages from queue;
               DeliverCallback is an interface which provides a single method:
                void handle(String tag, Delivery delivery) throws IOException; */
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                String[] msg = message.split(" ");
                //Store the received message
                setReceivedMessage(message);
                //System.out.println(" [x] Consumer Tag [" + consumerTag + "] - Received '" + message + "'");
               if (msg[0].equals("0") || msg[0].equals("1")) {
                    this.sendMessage(message);
               } else if (message.contains("initialize")) addnPlayers();
               //this.gui.updateTextArea();
            };
            CancelCallback cancelCallback = consumerTag -> {
                System.out.println(" [x] Consumer Tag [" + consumerTag + "] - Cancel Callback invoked!");
            };
            this.channelToRabbitMq.basicConsume(queueName, true, deliverCallback, cancelCallback);
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.toString());
        }
    }

    /**
     * Publish messages to existing exchange instead of the nameless one.
     * - The routingKey is empty ("") since the fanout exchange ignores it.
     * - Messages will be lost if no queue is bound to the exchange yet.
     * - Basic properties can be: MessageProperties.PERSISTENT_TEXT_PLAIN, etc.
     */
    public void sendMessage(String msgToSend) throws IOException {
        //RoutingKey will be ignored by FANOUT exchange
        BasicProperties prop = MessageProperties.PERSISTENT_TEXT_PLAIN;
        String[] msg = msgToSend.split(" ");
        channelToRabbitMq.basicPublish(exchangeName + "Server", "", null, msgToSend.getBytes("UTF-8"));
    }

    /**
     * @return the most recent message received from the broker
     */
    public String getReceivedMessage() {
        return receivedMessage;
    }

    /**
     * @param receivedMessage the received message to set
     */
    public void setReceivedMessage(String receivedMessage) {
        this.receivedMessage = receivedMessage;
    }

    public void addnPlayers() throws IOException {
        this.nplayers++;
        this.sendMessage(String.valueOf(nplayers));
    }
}
