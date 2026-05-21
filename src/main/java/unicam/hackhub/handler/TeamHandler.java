package unicam.hackhub.handler;

import unicam.hackhub.model.Hackathon;
import unicam.hackhub.model.Team;
import unicam.hackhub.model.User;
import unicam.hackhub.service.TeamService;

import java.util.List;

public class TeamHandler {

    private final TeamService teamService;

    public TeamHandler(TeamService teamService) {
        this.teamService = teamService;
    }

    /**
     * Gestisce la richiesta di creazione team.
     * Corrisponde al metodo createTeam nel sequence diagram.
     *
     * @param userID     ID dell'utente leader
     * @param name       Nome del team
     * @param teamUserIDs Lista degli ID degli utenti da invitare
     * @return Il team creato, o null in caso di errore
     */
    public Team createTeam(Long userID, String name, List<User> teamUserIDs) {
        try {
            return teamService.createTeam(userID, name, teamUserIDs);
        } catch (IllegalArgumentException e) {
            System.err.println("createTeam error: " + e.getMessage());
            return null;
        }
    }

}
