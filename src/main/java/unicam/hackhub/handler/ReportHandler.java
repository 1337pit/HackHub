package unicam.hackhub.handler;

import unicam.hackhub.model.Report;
import unicam.hackhub.service.ReportService;

import java.util.List;

public class ReportHandler {

    private final ReportService reportService;

    public ReportHandler(ReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * Gestisce la richiesta di visualizzazione segnalazioni.
     * Usato nel caso d'uso "Visualizza Segnalazioni" dell'Organizzatore.
     */
    public List<Report> getReports(Long hackathonID) {
        try {
            return reportService.getReports(hackathonID);
        } catch (IllegalArgumentException e) {
            System.err.println("getReports error: " + e.getMessage());
            return null;
        }
    }

    /**
     * Gestisce la richiesta di creazione segnalazione.
     * Usato nel caso d'uso "Segnala Team" del Mentore.
     */
    public Report createReport(Long mentorID, Long teamID,
                                        Long hackathonID, String description) {
        try {
            return reportService.createReport(mentorID, teamID, hackathonID, description);
        } catch (IllegalArgumentException e) {
            System.err.println("createReport error: " + e.getMessage());
            return null;
        }
    }
}
