package unicam.hackhub.repository;

import unicam.hackhub.model.Hackathon;
import unicam.hackhub.model.Mentor;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;

public class HackathonRepositoryImplementation implements HackathonRepository {

    private final Set<Hackathon> hackathons = new HashSet<Hackathon>();

    @Override
    public Hackathon findByID(Long hackathonID) {
        return hackathons.stream()
                .filter(h -> h.getId().equals(hackathonID))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Hackathon findByName(String name) {
        return hackathons.stream()
                .filter(h -> h.getNameHackathon().equals(name))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Hackathon> findByStaffMember(Long staffMemberID) {
        List<Hackathon> assignedHackathons = new ArrayList<>();

        for (Hackathon hackathon : hackathons) {
            boolean assigned = false;

            if (hackathon.getOrganizer() != null
                    && hackathon.getOrganizer().getId().equals(staffMemberID)) {
                assigned = true;
            }

            if (hackathon.getJudge() != null
                    && hackathon.getJudge().getId().equals(staffMemberID)) {
                assigned = true;
            }

            if (hackathon.getMentor() != null) {
                for (Mentor mentor : hackathon.getMentor()) {
                    if (mentor.getId().equals(staffMemberID)) {
                        assigned = true;
                        break;
                    }
                }
            }

            if (assigned) {
                assignedHackathons.add(hackathon);
            }
        }

        return assignedHackathons;
    }

    @Override
    public Hackathon save(Hackathon hackathon) {
        hackathons.add(hackathon);
        return hackathon;
    }

    @Override
    public void saveAll(List<Hackathon> entities) {
        for(Hackathon hackathon : entities){
            hackathons.add(hackathon);
        }
        System.out.println("Hackathons saved");
    }

}
