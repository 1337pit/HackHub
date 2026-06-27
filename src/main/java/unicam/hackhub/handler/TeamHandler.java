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

    /**
     * Gestisce la richiesta di eliminazione team.
     * Corrisponde al metodo deleteTeam nel sequence diagram.
     *
     * @param userID     ID del memebro del team
     * @param teamID     ID del team da eliminare
     */
    public void deleteTeam(Long userID, Long teamID) {
        try {
            teamService.deleteTeam(userID, teamID);
        } catch (IllegalArgumentException e) {
            System.err.println("deleteTeam error: " + e.getMessage());
        }
    }

    /**
     * Gestisce la richiesta di bandire un team.
     *
     * @param teamID ID del team da bandire
     */
    public void banTeam(Long teamID) {
        try {
            teamService.banTeam(teamID);
        } catch (IllegalArgumentException e) {
            System.err.println("banTeam error: " + e.getMessage());
        }
    }

    /**
     * Gestisce la modifica delle informazioni di un team.
     *
     * @param userID  ID dell'utente
     * @param teamID  ID del team
     * @param newName nuovo nome del team
     * @return team modificato oppure null in caso di errore
     */
    public Team editTeamInfo(Long userID, Long teamID, String newName) {
        try {
            return teamService.editTeamInfo(userID, teamID, newName);
        } catch (IllegalArgumentException e) {
            System.err.println("editTeamInfo error: " + e.getMessage());
            return null;
        }
    }

    /**
     * Gestisce la richiesta di creazione richiesta supporto.
     * Corrisponde al metodo requiresAssistance nel sequence diagram.
     * @param id        Id della richiesta di supporto
     * @param hackathonID  Id dell'hackathon
     * @param userID  Id del membro del team
     * @param teamID  Id del team
     * @param mentorID  Id del mentore
     */
    public void requiresAssistance(Long id, Long hackathonID, Long userID, Long teamID, Long mentorID) {
        try {
            teamService.requiresAssistance(id, hackathonID, userID, teamID, mentorID);
        } catch (IllegalArgumentException e) {
            System.err.println("requiresAssistance error: " + e.getMessage());
        }
    }

}
