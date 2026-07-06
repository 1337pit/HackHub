package unicam.hackhub.service;

import org.springframework.stereotype.Service;
import unicam.hackhub.model.*;
import unicam.hackhub.repository.HackathonRepository;
import unicam.hackhub.repository.ReportRepository;
import unicam.hackhub.repository.StaffMemberRepository;
import unicam.hackhub.repository.TeamRepository;

import java.util.List;

@Service
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
     */
    public List<Report> getReports(Long hackathonID) {
        if (hackathonID == null)
            throw new IllegalArgumentException("Hackathon ID cannot be null");

        Hackathon hackathon = hackathonRepository.findById(hackathonID)
                .orElseThrow(() -> new IllegalArgumentException("Hackathon not found"));

        return reportRepository.findByHackathon(hackathon);
    }

    /**
     * Crea una segnalazione per un team.
     */
    public Report createReport(Long mentorID, Long teamID,
                               Long hackathonID, String description) {
        if (mentorID == null || teamID == null
                || hackathonID == null || description == null
                || description.trim().isEmpty())
            throw new IllegalArgumentException("Invalid data");

        Hackathon hackathon = hackathonRepository.findById(hackathonID)
                .orElseThrow(() -> new IllegalArgumentException("Hackathon not found"));

        Team team = teamRepository.findById(teamID)
                .orElseThrow(() -> new IllegalArgumentException("Team not found"));

        Mentor mentor = (Mentor) staffMemberRepository.findById(mentorID)
                .filter(s -> s instanceof Mentor)
                .orElseThrow(() -> new IllegalArgumentException("Mentor not found"));

        Report report = new Report(null, description, team, mentor, hackathon);
        return reportRepository.save(report);
    }

    /**
     * Modifica una segnalazione.
     */
    public Report updateReport(Long reportID, Long mentorID, String description) {

        if (reportID == null || mentorID == null
                || description == null || description.trim().isEmpty())
            throw new IllegalArgumentException("Invalid data");

        Report report = reportRepository.findById(reportID)
                .orElseThrow(() -> new IllegalArgumentException("Report not found"));

        Mentor mentor = (Mentor) staffMemberRepository.findById(mentorID)
                .filter(s -> s instanceof Mentor)
                .orElseThrow(() -> new IllegalArgumentException("Mentor not found"));

        if (!mentor.getId().equals(report.getMentor().getId()))
            throw new IllegalArgumentException("Mentor has not report");

        report.setDescription(description);
        return reportRepository.save(report);
    }

    /**
     * Elimina una segnalazione.
     */
    public void deleteReport(Long reportID, Long mentorID) {

        if (reportID == null || mentorID == null)
            throw new IllegalArgumentException("Invalid data");

        Report report = reportRepository.findById(reportID)
                .orElseThrow(() -> new IllegalArgumentException("Report not found"));

        Mentor mentor = (Mentor) staffMemberRepository.findById(mentorID)
                .filter(s -> s instanceof Mentor)
                .orElseThrow(() -> new IllegalArgumentException("Mentor not found"));

        if (!mentor.getId().equals(report.getMentor().getId()))
            throw new IllegalArgumentException("Mentor has not report");

        reportRepository.delete(report);
    }

}