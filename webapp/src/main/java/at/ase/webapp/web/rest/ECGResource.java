package at.ase.webapp.web.rest;

import at.ase.webapp.service.ECGService;
import at.ase.webapp.service.UserService;
import at.ase.webapp.service.dto.ECGDataDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ECGResource {

    private final Logger log = LoggerFactory.getLogger(ECGResource.class);

    private final ECGService ecgService;

    private final UserService userService;

    @Autowired
    public ECGResource(ECGService ecgService, UserService userService) {
        this.ecgService = ecgService;
        this.userService = userService;
    }

    @GetMapping("/ecgdata")
    public List<ECGDataDto> getECGDataIds() throws URISyntaxException {
        return ecgService.getAllIds();
    }

    @GetMapping("/ecgdata/{id}")
    public ECGDataDto getECGDataIds(@PathVariable String id) throws URISyntaxException {
        log.info("retrieving dat for data id: {}", id);
        ECGDataDto data = ecgService.getDataById(id);
        if (data != null) {
            userService.addBill(data.getId(), data.getQuality());
        }
        return data;
    }
}
