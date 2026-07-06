package unicam.hackhub.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import unicam.hackhub.dto.SupportRequest;
import unicam.hackhub.model.Report;
import unicam.hackhub.model.Team;
import unicam.hackhub.model.User;
import unicam.hackhub.service.TeamService;
import unicam.hackhub.service.UserService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/teams")
public class TeamHandler {

    private final TeamService teamService;
    private final UserService userService;

    public TeamHandler(TeamService teamService, UserService userService) {
        this.teamService = teamService;
        this.userService = userService;
    }

    /**
     * Gestisce la richiesta di segnalazione team.
     */
    @PostMapping("/{teamId}/report")
    public ResponseEntity<Report> reportTeam(@PathVariable Long teamId,
                                             @RequestParam Long mentorId,
                                             @RequestParam String description) {
        try {
            Report report = teamService.reportTeam(mentorId, teamId, description);
            return new ResponseEntity<>(report, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping
    public ResponseEntity<Team> createTeam(@RequestBody SupportRequest request) {
        // Convertiamo la lista di ID ricevuti dal JSON in una lista di oggetti User per il Service
        List<User> invitedUsers = new ArrayList<>();
        if (request.invitedUserIds() != null) {
            for (Long id : request.invitedUserIds()) {
                try {
                    invitedUsers.add(userService.getUser(id));
                } catch (IllegalArgumentException e) {
                    // Se un utente invitato non esiste, decidiamo se saltarlo o fallire.
                    // Avendo irrobustito il service, saltarlo qui è coerente.
                }
            }
        }

        Team newTeam = teamService.createTeam(request.leaderId(), request.name(), invitedUsers);
        return new ResponseEntity<>(newTeam, HttpStatus.CREATED);
    }

    /**
     * Gestisce la richiesta di eliminazione team.
     */
    @DeleteMapping
    public ResponseEntity<Void> deleteTeam(@RequestParam Long userID, @RequestParam Long teamID) {
        try {
            teamService.deleteTeam(userID, teamID);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Richiede il supporto di un mentor per il team, prenotando una data.
     */
    @PostMapping("/support")
    public ResponseEntity<unicam.hackhub.model.SupportRequest> requestSupport(
            @RequestParam Long hackathonId,
            @RequestParam Long userId,
            @RequestParam Long teamId,
            @RequestParam Long mentorId,
            @RequestParam java.time.LocalDate date) {
        try {
            unicam.hackhub.model.SupportRequest request =
                    teamService.requiresAssistance(hackathonId, userId, teamId, mentorId, date);
            return new ResponseEntity<>(request, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Team> getTeamById(@PathVariable Long id) {
        Team team = teamService.getTeamByID(id);
        if (team == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(team);
    }
}