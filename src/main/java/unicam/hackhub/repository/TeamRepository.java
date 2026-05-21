package unicam.hackhub.repository;

import unicam.hackhub.model.Team;
import unicam.hackhub.model.User;

import java.util.List;

public interface TeamRepository {

    public Team findByID(Long teamID);

    public Team findByUser(User user);

    public Team findByName(String name);

    public Team save(Team entity);

    public void saveAll(List<Team> entities);

}
