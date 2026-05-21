package Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import unicam.hackhub.model.Invite;
import unicam.hackhub.model.Team;
import unicam.hackhub.model.User;
import unicam.hackhub.repository.InviteRepository;
import unicam.hackhub.repository.TeamRepository;
import unicam.hackhub.repository.UserRepository;
import unicam.hackhub.service.InviteService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test unitari per InviteService.
 *
 * Copre il metodo createInvite usato nel loop del caso d'uso createTeam.
 */
@ExtendWith(MockitoExtension.class)
class InviteServiceTest {

    @Mock
    private InviteRepository inviteRepository;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private UserRepository userRepository;

    private InviteService inviteService;

    private User invitedUser;
    private Team team;

    @BeforeEach
    void setUp() {
        inviteService = new InviteService(inviteRepository, teamRepository, userRepository);

        invitedUser = new User(2L, "Bob", "bob@test.it");
        team = new Team(1L, "Alpha", List.of());
    }

    // -----------------------------------------------------------------------
    // createInvite – happy path
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("createInvite – team e utente validi → invito creato e salvato")
    void createInvite_success() {
        when(teamRepository.findByID(1L)).thenReturn(team);
        when(userRepository.findByName("Bob")).thenReturn(invitedUser);
        when(inviteRepository.save(any(Invite.class))).thenAnswer(inv -> inv.getArgument(0));

        Invite result = inviteService.createInvite(1L, invitedUser);

        assertNotNull(result);
        verify(inviteRepository, times(1)).save(any(Invite.class));
    }

    // -----------------------------------------------------------------------
    // createInvite – team non trovato
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("createInvite – team non trovato → IllegalArgumentException 'Team not found'")
    void createInvite_teamNotFound_throwsException() {
        when(teamRepository.findByID(99L)).thenReturn(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> inviteService.createInvite(99L, invitedUser));

        assertEquals("Team not found", ex.getMessage());
        verify(inviteRepository, never()).save(any());
    }

    // -----------------------------------------------------------------------
    // createInvite – utente non registrato
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("createInvite – utente non trovato nel repository → IllegalArgumentException 'User not registered'")
    void createInvite_userNotRegistered_throwsException() {
        when(teamRepository.findByID(1L)).thenReturn(team);

        // findByName restituisce un utente diverso → .equals() fallisce
        User differentUser = new User(99L, "Fake", "fake@test.it");
        when(userRepository.findByName("Bob")).thenReturn(differentUser);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> inviteService.createInvite(1L, invitedUser));

        assertEquals("User not registered", ex.getMessage());
        verify(inviteRepository, never()).save(any());
    }

    // -----------------------------------------------------------------------
    // createInvite – inviti multipli generano ID crescenti
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("createInvite – chiamate multiple salvano inviti distinti")
    void createInvite_multipleCalls_saveMultipleInvites() {
        User userC = new User(3L, "Carlo", "carlo@test.it");

        when(teamRepository.findByID(1L)).thenReturn(team);
        when(userRepository.findByName("Bob")).thenReturn(invitedUser);
        when(userRepository.findByName("Carlo")).thenReturn(userC);
        when(inviteRepository.save(any(Invite.class))).thenAnswer(inv -> inv.getArgument(0));

        inviteService.createInvite(1L, invitedUser);
        inviteService.createInvite(1L, userC);

        verify(inviteRepository, times(2)).save(any(Invite.class));
    }
}
