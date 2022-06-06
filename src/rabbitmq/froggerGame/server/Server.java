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
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Declaring Exchange '" + this.exchangeName + "Server_" + this.game + "' with type " + this.exchangeType);
        this.channelToRabbitMq.exchangeDeclare(exchangeName + "Server_" + this.game, exchangeType);
    }

    /**
     * Creates a Consumer associated with an unnamed queue.
     */
    public void attachConsumerToChannelExchangeWithKey() {
        try {
            boolean durable=true;
            this.channelToRabbitMq.queueDeclare("FroggerWorkerQueue", durable, false, false, null);

            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

            int prefetchCount = 1;
            this.channelToRabbitMq.basicQos(prefetchCount);

            DeliverCallback deliverCallback=(consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                System.out.println(" [x] Received '" + message + "'");
                String[] msg = message.split(" ");
                setReceivedMessage(message);
                if (msg[1].equals("0") || msg[1].equals("1")) {
                    this.sendMessage(message);
                } else if (message.contains("initialize0")) {
                    sendMessage(message);
                }

                try {
                    doWork(message);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    System.out.println(" [x] Done processing task");
                    this.channelToRabbitMq.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                }
            };
            CancelCallback cancelCallback = consumerTag -> System.out.println(" [x] Consumer Tag [" + consumerTag + "] - Cancel Callback invoked!");
            boolean autoAck = false;
            this.channelToRabbitMq.basicConsume("FroggerWorkerQueue", autoAck, deliverCallback, consumerTag -> { });
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
        channelToRabbitMq.basicPublish(exchangeName + "Server_" + msg[0], "", null, msgToSend.getBytes("UTF-8"));
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

    private static void doWork(String task) throws InterruptedException {
        for (char ch : task.toCharArray()) {
            if (ch == '.') {
                Thread.sleep(1000);
            }
        }
    }
}
