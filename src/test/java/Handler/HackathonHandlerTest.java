package Handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import unicam.hackhub.handler.HackathonHandler;
import unicam.hackhub.model.*;
import unicam.hackhub.service.HackathonService;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HackathonHandlerTest {

    @Mock
    private HackathonService hackathonService;

    @InjectMocks
    private HackathonHandler hackathonHandler;

    private Organizer mockOrganizer;
    private Hackathon mockHackathon;
    private Team mockTeam;

    @BeforeEach
    void setUp() {
        // Inizializzazione di oggetti mock di supporto per i test
        mockOrganizer = mock(Organizer.class);
        mockHackathon = mock(Hackathon.class);
        mockTeam = mock(Team.class);
    }

    // --- TEST PER createHackathon ---
/*
    @Test
    void createHackathon_Success() {
        // Arrange
        String name = "HackUnicam 2026";
        String rulebook = "Regolamento ufficiale";
        LocalDate regDeadline = LocalDate.now().plusDays(5);
        LocalDate start = LocalDate.now().plusDays(10);
        LocalDate end = LocalDate.now().plusDays(12);
        String location = "Camerino";
        String prize = "1000€";
        HackathonState state = HackathonState.CREATED;
        int maxTeamSize = 5;
        Long mentorId = 1L;
        Long judgeId = 2L;

        when(hackathonService.createHackathon(name, rulebook, regDeadline, start, end, location,
                prize, state, maxTeamSize, mockOrganizer, mentorId, judgeId))
                .thenReturn(mockHackathon);

        // Act
        Hackathon result = hackathonHandler.createHackathon(name, rulebook, regDeadline, start, end,
                location, prize, state, maxTeamSize, mockOrganizer, mentorId, judgeId);

        // Assert
        assertNotNull(result);
        assertEquals(mockHackathon, result);
        verify(hackathonService, times(1)).createHackathon(name, rulebook, regDeadline, start, end,
                location, prize, state, maxTeamSize, mockOrganizer, mentorId, judgeId);
    }*/

    @Test
    void createHackathon_Exception_ReturnsNull() {
        // Arrange
        when(hackathonService.createHackathon(any(), any(), any(), any(), any(), any(),
                any(), any(), anyInt(), any(), anyLong(), anyLong()))
                .thenThrow(new IllegalArgumentException("Dati non validi"));

        // Act
        Hackathon result = hackathonHandler.createHackathon("Errore", "Rulebook", LocalDate.now(),
                LocalDate.now(), LocalDate.now(), "Online", "0", null, 0, null, 1L, 2L);

        // Assert
        assertNull(result);
    }

    // --- TEST PER addMentor ---

    @Test
    void addMentor_Success() {
        // Arrange
        Long mentorId = 10L;
        doNothing().when(hackathonService).addMentor(mentorId);

        // Act
        hackathonHandler.addMentor(mentorId);

        // Assert
        verify(hackathonService, times(1)).addMentor(mentorId);
    }

    @Test
    void addMentor_Exception_Handled() {
        // Arrange
        Long mentorId = 10L;
        doThrow(new IllegalArgumentException("Mentore non trovato")).when(hackathonService).addMentor(mentorId);

        // Act & Assert (Non deve lanciare eccezioni perché l'handler la cattura internamente)
        assertDoesNotThrow(() -> hackathonHandler.addMentor(mentorId));
        verify(hackathonService, times(1)).addMentor(mentorId);
    }

    // --- TEST PER declareWinner ---

    @Test
    void declareWinner_Success() {
        // Arrange
        doNothing().when(hackathonService).declareWinner(mockTeam);

        // Act
        hackathonHandler.declareWinner(mockTeam);

        // Assert
        verify(hackathonService, times(1)).declareWinner(mockTeam);
    }

    @Test
    void declareWinner_Exception_Handled() {
        // Arrange
        doThrow(new IllegalArgumentException("Team non valido")).when(hackathonService).declareWinner(mockTeam);

        // Act & Assert
        assertDoesNotThrow(() -> hackathonHandler.declareWinner(mockTeam));
        verify(hackathonService, times(1)).declareWinner(mockTeam);
    }

    // --- TEST PER changeState ---

    /*@Test
    void changeState_Success() {
        // Arrange
        HackathonState newState = HackathonState.IN_CORSO;
        doNothing().when(hackathonService).changeState(newState);

        // Act
        hackathonHandler.changeState(newState);

        // Assert
        verify(hackathonService, times(1)).changeState(newState);
    }*/

   /* @Test
    void changeState_Exception_Handled() {
        // Arrange
        HackathonState newState = HackathonState.TERMINATO;
        doThrow(new IllegalArgumentException("Transizione di stato non permessa")).when(hackathonService).changeState(newState);

        // Act & Assert
        assertDoesNotThrow(() -> hackathonHandler.changeState(newState));
        verify(hackathonService, times(1)).changeState(newState);
    }
    */
}
