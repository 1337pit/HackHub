package Handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import unicam.hackhub.dto.ReportRequest;
import unicam.hackhub.handler.ReportHandler;
import unicam.hackhub.model.Report;
import unicam.hackhub.service.ReportService;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

class ReportHandlerTest {

    private ReportService reportService;
    private ReportHandler reportHandler;

    @BeforeEach
    void setUp() {
        // Mock istantaneo del servizio senza annotazioni Spring
        reportService = Mockito.mock(ReportService.class);
        reportHandler = new ReportHandler(reportService);
    }

    // -----------------------------------------------------------------------
    // 1. Test per getReports (GET)
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("getReports – Ritorna una lista di segnalazioni (200 OK)")
    void getReports_Success() {
        // Arrange
        Report mockReport = new Report();
        when(reportService.getReports(1L)).thenReturn(List.of(mockReport));

        // Act
        ResponseEntity<List<Report>> response = reportHandler.getReports(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    @DisplayName("getReports – Lista vuota se non ci sono segnalazioni (200 OK)")
    void getReports_EmptyList() {
        // Arrange
        when(reportService.getReports(1L)).thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<List<Report>> response = reportHandler.getReports(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    // -----------------------------------------------------------------------
    // 2. Test per createReport (POST)
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("createReport – Dati validi crea la segnalazione (201 Created)")
    void createReport_Success() {
        // Arrange
        ReportRequest request = new ReportRequest(2L, 3L, 1L, "Test desc");
        Report mockReport = new Report();

        when(reportService.createReport(2L, 3L, 1L, "Test desc")).thenReturn(mockReport);

        // Act
        ResponseEntity<Report> response = reportHandler.createReport(request);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("createReport – Mentore/Dati invalidi lancia 400 Bad Request")
    void createReport_BadRequest() {
        // Arrange
        ReportRequest request = new ReportRequest(999L, 3L, 1L, "Errore");

        when(reportService.createReport(anyLong(), anyLong(), anyLong(), anyString()))
                .thenThrow(new IllegalArgumentException("Mentor not found"));

        // Act
        ResponseEntity<Report> response = reportHandler.createReport(request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    // -----------------------------------------------------------------------
    // 3. Test per updateReport (PUT)
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("updateReport – Modifica riuscita (200 OK)")
    void updateReport_Success() {
        // Arrange
        Report mockReport = new Report();
        when(reportService.updateReport(1L, 2L, "Nuova descrizione")).thenReturn(mockReport);

        // Act
        ResponseEntity<Report> response = reportHandler.updateReport(1L, 2L, "Nuova descrizione");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    // -----------------------------------------------------------------------
    // 4. Test per deleteReport (DELETE)
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("deleteReport – Eliminazione riuscita (204 No Content)")
    void deleteReport_Success() {
        // Act
        ResponseEntity<Void> response = reportHandler.deleteReport(1L, 2L);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    @DisplayName("deleteReport – Segnalazione non trovata (404 Not Found)")
    void deleteReport_NotFound() {
        // Arrange
        doThrow(new IllegalArgumentException("Report not found"))
                .when(reportService).deleteReport(99L, 2L);

        // Act
        ResponseEntity<Void> response = reportHandler.deleteReport(99L, 2L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}