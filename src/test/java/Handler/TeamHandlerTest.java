package Handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import unicam.hackhub.handler.TeamHandler;
import unicam.hackhub.model.Team;
import unicam.hackhub.model.User;
import unicam.hackhub.service.TeamService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test unitari per TeamHandler.
 *
 * Verifica che l'handler:
 *  - deleghi correttamente a TeamService
 *  - gestisca le eccezioni restituendo null (come da implementazione attuale)
 */
@ExtendWith(MockitoExtension.class)
class TeamHandlerTest {

    @Mock
    private TeamService teamService;

    private TeamHandler teamHandler;

    private User leader;

    @BeforeEach
    void setUp() {
        teamHandler = new TeamHandler(teamService);
        leader = new User(1L, "Alice", "alice@test.it");
    }

    // -----------------------------------------------------------------------
    // Happy path
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("createTeam – team creato con successo → restituisce il team")
    void createTeam_success_returnsTeam() {
        Team expectedTeam = new Team(1L, "Alpha", List.of(leader));
        when(teamService.createTeam(1L, "Alpha", List.of())).thenReturn(expectedTeam);

        Team result = teamHandler.createTeam(1L, "Alpha", List.of());

        assertNotNull(result);
        assertEquals("Alpha", result.getTeamName());
        verify(teamService, times(1)).createTeam(1L, "Alpha", List.of());
    }

    // -----------------------------------------------------------------------
    // Gestione errori: l'handler cattura IllegalArgumentException e restituisce null
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("createTeam – service lancia eccezione → handler restituisce null")
    void createTeam_serviceThrows_returnsNull() {
        when(teamService.createTeam(anyLong(), anyString(), any()))
                .thenThrow(new IllegalArgumentException("User not found"));

        Team result = teamHandler.createTeam(99L, "Alpha", List.of());

        assertNull(result);
    }

    @Test
    @DisplayName("createTeam – user already in a team → handler restituisce null")
    void createTeam_userAlreadyInTeam_returnsNull() {
        when(teamService.createTeam(anyLong(), anyString(), any()))
                .thenThrow(new IllegalArgumentException("User already in a team"));

        Team result = teamHandler.createTeam(1L, "Alpha", List.of());

        assertNull(result);
    }

    @Test
    @DisplayName("createTeam – team name already used → handler restituisce null")
    void createTeam_teamNameAlreadyUsed_returnsNull() {
        when(teamService.createTeam(anyLong(), anyString(), any()))
                .thenThrow(new IllegalArgumentException("Team name is already used"));

        Team result = teamHandler.createTeam(1L, "Alpha", List.of());

        assertNull(result);
    }

    // -----------------------------------------------------------------------
    // Delega: l'handler non fa logica, la lascia tutta al service
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("createTeam – i parametri vengono passati intatti al service")
    void createTeam_parametersPassedThrough() {
        List<User> members = List.of(new User(2L, "Bob", "bob@test.it"));
        Team expectedTeam = new Team(1L, "Beta", List.of(leader));
        when(teamService.createTeam(1L, "Beta", members)).thenReturn(expectedTeam);

        teamHandler.createTeam(1L, "Beta", members);

        verify(teamService).createTeam(1L, "Beta", members);
    }
}
