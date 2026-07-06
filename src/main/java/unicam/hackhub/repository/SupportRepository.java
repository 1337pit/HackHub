package unicam.hackhub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import unicam.hackhub.model.Mentor;
import unicam.hackhub.model.SupportRequest;
import unicam.hackhub.model.Team;

import java.util.List;

@Repository
public interface SupportRepository extends JpaRepository<SupportRequest, Long> {
    List<SupportRequest> findByMentor(Mentor mentor);
}
