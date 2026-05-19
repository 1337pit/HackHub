package unicam.hackhub.repository;

import unicam.hackhub.model.Team;

import java.util.List;

public interface TeamRepository {

    public void findByID(Long teamID);

    public Team save(Team entity);

    public void saveAll(List<Team> entities);

}
