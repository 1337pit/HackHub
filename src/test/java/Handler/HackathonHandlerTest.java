package Handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import unicam.hackhub.handler.HackathonHandler;
import unicam.hackhub.model.Hackathon;
import unicam.hackhub.model.Mentor;
import unicam.hackhub.service.HackathonService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class HackathonHandlerTest {

    private HackathonService hackathonService;
    private HackathonHandler hackathonHandler;

    @BeforeEach
    void setUp() {
        // 1. Creiamo il mock del servizio in modo elementare
        hackathonService = Mockito.mock(HackathonService.class);

        // 2. Lo passiamo direttamente al costruttore del controller
        hackathonHandler = new HackathonHandler(hackathonService);
    }

    @Test
    @DisplayName("Creazione Hackathon - Successo")
    void createHackathon_Success() {
        // Arrange (Configurazione dei dati staccati dal JSON)
        Hackathon inputHackathon = new Hackathon();
        inputHackathon.setNameHackathon("Hackathon 2026");

        Hackathon mockOutput = new Hackathon();
        mockOutput.setId(1L);
        mockOutput.setNameHackathon("Hackathon 2026");

        // Diciamo al mock cosa ritornare quando viene chiamato
        when(hackathonService.createHackathon(any(), any(), any(), any(), any(), any(), any(), any(), anyInt(), any(), anyLong(), anyLong()))
                .thenReturn(mockOutput);

        // Act (Chiamata diretta al metodo Java dell'Handler)
        ResponseEntity<Hackathon> response = hackathonHandler.createHackathon(inputHackathon, 2L, 3L, "RegistrationState");

        // Assert (Verifica dello stato HTTP e del contenuto)
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    @DisplayName("Creazione Hackathon - Stato sconosciuto lancia BadRequest")
    void createHackathon_InvalidState_ReturnsBadRequest() {
        // Act (Passiamo una stringa di stato non censita nello switch)
        ResponseEntity<Hackathon> response = hackathonHandler.createHackathon(new Hackathon(), 2L, 3L, "StatoInesistente");

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Modifica Hackathon - Successo")
    void editHackathon_Success() {
        // Arrange
        Hackathon mockOutput = new Hackathon();
        mockOutput.setNameHackathon("Nome Aggiornato");

        when(hackathonService.editHackathon(anyLong(), any(), any(), any(), any(), any(), anyInt(), anyLong(), anyLong()))
                .thenReturn(mockOutput);

        // Act (Invochiamo il metodo passando i parametri come argomenti Java completi)
        ResponseEntity<?> response = hackathonHandler.editHackathon(1L, "Nome Aggiornato", null, null, null, null, 5, null, null);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Aggiungi Mentore - Successo")
    void addMentor_Success() {
        // Arrange
        Mentor mockMentor = new Mentor();
        when(hackathonService.addMentor("mentore@test.it", 1L)).thenReturn(mockMentor);

        // Act
        ResponseEntity<Mentor> response = hackathonHandler.addMentor(1L, "mentore@test.it");

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }
}