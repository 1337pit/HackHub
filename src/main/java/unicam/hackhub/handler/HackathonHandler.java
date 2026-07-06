package unicam.hackhub.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import unicam.hackhub.model.*;
import unicam.hackhub.model.state.ConcludedState;
import unicam.hackhub.model.state.EvaluationState;
import unicam.hackhub.model.state.InProgressState;
import unicam.hackhub.model.state.RegistrationState;
import unicam.hackhub.service.HackathonService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/handler/hackathons")
public class HackathonHandler {

    private final HackathonService hackathonService;

    public HackathonHandler(HackathonService hackathonService) {
        this.hackathonService = hackathonService;
    }

    /**
     * Gestisce la richiesta di creazione hackathon.
     */
    @PostMapping
    public ResponseEntity<Hackathon> createHackathon(@RequestBody Hackathon hackathon,
                                                     @RequestParam Long mentorID,
                                                     @RequestParam Long judgeID,
                                                     @RequestParam String stateName) {
        try {
            HackathonState state = resolveState(stateName);

            Hackathon created = hackathonService.createHackathon(
                    hackathon.getNameHackathon(), hackathon.getRulebook(),
                    hackathon.getRegistrationDeadline(), hackathon.getStartDate(),
                    hackathon.getEndDate(), hackathon.getLocation(), hackathon.getPrize(),
                    state, hackathon.getMaxTeamSize(), hackathon.getOrganizer(),
                    mentorID, judgeID);
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Gestisce la richiesta di modifica hackathon
     */
    @PutMapping("/{hackathonId}")
    public ResponseEntity<?> editHackathon(@PathVariable Long hackathonId,
                                                   @RequestParam(required = false) String name,
                                                   @RequestParam(required = false) String rulebook,
                                                   @RequestParam(required = false) LocalDate registrationDeadline,
                                                   @RequestParam(required = false) String location,
                                                   @RequestParam(required = false) String prize,
                                                   @RequestParam(required = false, defaultValue = "0") int maxTeamSize,
                                                   @RequestParam(required = false) Long judgeID,
                                                   @RequestParam(required = false) Long mentorID) {
        try {
            Hackathon edited = hackathonService.editHackathon(hackathonId, name, rulebook,
                    registrationDeadline, location, prize, maxTeamSize, judgeID, mentorID);
            return ResponseEntity.ok(edited);
        } catch (IllegalArgumentException e) {
            // Messaggio esposto nel body per debug, come già fatto in ReportHandler.
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Payload minimo per trasportare Judge e Mentor opzionali
     * nel body di editHackathon, evitando ambiguità con i query param.
     */
    public record EditHackathonStaffPayload(Judge judge, Mentor mentor) {}

    /**
     * Gestisce la richiesta di aggiungi mentore.
     * POST /api/handler/hackathons/5/mentors?email=mentor@example.com
     */
    @PostMapping("/{hackathonId}/mentors")
    public ResponseEntity<Mentor> addMentor(@PathVariable Long hackathonId,
                                          @RequestParam String email) {
        try {
            Mentor mentor = hackathonService.addMentor(email, hackathonId);
            return new ResponseEntity<>(mentor, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Gestisce la richiesta di visualizzazione degli hackathon
     * assegnati a un membro dello staff.
     * GET /api/handler/hackathons/staff/3
     */
    @GetMapping("/staff/{staffMemberId}")
    public ResponseEntity<List<Hackathon>> getAssignedHackathons(@PathVariable Long staffMemberId) {
        try {
            return ResponseEntity.ok(hackathonService.getAssignedHackathons(staffMemberId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Gestisce la visualizzazione dei partecipanti
     * di un hackathon assegnato al membro dello staff.
     * GET /api/handler/hackathons/5/participants?staffMemberId=3
     */
    @GetMapping("/{hackathonId}/participants")
    public ResponseEntity<List<Registration>> getParticipants(@PathVariable Long hackathonId,
                                                              @RequestParam Long staffMemberId) {
        try {
            return ResponseEntity.ok(hackathonService.getParticipants(staffMemberId, hackathonId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Gestisce la dichiarazione del team vincitore.
     * POST /api/handler/hackathons/winner?organizerId=1&teamId=5
     */
    @PostMapping("/winner")
    public ResponseEntity<Void> declareWinner(@RequestParam Long organizerId,
                                              @RequestParam Long teamId,
                                              @RequestParam Long hackathonId,
                                              @RequestParam double prizeAmount) {
        try {
            hackathonService.declareWinner(organizerId, teamId, hackathonId, prizeAmount);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Gestisce il cambio dello stato dell'hackathon.
     * HackathonState è un'interfaccia: Jackson non può deserializzarla
     * direttamente da JSON, quindi riceviamo il nome dello stato e
     * costruiamo l'istanza concreta lato server.
     * PUT /api/handler/hackathons/state?stateName=InProgressState
     */
    @PutMapping("/state")
    public ResponseEntity<Void> changeState(@RequestParam String stateName) {
        try {
            hackathonService.changeState(resolveState(stateName));
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Costruisce l'istanza concreta di HackathonState a partire dal nome.
     * Centralizza la logica usata sia da createHackathon che da changeState.
     */
    private HackathonState resolveState(String stateName) {
        return switch (stateName) {
            case "RegistrationState" -> new RegistrationState();
            case "InProgressState" -> new InProgressState();
            case "EvaluationState" -> new EvaluationState();
            case "ConcludedState" -> new ConcludedState();
            default -> throw new IllegalArgumentException("Unknown state: " + stateName);
        };
    }

    /**
     * Gestisce la richiesta di consultare le sottomissioni di un hackathon.
     * GET /api/handler/hackathons/5/submissions?staffMemberId=3
     */
    @GetMapping("/{hackathonId}/submissions")
    public ResponseEntity<List<Submission>> getSubmissions(@PathVariable Long hackathonId,
                                                           @RequestParam Long staffMemberId) {
        try {
            return ResponseEntity.ok(hackathonService.getSubmissions(staffMemberId, hackathonId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Gestisce la richiesta di visualizzare le richieste di supporto di un hackathon.
     * Usato nel caso d'uso "Visualizza Richieste Supporto" del Mentore.
     * GET /api/handler/hackathons/5/support-requests?mentorId=2
     */
    @GetMapping("/{hackathonId}/support-requests")
    public ResponseEntity<List<SupportRequest>> getRequestsSupport(@PathVariable Long hackathonId,
                                                                   @RequestParam Long mentorId) {
        try {
            return ResponseEntity.ok(hackathonService.getRequestsSupport(mentorId, hackathonId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}