package unicam.hackhub.repository;

import unicam.hackhub.model.Invite;
import unicam.hackhub.model.Team;
import unicam.hackhub.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InviteRepositoryImplementation implements InviteRepository {

    private final List<Invite> invites = new ArrayList<>();

    @Override
    public Invite findByID(Long inviteID) {
        return invites.stream()
                .filter(i -> i.getId().equals(inviteID))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Invite> findByUser(User user) {
        return invites.stream()
                .filter(i -> i.getInvitedUser().equals(user))
                .collect(Collectors.toList());
    }

    @Override
    public List<Invite> findByTeam(Team team) {
        return invites.stream()
                .filter(i -> i.getTeam().equals(team))
                .collect(Collectors.toList());
    }

    @Override
    public Invite save(Invite entity) {
        invites.removeIf(i -> i.getId().equals(entity.getId()));
        invites.add(entity);
        return entity;
    }

    @Override
    public void saveAll(List<Invite> entities) {
        entities.forEach(this::save);
    }
}