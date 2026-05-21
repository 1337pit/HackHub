package unicam.hackhub.repository;

import unicam.hackhub.model.Team;
import unicam.hackhub.model.User;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TeamRepositoryImplementation implements TeamRepository {

    private final Set<Team> teams = new HashSet<Team>();

    @Override
    public Team findByID(Long teamID) {
        return teams.stream()
                .filter(t -> t.getId().equals(teamID))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Team findByUser(User user) {
        return teams.stream()
                .filter(t -> t.getMembers() != null && t.getMembers().contains(user))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Team findByName(String name) {
        return teams.stream()
                .filter(t -> t.getTeamName().equals(name))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Team save(Team team) {
        teams.add(team);
        return team;
    }

    @Override
    public void saveAll(List<Team> entities) {
        for(Team team : entities){teams.add(team);}
        System.out.println("Teams saved");
    }
}
