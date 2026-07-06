package Handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import unicam.hackhub.dto.SupportRequest;
import unicam.hackhub.handler.TeamHandler;
import unicam.hackhub.model.Report;
import unicam.hackhub.model.Team;
import unicam.hackhub.model.User;
import unicam.hackhub.service.TeamService;
import unicam.hackhub.service.UserService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TeamHandlerTest {

    private TeamService teamService;
    private UserService userService;
    private TeamHandler teamHandler;

    private Team mockTeam;
    private User mockUser;
    private Report mockReport;

    @BeforeEach
    void setUp() {
        // Inizializzazione esplicita dei mock dei Service
        teamService = Mockito.mock(TeamService.class);
        userService = Mockito.mock(UserService.class);

        // Iniezione manuale nel costruttore a due parametri
        teamHandler = new TeamHandler(teamService, userService);

        mockTeam = Mockito.mock(Team.class);
        mockUser = Mockito.mock(User.class);
        mockReport = Mockito.mock(Report.class);
    }

    // =======================================================================
    // 1. Test per createTeam (POST)
    // =======================================================================

    @Test
    @DisplayName("createTeam – Successo restituisce 201 Created")
    void createTeam_Success() {
        // DTO di input con gli ID degli utenti invitati
        SupportRequest dto = new SupportRequest(1L, "Alpha", List.of(2L));

        // Mappatura interna dell'handler da ID a Oggetto User
        when(userService.getUser(2L)).thenReturn(mockUser);
        when(teamService.createTeam(eq(1L), eq("Alpha"), anyList())).thenReturn(mockTeam);

        ResponseEntity<Team> response = teamHandler.createTeam(dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(mockTeam, response.getBody());
        verify(teamService).createTeam(eq(1L), eq("Alpha"), anyList());
    }

    // =======================================================================
    // 2. Test per reportTeam (POST)
    // =======================================================================

    @Test
    @DisplayName("reportTeam – Successo restituisce 201 Created")
    void reportTeam_Success() {
        when(teamService.reportTeam(2L, 10L, "Insulti")).thenReturn(mockReport);

        ResponseEntity<Report> response = teamHandler.reportTeam(10L, 2L, "Insulti");

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(mockReport, response.getBody());
    }

    @Test
    @DisplayName("reportTeam – Eccezione nel service restituisce 400 Bad Request")
    void reportTeam_Exception_ReturnsBadRequest() {
        when(teamService.reportTeam(anyLong(), anyLong(), anyString()))
                .thenThrow(new IllegalArgumentException("Mentor not found"));

        ResponseEntity<Report> response = teamHandler.reportTeam(10L, 99L, "Insulti");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    // =======================================================================
    // 3. Test per deleteTeam (DELETE)
    // =======================================================================

    @Test
    @DisplayName("deleteTeam – Successo restituisce 240 No Content")
    void deleteTeam_Success() {
        doNothing().when(teamService).deleteTeam(1L, 10L);

        ResponseEntity<Void> response = teamHandler.deleteTeam(1L, 10L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    // =======================================================================
    // 4. Test per requestSupport (POST)
    // =======================================================================

    @Test
    @DisplayName("requestSupport – Successo restituisce 201 Created")
    void requestSupport_Success() {
        unicam.hackhub.model.SupportRequest mockSupport = Mockito.mock(unicam.hackhub.model.SupportRequest.class);
        LocalDate date = LocalDate.now();

        when(teamService.requiresAssistance(1L, 2L, 10L, 3L, date)).thenReturn(mockSupport);

        ResponseEntity<unicam.hackhub.model.SupportRequest> response =
                teamHandler.requestSupport(1L, 2L, 10L, 3L, date);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(mockSupport, response.getBody());
    }

    // =======================================================================
    // 5. Test per getTeamById (GET)
    // =======================================================================

    @Test
    @DisplayName("getTeamById – Trovato restituisce 200 OK")
    void getTeamById_Success() {
        when(teamService.getTeamByID(10L)).thenReturn(mockTeam);

        ResponseEntity<Team> response = teamHandler.getTeamById(10L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockTeam, response.getBody());
    }

    @Test
    @DisplayName("getTeamById – Non trovato restituisce 404 Not Found")
    void getTeamById_NotFound() {
        when(teamService.getTeamByID(99L)).thenReturn(null);

        ResponseEntity<Team> response = teamHandler.getTeamById(99L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }
}