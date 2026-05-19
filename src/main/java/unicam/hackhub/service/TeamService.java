package unicam.hackhub.service;

import unicam.hackhub.model.Team;
import unicam.hackhub.model.User;

import java.util.List;

public class TeamService {

    //TODO
    public Team createTeam(Long userID, String name, List<User> teamUsers){
        if(name==null){
            throw new IllegalArgumentException("Team name cannot be null");
        }
        if(userID==null){
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if(teamUsers == null || teamUsers.size() == 0){
            throw new IllegalArgumentException("Team Users cannot be null or empty");
        }
        return null;
    }

    //TODO
    public void banTeam(Team team){
        if(team==null){
            throw new IllegalArgumentException("Team cannot be null");
        }
    }



}
