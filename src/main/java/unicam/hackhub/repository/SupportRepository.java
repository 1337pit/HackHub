package unicam.hackhub.repository;

import unicam.hackhub.model.SupportRequest;
import java.util.List;

public interface SupportRepository {
    SupportRequest save(SupportRequest requestSupport);
    SupportRequest findByID(Long id);
    List<SupportRequest> findAllByMentor(Long mentorID);
    List<SupportRequest> findAllByTeam(Long teamID);
    void delete(SupportRequest request);
}
