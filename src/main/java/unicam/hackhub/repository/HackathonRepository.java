package unicam.hackhub.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import unicam.hackhub.model.Hackathon;
import unicam.hackhub.model.Judge;
import unicam.hackhub.model.Organizer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

import java.util.List;

@Repository
public interface HackathonRepository extends JpaRepository<Hackathon, Long> {
    Optional<Hackathon> findByNameHackathon(String nameHackathon);

    @Query("SELECT h FROM Hackathon h WHERE h.organizer.id = :staffId " +
            "OR h.judge.id = :staffId " +
            "OR EXISTS (SELECT m FROM h.listMentors m WHERE m.id = :staffId)")
    List<Hackathon> findByStaffMemberId(@Param("staffId") Long staffId);
}
