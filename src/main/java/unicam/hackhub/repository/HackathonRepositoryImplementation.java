package unicam.hackhub.repository;

import unicam.hackhub.model.Hackathon;
import unicam.hackhub.model.Team;

import java.util.List;
import java.util.Set;

public class HackathonRepositoryImplementation implements HackathonRepository {

    private Set<Hackathon> hackathons;

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
