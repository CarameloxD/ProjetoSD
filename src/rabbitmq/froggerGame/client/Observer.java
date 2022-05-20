package rabbitmq.froggerGame.client;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.*;
import rabbitmq.froggerGame.frogger.Main;
import rabbitmq.util.RabbitUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author rui
 */
public class Observer {

    //Reference for gui
    private final Main main;

    //Preferences for exchange...
    private final Channel channelToRabbitMq;
    private final String exchangeName;
    private final BuiltinExchangeType exchangeType;
    //private final String[] exchangeBindingKeys;
    private final String messageFormat;

    //Store received message to be get by gui
    private String receivedMessage;

    private final String game;
    private final String username;

    /**
     * @param main
     */
    public Observer(Main main, String host, int port, String user, String pass, String username, String game, String exchangeName, BuiltinExchangeType exchangeType, String messageFormat) throws IOException, TimeoutException {
        this.main=main;
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, " going to attach observer to host: " + host + "...");

        Connection connection=RabbitUtils.newConnection2Server(host, port, user, pass);
        this.channelToRabbitMq=RabbitUtils.createChannel2Server(connection);

        this.exchangeName=exchangeName;
        this.exchangeType=exchangeType;
        //String[] bindingKeys={"",""};
        //this.exchangeBindingKeys=bindingKeys;
        this.messageFormat=messageFormat;

        this.username=username;
        this.game=game;

        bindExchangeToChannelRabbitMQ();
        attachConsumerToChannelExchangeWithKey();
    }

    /**
     * Binds the channel to given exchange name and type.
     */
    private void bindExchangeToChannelRabbitMQ() throws IOException {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Declaring Exchange '" + this.exchangeName + "' with type " + this.exchangeType);
        this.channelToRabbitMq.exchangeDeclare(exchangeName, exchangeType);
    }

    /**
     * Creates a Consumer associated with an unnamed queue.
     */
    public void attachConsumerToChannelExchangeWithKey() {
        try {

            String queueName = this.channelToRabbitMq.queueDeclare().getQueue();

            String routingKey = "";
            this.channelToRabbitMq.queueBind(queueName, exchangeName, routingKey);

            Logger.getLogger(this.getClass().getName()).log(Level.INFO, " Created consumerChannel bound to Exchange " + this.exchangeName + "...");

            /* Use a DeliverCallback lambda function instead of DefaultConsumer to receive messages from queue;
               DeliverCallback is an interface which provides a single method:
                void handle(String tag, Delivery delivery) throws IOException; */
            DeliverCallback deliverCallback=(consumerTag, delivery) -> {
                String message=new String(delivery.getBody(), messageFormat);

                //Store the received message
                setReceivedMessage(message);
                System.out.println(" [x] Consumer Tag [" + consumerTag + "] - Received '" + message + "'");

                //this.gui.updateTextArea();

            };
            CancelCallback cancelCallback=consumerTag -> {
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
        String routingKey="";
        BasicProperties prop = MessageProperties.PERSISTENT_TEXT_PLAIN;

        channelToRabbitMq.basicPublish(exchangeName, routingKey, null, msgToSend.getBytes("UTF-8"));
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
        this.receivedMessage=receivedMessage;
    }

    public String getGame() {
        return game;
    }

    public String getUsername() {
        return username;
    }
}
