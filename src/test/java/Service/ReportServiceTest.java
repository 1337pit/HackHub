package Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import unicam.hackhub.model.*;
import unicam.hackhub.repository.*;
import unicam.hackhub.service.ReportService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test unitari per ReportService.
 *
 * Copre i flussi di:
 * - getReports (Visualizza Segnalazioni)
 * - createReport (Segnala Team)
 */
@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock private ReportRepository reportRepository;
    @Mock private HackathonRepository hackathonRepository;
    @Mock private TeamRepository teamRepository;
    @Mock private StaffMemberRepository staffMemberRepository;

    private ReportService reportService;

    private Hackathon hackathon;
    private Team team;
    private Mentor mentor;

    @BeforeEach
    void setUp() {
        reportService = new ReportService(
                reportRepository, hackathonRepository,
                teamRepository, staffMemberRepository
        );

        hackathon = new Hackathon(1L, "HackHub Test");
        team = new Team(2L, "Team Alpha", List.of());
        mentor = new Mentor(3L, "Mentor Mario", "mentor@example.com", hackathon);
    }

    // =========================================================================
    // getReports TESTS
    // =========================================================================

    @Test
    @DisplayName("getReports - hackathon con segnalazioni → lista ViolationReport")
    void getReports_HackathonWithReports_ReturnsList() {
        Report report = new Report(1L, "Bad behavior", team, mentor, hackathon);

        when(hackathonRepository.findByID(1L)).thenReturn(hackathon);
        when(reportRepository.findAllReportByHackathon(1L)).thenReturn(List.of(report));

        List<Report> result = reportService.getReports(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Bad behavior", result.get(0).getDescription());

        verify(hackathonRepository).findByID(1L);
        verify(reportRepository).findAllReportByHackathon(1L);
    }

    @Test
    @DisplayName("getReports - hackathonID null → IllegalArgumentException")
    void getReports_NullID_ThrowsException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> reportService.getReports(null));

        assertEquals("Hackathon ID cannot be null", ex.getMessage());
        verify(hackathonRepository, never()).findByID(any());
    }

    @Test
    @DisplayName("getReports - hackathon non trovato → IllegalArgumentException")
    void getReports_HackathonNotFound_ThrowsException() {
        when(hackathonRepository.findByID(99L)).thenReturn(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> reportService.getReports(99L));

        assertEquals("Hackathon not found", ex.getMessage());
        verify(reportRepository, never()).findAllReportByHackathon(any());
    }

    @Test
    @DisplayName("getReports - nessuna segnalazione → IllegalArgumentException")
    void getReports_NoReports_ThrowsException() {
        when(hackathonRepository.findByID(1L)).thenReturn(hackathon);
        when(reportRepository.findAllReportByHackathon(1L)).thenReturn(List.of());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> reportService.getReports(1L));

        assertEquals("No reports found", ex.getMessage());
    }

    @Test
    @DisplayName("getReports - più segnalazioni → lista completa")
    void getReports_MultipleReports_ReturnsAll() {
        Report r1 = new Report(1L, "Behavior 1", team, mentor, hackathon);
        Report r2 = new Report(2L, "Behavior 2", team, mentor, hackathon);

        when(hackathonRepository.findByID(1L)).thenReturn(hackathon);
        when(reportRepository.findAllReportByHackathon(1L)).thenReturn(List.of(r1, r2));

        List<Report> result = reportService.getReports(1L);

        assertEquals(2, result.size());
    }

    // =========================================================================
    // createReport TESTS
    // =========================================================================

    @Test
    @DisplayName("createReport - dati validi → ViolationReport creato e salvato")
    void createReport_ValidData_ReturnsReport() {
        when(hackathonRepository.findByID(1L)).thenReturn(hackathon);
        when(teamRepository.findByID(2L)).thenReturn(team);
        when(staffMemberRepository.findByID(3L)).thenReturn(mentor);
        when(reportRepository.save(any(Report.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Report result = reportService.createReport(3L, 2L, 1L, "Bad behavior");

        assertNotNull(result);
        assertEquals("Bad behavior", result.getDescription());
        assertEquals(team, result.getTeam());
        assertEquals(mentor, result.getMentor());
        assertEquals(hackathon, result.getHackathon());

        verify(reportRepository).save(any(Report.class));
    }

    @Test
    @DisplayName("createReport - parametri null → IllegalArgumentException")
    void createReport_NullParams_ThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> reportService.createReport(null, 2L, 1L, "desc"));

        assertThrows(IllegalArgumentException.class,
                () -> reportService.createReport(3L, null, 1L, "desc"));

        assertThrows(IllegalArgumentException.class,
                () -> reportService.createReport(3L, 2L, null, "desc"));

        assertThrows(IllegalArgumentException.class,
                () -> reportService.createReport(3L, 2L, 1L, null));

        assertThrows(IllegalArgumentException.class,
                () -> reportService.createReport(3L, 2L, 1L, ""));

        verify(reportRepository, never()).save(any());
    }

    @Test
    @DisplayName("createReport - hackathon non trovato → IllegalArgumentException")
    void createReport_HackathonNotFound_ThrowsException() {
        when(hackathonRepository.findByID(1L)).thenReturn(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> reportService.createReport(3L, 2L, 1L, "Bad behavior"));

        assertEquals("Hackathon not found", ex.getMessage());
        verify(reportRepository, never()).save(any());
    }

    @Test
    @DisplayName("createReport - team non trovato → IllegalArgumentException")
    void createReport_TeamNotFound_ThrowsException() {
        when(hackathonRepository.findByID(1L)).thenReturn(hackathon);
        when(teamRepository.findByID(2L)).thenReturn(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> reportService.createReport(3L, 2L, 1L, "Bad behavior"));

        assertEquals("Team not found", ex.getMessage());
        verify(reportRepository, never()).save(any());
    }

    @Test
    @DisplayName("createReport - mentor non trovato → IllegalArgumentException")
    void createReport_MentorNotFound_ThrowsException() {
        when(hackathonRepository.findByID(1L)).thenReturn(hackathon);
        when(teamRepository.findByID(2L)).thenReturn(team);
        when(staffMemberRepository.findByID(3L)).thenReturn(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> reportService.createReport(3L, 2L, 1L, "Bad behavior"));

        assertEquals("Mentor not found", ex.getMessage());
        verify(reportRepository, never()).save(any());
    }

    @Test
    @DisplayName("createReport - staff non è un Mentor → IllegalArgumentException")
    void createReport_StaffNotMentor_ThrowsException() {
        Judge judge = new Judge(3L, "Judge Joe");
        when(hackathonRepository.findByID(1L)).thenReturn(hackathon);
        when(teamRepository.findByID(2L)).thenReturn(team);
        when(staffMemberRepository.findByID(3L)).thenReturn(judge);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> reportService.createReport(3L, 2L, 1L, "Bad behavior"));

        assertEquals("Mentor not found", ex.getMessage());
        verify(reportRepository, never()).save(any());
    }

    @Test
    @DisplayName("createReport - description blank → IllegalArgumentException")
    void createReport_BlankDescription_ThrowsException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> reportService.createReport(3L, 2L, 1L, "   "));

        assertEquals("Invalid data", ex.getMessage());
        verify(reportRepository, never()).save(any());
    }
}