package unicam.hackhub.service;

import unicam.hackhub.model.*;
import unicam.hackhub.repository.HackathonRepository;
import unicam.hackhub.repository.ReportRepository;
import unicam.hackhub.repository.StaffMemberRepository;
import unicam.hackhub.repository.TeamRepository;

import java.util.List;

public class ReportService {

    private final ReportRepository reportRepository;
    private final HackathonRepository hackathonRepository;
    private final TeamRepository teamRepository;
    private final StaffMemberRepository staffMemberRepository;

    public ReportService(ReportRepository reportRepository,
                         HackathonRepository hackathonRepository,
                         TeamRepository teamRepository,
                         StaffMemberRepository staffMemberRepository) {
        this.reportRepository = reportRepository;
        this.hackathonRepository = hackathonRepository;
        this.teamRepository = teamRepository;
        this.staffMemberRepository = staffMemberRepository;
    }

    /**
     * Recupera tutte le segnalazioni di un hackathon.
     * Usato nel caso d'uso "Visualizza Segnalazioni" dell'Organizzatore.
     * 1. Verifica che l'hackathon esista
     * 2. Recupera le segnalazioni tramite ReportRepository
     */
    public List<Report> getReports(Long hackathonID) {
        if (hackathonID == null)
            throw new IllegalArgumentException("Hackathon ID cannot be null");

        Hackathon hackathon = hackathonRepository.findByID(hackathonID);
        if (hackathon == null)
            throw new IllegalArgumentException("Hackathon not found");

        List<Report> reports =
                reportRepository.findAllReportByHackathon(hackathonID);

        if (reports == null || reports.isEmpty())
            throw new IllegalArgumentException("No reports found");

        return reports;
    }

    /**
     * Crea una segnalazione per un team.
     * Usato nel caso d'uso "Segnala Team" del Mentore.
     * 1. Verifica dati validi
     * 2. Recupera team e hackathon
     * 3. Recupera il mentor
     * 4. Delega la creazione al Mentor
     * 5. Salva il report
     */
    public Report createReport(Long mentorID, Long teamID,
                                        Long hackathonID, String description) {
        if (mentorID == null || teamID == null
                || hackathonID == null || description == null
                || description.trim().isEmpty())
            throw new IllegalArgumentException("Invalid data");

        Hackathon hackathon = hackathonRepository.findByID(hackathonID);
        if (hackathon == null)
            throw new IllegalArgumentException("Hackathon not found");

        Team team = teamRepository.findByID(teamID);
        if (team == null)
            throw new IllegalArgumentException("Team not found");

        StaffMember staff = staffMemberRepository.findByID(mentorID);
        if (!(staff instanceof Mentor))
            throw new IllegalArgumentException("Mentor not found");
        Mentor mentor = (Mentor) staff;

        Report report = new Report(null, description,
                team, mentor, hackathon);
        reportRepository.save(report);

        return report;
    }

    /**
     * Modifica una segnalazione.
     * Usato nel caso d'uso "Modifica Segnalazione" del Mentore.
     * 1. Verifica dati validi
     * 2. Recupera report e mentore
     * 3. Verifica che la segnalazione sia stata creata dal medesimo Mentore.
     * 4. Delega la modifica al Report
     * 5. Salva il report modificato
     */
    public Report updateReport(Long reportID, Long mentorID, String description) {

        // 1. Verifica che i dati siano validi
        if (reportID == null || mentorID == null || description == null)
            throw new IllegalArgumentException("Invalid data");

        // 2. Recupera la segnalazione
        Report report = reportRepository.findByID(reportID);
        if (report == null)
            throw new IllegalArgumentException("Report not found");

        // 3. Recupera il mentore
        Mentor mentor = staffMemberRepository.getMentor(mentorID);
        if (mentor == null)
            throw new IllegalArgumentException("Mentor not found");

        // 4. Verifica che la segnalazione da modificare appartenga al medesimo Mentore
        if(!mentorID.equals(report.getMentor().getId()))
            throw new IllegalArgumentException("Mentor has not report");

        // 5. Delega la modifica della segnalazione al Report
        Report reportUpdated = report.updateReport(report, description);

        // 6. Salva la segnalazione aggiornata
        reportRepository.save(reportUpdated);

        return reportUpdated;
    }

}
