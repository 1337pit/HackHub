package Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import unicam.hackhub.model.*;
import unicam.hackhub.repository.*;
import unicam.hackhub.service.ReportService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock private ReportRepository reportRepository;
    @Mock private HackathonRepository hackathonRepository;
    @Mock private TeamRepository teamRepository;
    @Mock private StaffMemberRepository staffMemberRepository;

    @InjectMocks
    private ReportService reportService;

    private Hackathon mockHackathon;
    private Team mockTeam;
    private Mentor mockMentor;
    private Report mockReport;

    @BeforeEach
    void setUp() {
        mockHackathon = mock(Hackathon.class);
        mockTeam = mock(Team.class);
        mockMentor = mock(Mentor.class);
        mockReport = mock(Report.class);

        // Rimosso lo stubbing globale da qui per evitare UnnecessaryStubbingException
    }

    // =========================================================================
    // 1. Test per getReports
    // =========================================================================

    @Test
    @DisplayName("getReports – Hackathon valido → Ritorna la lista delle segnalazioni")
    void getReports_Success() {
        when(hackathonRepository.findById(1L)).thenReturn(Optional.of(mockHackathon));
        when(reportRepository.findByHackathon(mockHackathon)).thenReturn(List.of(mockReport));

        List<Report> result = reportService.getReports(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(hackathonRepository).findById(1L);
        verify(reportRepository).findByHackathon(mockHackathon);
    }

    @Test
    @DisplayName("getReports – HackathonID null → Lancia IllegalArgumentException")
    void getReports_NullId_ThrowsException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> reportService.getReports(null));

        assertEquals("Hackathon ID cannot be null", ex.getMessage());
        verify(hackathonRepository, never()).findById(any());
    }

    @Test
    @DisplayName("getReports – Hackathon non trovato → Lancia IllegalArgumentException")
    void getReports_NotFound_ThrowsException() {
        when(hackathonRepository.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> reportService.getReports(99L));

        assertEquals("Hackathon not found", ex.getMessage());
    }

    // =========================================================================
    // 2. Test per createReport
    // =========================================================================

    @Test
    @DisplayName("createReport – Dati validi → Segnalazione salvata con successo")
    void createReport_Success() {
        when(hackathonRepository.findById(1L)).thenReturn(Optional.of(mockHackathon));
        when(teamRepository.findById(2L)).thenReturn(Optional.of(mockTeam));
        when(staffMemberRepository.findById(3L)).thenReturn(Optional.of(mockMentor));
        when(reportRepository.save(any(Report.class))).thenReturn(mockReport);

        Report result = reportService.createReport(3L, 2L, 1L, "Bad behavior");

        assertNotNull(result);
        verify(reportRepository).save(any(Report.class));
    }

    @Test
    @DisplayName("createReport – Dati invalidi o vuoti → Lancia IllegalArgumentException 'Invalid data'")
    void createReport_InvalidData_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> reportService.createReport(null, 2L, 1L, "desc"));
        assertThrows(IllegalArgumentException.class, () -> reportService.createReport(3L, 2L, 1L, "   "));

        verify(reportRepository, never()).save(any());
    }

    @Test
    @DisplayName("createReport – Staff trovato non è un istanza di Mentor → Lancia eccezione")
    void createReport_StaffNotMentor_ThrowsException() {
        Judge mockJudge = mock(Judge.class);
        when(hackathonRepository.findById(1L)).thenReturn(Optional.of(mockHackathon));
        when(teamRepository.findById(2L)).thenReturn(Optional.of(mockTeam));
        when(staffMemberRepository.findById(3L)).thenReturn(Optional.of(mockJudge));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> reportService.createReport(3L, 2L, 1L, "Bad behavior"));

        assertEquals("Mentor not found", ex.getMessage());
    }

    // =========================================================================
    // 3. Test per updateReport
    // =========================================================================

    @Test
    @DisplayName("updateReport – Mentore autorizzato → Modifica la descrizione con successo")
    void updateReport_Success() {
        // Spostato qui solo dove serve realmente
        when(mockMentor.getId()).thenReturn(3L);

        when(reportRepository.findById(10L)).thenReturn(Optional.of(mockReport));
        when(staffMemberRepository.findById(3L)).thenReturn(Optional.of(mockMentor));
        when(mockReport.getMentor()).thenReturn(mockMentor);
        when(reportRepository.save(mockReport)).thenReturn(mockReport);

        Report updated = reportService.updateReport(10L, 3L, "New Description");

        assertNotNull(updated);
        verify(mockReport).setDescription("New Description");
        verify(reportRepository).save(mockReport);
    }

    @Test
    @DisplayName("updateReport – Mentore non proprietario del report → Lancia IllegalArgumentException")
    void updateReport_UnauthorizedMentor_ThrowsException() {
        // Spostato qui solo dove serve realmente
        when(mockMentor.getId()).thenReturn(3L);

        Mentor maliciousMentor = mock(Mentor.class);
        when(maliciousMentor.getId()).thenReturn(99L);

        when(reportRepository.findById(10L)).thenReturn(Optional.of(mockReport));
        when(staffMemberRepository.findById(99L)).thenReturn(Optional.of(maliciousMentor));
        when(mockReport.getMentor()).thenReturn(mockMentor);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> reportService.updateReport(10L, 99L, "New Description"));

        assertEquals("Mentor has not report", ex.getMessage());
        verify(reportRepository, never()).save(mockReport);
    }

    // =========================================================================
    // 4. Test per deleteReport
    // =========================================================================

    @Test
    @DisplayName("deleteReport – Mentore autorizzato → Elimina la segnalazione")
    void deleteReport_Success() {
        // Spostato qui solo dove serve realmente
        when(mockMentor.getId()).thenReturn(3L);

        when(reportRepository.findById(10L)).thenReturn(Optional.of(mockReport));
        when(staffMemberRepository.findById(3L)).thenReturn(Optional.of(mockMentor));
        when(mockReport.getMentor()).thenReturn(mockMentor);

        assertDoesNotThrow(() -> reportService.deleteReport(10L, 3L));

        verify(reportRepository).delete(mockReport);
    }
}