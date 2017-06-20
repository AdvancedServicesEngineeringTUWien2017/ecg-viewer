package at.ase.webapp.web.websocket;

import at.ase.webapp.web.rabbitmq.dto.ECGDataDTO;
import at.ase.webapp.web.rabbitmq.dto.HeartRateDataDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.HashMap;
import java.util.Map;

@Controller
public class ECGWebSocketService implements ApplicationListener<SessionDisconnectEvent> {

    private static final Logger log = LoggerFactory.getLogger(ECGWebSocketService.class);

    private final SimpMessageSendingOperations messagingTemplate;

    private final Map<String, Double> heartRateMap;

    public ECGWebSocketService(SimpMessageSendingOperations messagingTemplate) {
        this.heartRateMap = new HashMap<>();
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/websocket/ecgdata")
    @SendTo("/topic/ecgdata")
    public void sendECGData(ECGDataDTO ecgDataDTO) {
//        log.info("sending ecg data with id: {}",ecgDataDTO.getId());
        synchronized (heartRateMap) {
            ecgDataDTO.setHeartRate(heartRateMap.get(ecgDataDTO.getId()));
        }
        messagingTemplate.convertAndSend("/topic/ecgdata", ecgDataDTO);
    }

    public void setHeartRate(HeartRateDataDTO dto) {
        synchronized (heartRateMap) {
            this.heartRateMap.put(dto.getSensorId(), dto.getValue());
        }
    }

    @Override
    public void onApplicationEvent(SessionDisconnectEvent event) {
//        ActivityDTO activityDTO = new ActivityDTO();
//        activityDTO.setSessionId(event.getSessionId());
//        activityDTO.setPage("logout");
//        messagingTemplate.convertAndSend("/topic/tracker", activityDTO);
    }
}
