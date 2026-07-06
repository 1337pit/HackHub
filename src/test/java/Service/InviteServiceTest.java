package Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import unicam.hackhub.model.Invite;
import unicam.hackhub.model.Team;
import unicam.hackhub.model.User;
import unicam.hackhub.model.enums.InviteState;
import unicam.hackhub.repository.InviteRepository;
import unicam.hackhub.repository.TeamRepository;
import unicam.hackhub.repository.UserRepository;
import unicam.hackhub.service.InviteService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InviteServiceTest {

    @Mock
    private InviteRepository inviteRepository;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private InviteService inviteService;

    private User invitedUser;
    private Team team;
    private Invite mockInvite;

    @BeforeEach
    void setUp() {
        invitedUser = new User(2L, "Bob", "bob@test.it");

        // Usiamo un'ArrayList modificabile per i membri del team
        team = new Team(1L, "Alpha", new ArrayList<>());

        mockInvite = mock(Invite.class);
    }

    // =========================================================================
    // 1. Test per createInvite
    // =========================================================================

    @Test
    @DisplayName("createInvite – Dati validi → Invito creato e salvato con successo")
    void createInvite_Success() {
        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
        when(userRepository.findById(2L)).thenReturn(Optional.of(invitedUser));
        when(inviteRepository.save(any(Invite.class))).thenAnswer(inv -> inv.getArgument(0));

        Invite result = inviteService.createInvite(1L, invitedUser);

        assertNotNull(result);
        assertEquals(invitedUser, result.getInvitedUser());
        assertEquals(team, result.getTeam());
        verify(inviteRepository).save(any(Invite.class));
    }

    @Test
    @DisplayName("createInvite – Team non trovato → Lancia IllegalArgumentException")
    void createInvite_TeamNotFound_ThrowsException() {
        when(teamRepository.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> inviteService.createInvite(99L, invitedUser));

        assertEquals("Team not found", ex.getMessage());
        verify(inviteRepository, never()).save(any());
    }

    @Test
    @DisplayName("createInvite – Utente non registrato → Lancia IllegalArgumentException")
    void createInvite_UserNotRegistered_ThrowsException() {
        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> inviteService.createInvite(1L, invitedUser));

        assertEquals("User not registered", ex.getMessage());
        verify(inviteRepository, never()).save(any());
    }

    // =========================================================================
    // 2. Test per acceptInvite
    // =========================================================================

    @Test
    @DisplayName("acceptInvite – Stato PENDING → Passa ad ACCEPTED e aggiunge l'utente al team")
    void acceptInvite_Success() {
        when(inviteRepository.findById(10L)).thenReturn(Optional.of(mockInvite));
        when(mockInvite.getStatus()).thenReturn(InviteState.PENDING);
        when(mockInvite.getInvitedUser()).thenReturn(invitedUser);
        when(mockInvite.getTeam()).thenReturn(team);

        inviteService.acceptInvite(10L);

        verify(mockInvite).setStatus(InviteState.ACCEPTED);
        assertTrue(team.getMembers().contains(invitedUser));
        assertEquals(team, invitedUser.getCurrentTeam());
        verify(inviteRepository).save(mockInvite);
    }

    @Test
    @DisplayName("acceptInvite – Invito già elaborato (non PENDING) → Lancia eccezione")
    void acceptInvite_AlreadyProcessed_ThrowsException() {
        when(inviteRepository.findById(10L)).thenReturn(Optional.of(mockInvite));
        when(mockInvite.getStatus()).thenReturn(InviteState.ACCEPTED); // Già accettato

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> inviteService.acceptInvite(10L));

        assertEquals("Invite already processed", ex.getMessage());
        verify(inviteRepository, never()).save(any());
    }

    // =========================================================================
    // 3. Test per declineInvite
    // =========================================================================

    @Test
    @DisplayName("declineInvite – Stato PENDING → Passa a REFUSED correttamente")
    void declineInvite_Success() {
        when(inviteRepository.findById(20L)).thenReturn(Optional.of(mockInvite));
        when(mockInvite.getStatus()).thenReturn(InviteState.PENDING);

        inviteService.declineInvite(20L);

        verify(mockInvite).setStatus(InviteState.REFUSED);
        verify(inviteRepository).save(mockInvite);
    }
}