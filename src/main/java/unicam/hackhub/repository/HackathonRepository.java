package unicam.hackhub.repository;

import unicam.hackhub.model.Hackathon;
import unicam.hackhub.model.Organizer;

import java.util.List;

public interface HackathonRepository {

    public Hackathon findByID(Long id);

    public Hackathon findByName(String name);

    public Hackathon save(Hackathon entity);

    public void saveAll(List<Hackathon> entities);

    public List<Hackathon> findByStaffMember(Long staffMemberID);
}
