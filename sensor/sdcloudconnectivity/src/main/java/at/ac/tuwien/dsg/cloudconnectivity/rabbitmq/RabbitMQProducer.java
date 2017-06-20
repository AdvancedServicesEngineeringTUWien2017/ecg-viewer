package at.ac.tuwien.dsg.cloudconnectivity.rabbitmq;

import at.ac.tuwien.dsg.common.sdapi.Event;
import at.ac.tuwien.dsg.common.sdapi.Producer;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;

/*
Writtenb iCOMOT team.
Last change: revised and modifed by Hong-Linh truong

Hong-Linh Truong Revision
8 Oct 2016: change to paho
*/

public class RabbitMQProducer implements Producer {

    private static final Logger LOGGER = Logger.getLogger(RabbitMQProducer.class);

    private String username = "guest";
    private String password = "guest";
    private String virtualHost = "testID";
    private String hostname;

    private String exchangeName;
    private String queueName;
    private String queueNameWebapp;
    private String routingKey;

    private int port;

    private Channel channel;

    private Connection connection;

    public RabbitMQProducer(String configFile) {
        // get the broker IP from /etc/environment file
        //makesure you have set the iCOMMOT_CLOUDCONNECTIVITY_DIR

        JSONParser parser = new JSONParser();
        try {

            Object obj = parser.parse(new FileReader(configFile));
            JSONObject jsonObject = (JSONObject) obj;
            this.username = (String) jsonObject.get("username");
            this.password = (String) jsonObject.get("password");
            this.virtualHost = (String) jsonObject.get("virtualHost");
            this.hostname = (String) jsonObject.get("hostname");
            this.port = ((Long) jsonObject.get("port")).intValue();
            this.exchangeName = (String) jsonObject.get("exchangeName");
            this.queueName = (String) jsonObject.get("queueName");
            this.queueNameWebapp = (String) jsonObject.get("queueNameWebapp");
            this.routingKey = (String) jsonObject.get("routingKey");

            ConnectionFactory factory = new ConnectionFactory();
            factory.setUsername(username);
            factory.setPassword(password);
            factory.setVirtualHost(virtualHost);
            factory.setHost(hostname);
            factory.setPort(port);
            channel = factory.newConnection().createChannel();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setUp() {

        try {


            ConnectionFactory factory = new ConnectionFactory();
            factory.setUsername(username);
            factory.setPassword(password);
            factory.setVirtualHost(virtualHost);
            factory.setHost(hostname);
            factory.setPort(port);


            LOGGER.info(String.format("Trying to connect to %s ...", this.hostname));
            connection = factory.newConnection();
            channel = connection.createChannel();

            channel.exchangeDeclare(exchangeName, "direct", true);
            channel.queueDeclare(queueName, true, false, false, null);
            channel.queueBind(queueName, exchangeName, routingKey);
            channel.queueDeclare(queueNameWebapp, true, false, false, null);
            channel.queueBind(queueNameWebapp, exchangeName, routingKey);

            LOGGER.info(String.format("Successfully connected to %s.", this.hostname));
        } catch (Exception e) {
            LOGGER.error("Cloud not start MQTT producer client!", e);
            e.printStackTrace();
        }
    }

    public void push(Event e) {
        try {
            channel.basicPublish(exchangeName, routingKey,
                    new AMQP.BasicProperties.Builder()
                            .contentType("application/json")
                            .deliveryMode(2)
                            .priority(1)
                            .build(), e.getEventContent().getBytes());
            System.out.println("Message published");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void close() {
        LOGGER.info("Closing MQTT producer client!");
        try {
            if (this.channel != null)
                this.channel.close();
            if (this.connection != null)
                this.connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pollEvent() {
        throw new UnsupportedOperationException();

    }

}
