package unicam.hackhub.repository;

import unicam.hackhub.model.Invite;

import java.util.List;

public interface InviteRepository {

    public void findByID(Long inviteID);

    public Invite save(Invite entity);

    public void saveAll(List<Invite> entities);
}
