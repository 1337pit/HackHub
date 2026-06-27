package unicam.hackhub.repository;

import unicam.hackhub.model.SupportRequest;
import unicam.hackhub.model.SupportRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SupportRepositoryImplementation implements SupportRepository {
    private final List<SupportRequest> requests = new ArrayList<>();

    @Override
    public SupportRequest save(SupportRequest requestSupport) {
        requests.removeIf(r -> r.getId().equals(requestSupport.getId()));
        requests.add(requestSupport);
        return requestSupport;
    }

    @Override
    public SupportRequest findByID(Long id) {
        return requests.stream()
                .filter(r -> r.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<SupportRequest> findAllByMentor(Long mentorID) {
        return requests.stream()
                .filter(r -> r.getMentorID().equals(mentorID))
                .collect(Collectors.toList());
    }

    @Override
    public List<SupportRequest> findAllByTeam(Long teamID) {
        return requests.stream()
                .filter(r -> r.getTeamID().equals(teamID))
                .collect(Collectors.toList());
    }

    @Override
    public void delete(SupportRequest request){
        requests.remove(request);
    };
}
