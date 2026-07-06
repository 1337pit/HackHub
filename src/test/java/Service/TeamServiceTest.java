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
import unicam.hackhub.service.CalendarService;
import unicam.hackhub.service.InviteService;
import unicam.hackhub.service.TeamService;
import unicam.hackhub.service.UserService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private TeamRepository teamRepository;
    @Mock private HackathonRepository hackathonRepository;
    @Mock private StaffMemberRepository staffMemberRepository;
    @Mock private ReportRepository reportRepository;
    @Mock private InviteService inviteService;
    @Mock private UserService userService;
    @Mock private CalendarService calendarService;

    @InjectMocks
    private TeamService teamService;

    private User leader;
    private User memberA;
    private User memberB;
    private Team mockTeam;

    @BeforeEach
    void setUp() {
        leader  = new User(1L, "Alice", "alice@test.it");
        memberA = new User(2L, "Bob",   "bob@test.it");
        memberB = new User(3L, "Carlo", "carlo@test.it");
        mockTeam = mock(Team.class);
    }

    // =========================================================================
    // 1. Test per createTeam
    // =========================================================================

    @Test
    @DisplayName("createTeam – Utente non trovato → Lancia IllegalArgumentException 'User not found'")
    void createTeam_UserNotFound_ThrowsException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> teamService.createTeam(99L, "Alpha", List.of()));

        assertEquals("User not found", ex.getMessage());
    }

    @Test
    @DisplayName("createTeam – Nome già utilizzato → Lancia IllegalArgumentException")
    void createTeam_TeamNameAlreadyUsed_ThrowsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(leader));
        when(teamRepository.findByTeamName("Alpha")).thenReturn(Optional.of(mockTeam));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> teamService.createTeam(1L, "Alpha", List.of()));

        assertEquals("Team name is already used", ex.getMessage());
        verify(teamRepository, never()).save(any());
    }

    @Test
    @DisplayName("createTeam – Flusso di successo → Team creato e inviti spediti ai membri validi")
    void createTeam_Success_WithInvites() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(leader));
        when(teamRepository.findByTeamName("Alpha")).thenReturn(Optional.empty());
        when(userRepository.findById(2L)).thenReturn(Optional.of(memberA));

        Team result = teamService.createTeam(1L, "Alpha", List.of(memberA, leader)); // leader si auto-invita per test

        assertNotNull(result);
        assertEquals("Alpha", result.getTeamName());
        assertTrue(result.getMembers().contains(leader));

        verify(teamRepository).save(any(Team.class));
        verify(userRepository).save(leader);
        // Verifica inviti: inviato a memberA, saltato l'auto-invito del leader
        verify(inviteService, times(1)).createInvite(any(), eq(memberA));
        verify(inviteService, never()).createInvite(any(), eq(leader));
    }

    @Test
    @DisplayName("createTeam – Nome vuoto o composto da spazi → Lancia eccezione")
    void createTeam_BlankName_ThrowsException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> teamService.createTeam(1L, "   ", List.of()));

        assertEquals("Team name cannot be empty or blank", ex.getMessage());
    }

    // =========================================================================
    // 2. Test per deleteTeam
    // =========================================================================

    @Test
    @DisplayName("deleteTeam – Utente nel team corretto → Sgancia i membri ed elimina il team")
    void deleteTeam_Success() {
        Team teamToDelete = new Team(10L, "Alpha", new ArrayList<>(List.of(leader)));
        leader.setCurrentTeam(teamToDelete);

        when(teamRepository.findById(10L)).thenReturn(Optional.of(teamToDelete));
        when(userRepository.findById(1L)).thenReturn(Optional.of(leader));

        assertDoesNotThrow(() -> teamService.deleteTeam(1L, 10L));

        assertNull(leader.getCurrentTeam());
        verify(userRepository).save(leader);
        verify(teamRepository).delete(teamToDelete);
    }

    // =========================================================================
    // 3. Test per banTeam
    // =========================================================================

    @Test
    @DisplayName("banTeam – ID valido → Sgancia tutti i membri e svuota il team")
    void banTeam_Success() {
        List<User> members = new ArrayList<>(List.of(leader, memberA));
        Team team = new Team(10L, "Alpha", members);
        leader.setCurrentTeam(team);
        memberA.setCurrentTeam(team);

        when(teamRepository.findById(10L)).thenReturn(Optional.of(team));

        teamService.banTeam(10L);

        assertNull(leader.getCurrentTeam());
        assertNull(memberA.getCurrentTeam());
        assertTrue(team.getMembers().isEmpty());
        verify(teamRepository).save(team);
    }

    // =========================================================================
    // 4. Test per reportTeam
    // =========================================================================

    @Test
    @DisplayName("reportTeam – Mentore assegnato all'hackathon → Crea la segnalazione")
    void reportTeam_Success() {
        Mentor mentor = mock(Mentor.class);
        Hackathon hackathon = mock(Hackathon.class);

        when(mentor.getId()).thenReturn(3L);
        when(mockTeam.getHackathon()).thenReturn(hackathon);
        when(hackathon.getListMentors()).thenReturn(List.of(mentor));

        when(staffMemberRepository.findById(3L)).thenReturn(Optional.of(mentor));
        when(teamRepository.findById(10L)).thenReturn(Optional.of(mockTeam));
        when(reportRepository.save(any(Report.class))).thenReturn(mock(Report.class));

        Report report = teamService.reportTeam(3L, 10L, "Comportamento scorretto");

        assertNotNull(report);
        verify(reportRepository).save(any(Report.class));
    }

    @Test
    @DisplayName("reportTeam – Mentore non assegnato all'hackathon → Lancia IllegalArgumentException")
    void reportTeam_MentorNotAssigned_ThrowsException() {
        Mentor mentor = new Mentor();
        mentor.setId(3L);

        Mentor otherMentor = new Mentor();
        otherMentor.setId(99L);

        Hackathon hackathon = mock(Hackathon.class);

        // Configura l'hackathon per contenere SOLO l'altro mentore
        when(mockTeam.getHackathon()).thenReturn(hackathon);
        when(hackathon.getListMentors()).thenReturn(List.of(otherMentor));

        // Ora entrambi gli stubbing sono strettamente necessari e verranno consumati!
        when(staffMemberRepository.findById(3L)).thenReturn(Optional.of(mentor));
        when(teamRepository.findById(10L)).thenReturn(Optional.of(mockTeam));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> teamService.reportTeam(3L, 10L, "Comportamento scorretto"));

        assertEquals("Mentor is not assigned to this hackathon", ex.getMessage());
        verify(reportRepository, never()).save(any());
    }
    // =========================================================================
    // 5. Test per editTeamInfo
    // =========================================================================

    @Test
    @DisplayName("editTeamInfo – Dati validi ed utente nel team → Aggiorna il nome correttamente")
    void editTeamInfo_Success() {
        Team team = new Team(10L, "Old Name", List.of(leader));
        leader.setCurrentTeam(team);

        when(userRepository.findById(1L)).thenReturn(Optional.of(leader));
        when(teamRepository.findById(10L)).thenReturn(Optional.of(team));
        when(teamRepository.findByTeamName("New Name")).thenReturn(Optional.empty());
        when(teamRepository.save(team)).thenReturn(team);

        Team result = teamService.editTeamInfo(1L, 10L, "New Name");

        assertNotNull(result);
        assertEquals("New Name", result.getTeamName());
        verify(teamRepository).save(team);
    }

    @Test
    @DisplayName("editTeamInfo – Dati non validi o mancanti → Lancia IllegalArgumentException 'Invalid team data'")
    void editTeamInfo_InvalidData_ThrowsException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> teamService.editTeamInfo(null, 10L, "New Name"));

        assertEquals("Invalid team data", ex.getMessage());
        verify(teamRepository, never()).save(any());
    }
}