package unicam.hackhub.service;

import unicam.hackhub.model.*;

public class HackathonService {

    public Hackathon createHackathon(Hackathon hackathon){
        return null;
    }

    public void addMentor(Long mentorID) {}

    public void declareWinner(Team team){}

    public void changeState(HackathonState state) {}

    public void checkHackathonAvailability(Hackathon hackathon) {
        if(!(hackathon.isRegistrationOpen())) {
            throw new IllegalArgumentException("Hackathon is not open");
        }
    }

    public void checkTeamSize(Team team, Hackathon hackathon) {
        if(team.getSize() > hackathon.getMaxTeamSize()) {
            throw new IllegalArgumentException("Team size exceeds limit");
        }
    }

    public void checkTeamAlreadyRegistered(Registration registration){
        if (registration.exists()) {
            throw new IllegalArgumentException("Team already registered");
        }
    }

}
