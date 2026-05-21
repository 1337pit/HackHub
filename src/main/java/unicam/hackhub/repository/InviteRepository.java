package unicam.hackhub.repository;

import unicam.hackhub.model.Invite;
import unicam.hackhub.model.Team;
import unicam.hackhub.model.User;

import java.util.List;

public interface InviteRepository {

    Invite findByID(Long inviteID);

    List<Invite> findByUser(User user);

    List<Invite> findByTeam(Team team);

    Invite save(Invite entity);

    void saveAll(List<Invite> entities);
}