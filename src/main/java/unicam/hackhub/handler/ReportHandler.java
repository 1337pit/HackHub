package unicam.hackhub.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import unicam.hackhub.dto.ReportRequest;
import unicam.hackhub.model.Report;
import unicam.hackhub.service.ReportService;

import java.util.List;

@RestController
@RequestMapping("/api/handler/report")
public class ReportHandler {

    private final ReportService reportService;

    public ReportHandler(ReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * Visualizza tutte le segnalazioni di un determinato Hackathon.
     */
    @GetMapping("/hackathon/{hackathonId}")
    public ResponseEntity<List<Report>> getReports(@PathVariable Long hackathonId) {
        List<Report> reports = reportService.getReports(hackathonId);
        // Se la lista è vuota restituisce comunque un 200 OK con array vuato [], che è corretto in REST
        return ResponseEntity.ok(reports);
    }

    /**
     * Crea una nuova segnalazione per un team.
     */
    @PostMapping
    public ResponseEntity<Report> createReport(@RequestBody ReportRequest request) {
        try {
            Report newReport = reportService.createReport(
                    request.mentorId(),
                    request.teamId(),
                    request.hackathonId(),
                    request.description()
            );
            return new ResponseEntity<>(newReport, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Gestisce la richiesta di modifica segnalazione.
     */
    @PutMapping("/{reportId}")
    public ResponseEntity<Report> updateReport(@PathVariable Long reportId,
                                               @RequestParam Long mentorId,
                                               @RequestParam String description) {
        try {
            return ResponseEntity.ok(reportService.updateReport(reportId, mentorId, description));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Gestisce la richiesta di eliminazione segnalazione.
     */
    @DeleteMapping("/{reportId}")
    public ResponseEntity<Void> deleteReport(@PathVariable Long reportId,
                                             @RequestParam Long mentorId) {
        try {
            reportService.deleteReport(reportId, mentorId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

}