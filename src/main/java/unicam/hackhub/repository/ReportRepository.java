package unicam.hackhub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import unicam.hackhub.model.Hackathon;
import unicam.hackhub.model.Mentor;
import unicam.hackhub.model.Report;
import unicam.hackhub.model.Team;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByHackathon(Hackathon hackathon);
}
