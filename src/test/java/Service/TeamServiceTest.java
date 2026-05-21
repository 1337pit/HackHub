package Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.matchers.Null;
import org.mockito.junit.jupiter.MockitoExtension;
import unicam.hackhub.model.Invite;
import unicam.hackhub.model.Team;
import unicam.hackhub.model.User;
import unicam.hackhub.repository.InviteRepository;
import unicam.hackhub.repository.TeamRepository;
import unicam.hackhub.repository.UserRepository;
import unicam.hackhub.service.InviteService;
import unicam.hackhub.service.TeamService;
import unicam.hackhub.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test unitari per TeamService.
 *
 * Copre tutti i flussi del sequence diagram "createTeam":
 *  - [break] User not found
 *  - [break] User already in a team
 *  - [break] Team name already used
 *  - [opt/loop] Inviti ai membri iniziali
 *  - Happy path: team creato con successo
 */
@ExtendWith(MockitoExtension.class)
class TeamServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private InviteService inviteService;

    @Mock
    private UserService userService;

    // Non si può usare @InjectMocks perché TeamService non ha un costruttore no-arg;
    // istanziamo manualmente nel @BeforeEach.
    private TeamService teamService;

    private User leader;
    private User memberA;
    private User memberB;

    @BeforeEach
    void setUp() {
        teamService = new TeamService(userRepository, teamRepository, inviteService, userService);

        leader  = new User(1L, "Alice", "alice@test.it");
        memberA = new User(2L, "Bob",   "bob@test.it");
        memberB = new User(3L, "Carlo", "carlo@test.it");
    }

    // -----------------------------------------------------------------------
    // BREAK: User not found
    // -----------------------------------------------------------------------

    /**
     * Scenario: userRepository restituisce null → checkEligibility riceve null →
     * NullPointerException (bug noto: TeamService non fa il null-check prima
     * di delegare a UserService).
     *
     * Il test documenta il comportamento attuale. Per allinearlo al diagramma
     * ("User not found") si dovrebbe aggiungere:
     *   if (leader == null) throw new IllegalArgumentException("User not found");
     * prima della chiamata a checkEligibility.
     */
    @Test
    @DisplayName("[break] User not found → NullPointerException (comportamento attuale)")
    void createTeam_userNotFound_throwsNPE() {
        when(userRepository.findByID(99L)).thenReturn(null);
        assertThrows(NullPointerException.class,
                () -> teamService.createTeam(99L, "Alpha", List.of()));
    }

    // -----------------------------------------------------------------------
    // BREAK: User already in a team
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("[break] User already in a team → IllegalArgumentException")
    void createTeam_userAlreadyInTeam_throwsException() {
        Team existingTeam = new Team(10L, "OldTeam", List.of(leader));
        leader.setCurrentTeam(existingTeam); // leader.hasTeam() == true

        when(userRepository.findByID(1L)).thenReturn(leader);

        // checkEligibility è il metodo reale di UserService; usiamo il mock
        doThrow(new IllegalArgumentException("User already in a team"))
                .when(userService).checkEligibility(leader);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> teamService.createTeam(1L, "NewTeam", List.of()));

        assertEquals("User already in a team", ex.getMessage());
        verify(teamRepository, never()).save(any());
    }

    // -----------------------------------------------------------------------
    // BREAK: Team name already used
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("[break] Team name already used → IllegalArgumentException")
    void createTeam_teamNameAlreadyUsed_throwsException() {
        when(userRepository.findByID(1L)).thenReturn(leader);
        doNothing().when(userService).checkEligibility(leader); // leader è eleggibile

        Team duplicateTeam = new Team(5L, "Alpha", List.of());
        when(teamRepository.findByName("Alpha")).thenReturn(duplicateTeam);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> teamService.createTeam(1L, "Alpha", List.of()));

        assertEquals("Team name is already used", ex.getMessage());
        verify(teamRepository, never()).save(any());
    }

    // -----------------------------------------------------------------------
    // Happy path – solo leader, nessun invito
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Happy path – team creato, leader aggiornato, nessun invito")
    void createTeam_success_noInvites() {
        when(userRepository.findByID(1L)).thenReturn(leader);
        doNothing().when(userService).checkEligibility(leader);
        when(teamRepository.findByName("Alpha")).thenReturn(null);
        when(teamRepository.save(any(Team.class))).thenAnswer(inv -> inv.getArgument(0));
        when(userRepository.save(leader)).thenReturn(leader);

        Team result = teamService.createTeam(1L, "Alpha", null);

        assertNotNull(result);
        assertEquals("Alpha", result.getTeamName());
        assertTrue(result.getMembers().contains(leader));

        verify(teamRepository).save(result);
        verify(userRepository).save(leader);
        verify(inviteService, never()).createInvite(anyLong(), any());
    }

    // -----------------------------------------------------------------------
    // [opt / loop] Inviti ai membri iniziali
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("[opt/loop] Team creato con lista membri → inviti inviati")
    void createTeam_withMembers_invitesSent() {
        when(userRepository.findByID(1L)).thenReturn(leader);
        doNothing().when(userService).checkEligibility(leader);
        when(teamRepository.findByName("Alpha")).thenReturn(null);
        when(teamRepository.save(any(Team.class))).thenAnswer(inv -> inv.getArgument(0));
        when(userRepository.save(leader)).thenReturn(leader);

        when(userRepository.findByID(2L)).thenReturn(memberA);
        when(userRepository.findByID(3L)).thenReturn(memberB);

        Invite fakeInvite = new Invite();
        when(inviteService.createInvite(anyLong(), any(User.class))).thenReturn(fakeInvite);

        Team result = teamService.createTeam(1L, "Alpha", List.of(memberA, memberB));

        assertNotNull(result);
        // Un invito per ognuno dei due membri
        verify(inviteService, times(1)).createInvite(result.getId(), memberA);
        verify(inviteService, times(1)).createInvite(result.getId(), memberB);
        verify(inviteService, times(2)).createInvite(anyLong(), any(User.class));
    }

    @Test
    @DisplayName("[opt/loop] Membro non trovato in repository → invito saltato")
    void createTeam_memberNotFound_inviteSkipped() {
        when(userRepository.findByID(1L)).thenReturn(leader);
        doNothing().when(userService).checkEligibility(leader);
        when(teamRepository.findByName("Alpha")).thenReturn(null);
        when(teamRepository.save(any(Team.class))).thenAnswer(inv -> inv.getArgument(0));
        when(userRepository.save(leader)).thenReturn(leader);

        when(userRepository.findByID(2L)).thenReturn(null); // memberA non trovato
        when(userRepository.findByID(3L)).thenReturn(memberB);

        when(inviteService.createInvite(anyLong(), any(User.class))).thenReturn(new Invite());

        teamService.createTeam(1L, "Alpha", List.of(memberA, memberB));

        // Solo memberB deve ricevere l'invito
        verify(inviteService, times(1)).createInvite(anyLong(), eq(memberB));
        verify(inviteService, never()).createInvite(anyLong(), eq(memberA));
    }

    // -----------------------------------------------------------------------
    // Verifica stato del team restituito
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Il team restituito contiene solo il leader come membro iniziale")
    void createTeam_returnedTeam_containsOnlyLeader() {
        when(userRepository.findByID(1L)).thenReturn(leader);
        doNothing().when(userService).checkEligibility(leader);
        when(teamRepository.findByName("Beta")).thenReturn(null);
        when(teamRepository.save(any(Team.class))).thenAnswer(inv -> inv.getArgument(0));
        when(userRepository.save(leader)).thenReturn(leader);
        when(userRepository.findByID(2L)).thenReturn(memberA);
        when(inviteService.createInvite(anyLong(), any())).thenReturn(new Invite());

        Team result = teamService.createTeam(1L, "Beta", List.of(memberA));

        assertEquals(1, result.getMembers().size(),
                "I membri invitati non devono essere aggiunti al team finché non accettano");
        assertTrue(result.getMembers().contains(leader));
    }

    // 1. Correggiamo il test del fallimento per allinearlo alla logica reale
    @Test
    @DisplayName("[break] User already in a team → Gestito dal controllo interno di TeamService")
    void createTeam_userAlreadyInTeam_checkedByService() {
        Team existingTeam = new Team(10L, "OldTeam", List.of(leader));
        leader.setCurrentTeam(existingTeam);

        when(userRepository.findByID(1L)).thenReturn(leader);
        doNothing().when(userService).checkEligibility(leader); // Il mock non fa nulla

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> teamService.createTeam(1L, "NewTeam", List.of()));

        assertEquals("User already in a team", ex.getMessage());
    }

    // 2. Test per l'auto-invito del leader
    @Test
    @DisplayName("Se il leader è presente nella lista invitati, non deve ricevere l'invito")
    void createTeam_leaderInInviteList_shouldSkipLeader() {
        when(userRepository.findByID(1L)).thenReturn(leader);
        doNothing().when(userService).checkEligibility(leader);
        when(teamRepository.findByName("Alpha")).thenReturn(null);
        when(teamRepository.save(any(Team.class))).thenAnswer(inv -> inv.getArgument(0));

        // Il leader prova a invitare se stesso
        Team result = teamService.createTeam(1L, "Alpha", List.of(leader));

        // Verifica che non sia mai stato creato un invito per il leader (ID 1L)
        verify(inviteService, never()).createInvite(anyLong(), eq(leader));
    }

    // 3. Test per la validazione del nome stringa vuota
    @Test
    @DisplayName("Nome team composto da soli spazi → IllegalArgumentException")
    void createTeam_blankName_throwsException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> teamService.createTeam(1L, "   ", List.of()));

        assertEquals("Team name cannot be empty or blank", ex.getMessage());
    }
}
