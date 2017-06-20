package at.ase.webapp.service;

import at.ase.webapp.repository.ECGRepository;
import at.ase.webapp.service.dto.ECGDataDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for managing users.
 */
@Service
public class ECGService {

    private final Logger log = LoggerFactory.getLogger(ECGService.class);

    private final ECGRepository ecgRepository;

    @Autowired
    public ECGService(ECGRepository ecgRepository) {
        this.ecgRepository = ecgRepository;
    }

    public List<ECGDataDto> getAllIds() {
        return ecgRepository
            .findAllIds().stream().map(ECGDataDto::new).collect(Collectors.toList());
    }

    public ECGDataDto getDataById(String id) {
        return new ECGDataDto(ecgRepository.findOne(id));
    }

}
