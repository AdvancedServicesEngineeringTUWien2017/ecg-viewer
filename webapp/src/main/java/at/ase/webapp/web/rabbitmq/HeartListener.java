package at.ase.webapp.web.rabbitmq;

import at.ase.webapp.web.rabbitmq.dto.HeartRateDataDTO;
import at.ase.webapp.web.websocket.ECGWebSocketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by gena on 05/06/17.
 */
@Component()
public class HeartListener {

    private static final Logger log = LoggerFactory.getLogger(HeartListener.class);

    @Autowired
    private ECGWebSocketService service;

    public void handleMessage(HeartRateDataDTO message) {

        log.info("Got Heartrate: {}", message);
        service.setHeartRate(message);
    }

}
