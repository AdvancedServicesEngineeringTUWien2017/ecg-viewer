package at.ase.webapp.repository;

import at.ase.webapp.domain.ECGData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

/**
 * Spring Data MongoDB repository for the User entity.
 */
public interface ECGRepository extends MongoRepository<ECGData, String> {

    @Query(value = "{}", fields = "{value: 0}")
    List<ECGData> findAllIds();

}
