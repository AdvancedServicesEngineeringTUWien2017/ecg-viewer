package at.ac.tuwien.dsg.cloudconnectivity.mqtt;

import at.ac.tuwien.dsg.common.sdapi.Event;
import at.ac.tuwien.dsg.common.sdapi.Producer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.Properties;

/*
Writtenb iCOMOT team.
Last change: revised and modifed by Hong-Linh truong

Hong-Linh Truong Revision
8 Oct 2016: change to paho
*/

public class KAFKAProducer implements Producer {

    private static final Logger LOGGER = Logger.getLogger(KAFKAProducer.class);

    private String zookeeper = "guest";
    private String bootstrap_server = "guest";
    private String clientID = "testID";
    private String topic;
    private String group_id;

    org.apache.kafka.clients.producer.Producer<String, String> producer = null;

    Properties properties = null;

    public KAFKAProducer(String configFile) {
        // get the broker IP from /etc/environment file
        //makesure you have set the iCOMMOT_CLOUDCONNECTIVITY_DIR

        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader(configFile));
            JSONObject jsonObject = (JSONObject) obj;
            group_id = (String) jsonObject.get("group.id");
            zookeeper = (String) jsonObject.get("zookeeper.connect");
            bootstrap_server = (String) jsonObject.get("bootstrap.servers");
            String topic = (String) jsonObject.get("topic");
            this.topic = topic;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setUp() {

        try {

            Properties props = new Properties();

            props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap_server);
            props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
            props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
            props.put(ProducerConfig.CLIENT_ID_CONFIG, this.clientID);
            props.put("request.required.acks", "1");

            LOGGER.info(String.format("Trying to connect to %s ...", this.bootstrap_server));
            producer = new KafkaProducer<String, String>(props);

            LOGGER.info(String.format("Successfully connected to %s.", this.bootstrap_server));
        } catch (Exception e) {
            LOGGER.error("Cloud not start MQTT producer client!", e);
            e.printStackTrace();
        }
    }

    public void push(Event e) {
        try {
            producer.send(new ProducerRecord<String, String>(this.topic, e.getEventContent()));
            System.out.println("Message published");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void close() {
        LOGGER.info("Closing MQTT producer client!");
        try {
            if (this.producer != null)
                this.producer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pollEvent() {
        throw new UnsupportedOperationException();

    }

}
