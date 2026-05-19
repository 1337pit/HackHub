package unicam.hackhub.repository;

import unicam.hackhub.model.Hackathon;

import java.util.List;

public interface HackathonRepository {

    public void findByName(String name);

    public Hackathon save(Hackathon entity);

    public void saveAll(List<Hackathon> entities);

}
