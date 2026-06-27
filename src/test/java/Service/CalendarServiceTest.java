package Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import unicam.hackhub.model.Mentor;
import unicam.hackhub.model.Organizer;
import unicam.hackhub.model.SupportRequest;
import unicam.hackhub.model.Team;
import unicam.hackhub.repository.StaffMemberRepository;
import unicam.hackhub.repository.SupportRepository;
import unicam.hackhub.repository.TeamRepository;
import unicam.hackhub.service.CalendarService;
import unicam.hackhub.service.GoogleCalendarAPI;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalendarServiceTest {

    @Mock private StaffMemberRepository staffMemberRepository;
    @Mock private SupportRepository supportRepository;
    @Mock private TeamRepository teamRepository;
    @Mock private GoogleCalendarAPI googleCalendarAPI;

    private CalendarService calendarService;

    private Mentor mentor;
    private Team team;

    private final LocalDate TODAY     = LocalDate.now();
    private final LocalDate TOMORROW  = TODAY.plusDays(1);
    private final LocalDate NEXT_WEEK = TODAY.plusDays(7);

    @BeforeEach
    void setUp() {
        calendarService = new CalendarService(
                staffMemberRepository, supportRepository,
                teamRepository, googleCalendarAPI);

        mentor = new Mentor(1L, "Mentor One", "mentor@test.it", null);
        team   = new Team(1L, "Team Alpha", List.of());
    }

    // -----------------------------------------------------------------------
    // getAvailableDates
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("getAvailableDates – range valido → restituisce date libere da Google")
    void getAvailableDates_validData_returnsFreeDates() throws Exception {
        when(staffMemberRepository.findByID(1L)).thenReturn(mentor);
        when(googleCalendarAPI.getFreeDates(TODAY, NEXT_WEEK))
                .thenReturn(List.of(TOMORROW, TODAY.plusDays(3)));

        List<LocalDate> result = calendarService.getAvailableDates(1L, TODAY, NEXT_WEEK);

        assertEquals(2, result.size());
        assertTrue(result.contains(TOMORROW));
        verify(googleCalendarAPI).getFreeDates(TODAY, NEXT_WEEK);
    }

    @Test
    @DisplayName("getAvailableDates – parametro null → IllegalArgumentException 'Invalid data'")
    void getAvailableDates_nullData_throwsException() throws GeneralSecurityException, IOException {
        assertThrows(IllegalArgumentException.class,
                () -> calendarService.getAvailableDates(null, TODAY, NEXT_WEEK));
        assertThrows(IllegalArgumentException.class,
                () -> calendarService.getAvailableDates(1L, null, NEXT_WEEK));
        assertThrows(IllegalArgumentException.class,
                () -> calendarService.getAvailableDates(1L, TODAY, null));

        verify(googleCalendarAPI, never()).getFreeDates(any(), any());
    }

    @Test
    @DisplayName("getAvailableDates – end non dopo start → IllegalArgumentException")
    void getAvailableDates_endNotAfterStart_throwsException() throws GeneralSecurityException, IOException {
        assertThrows(IllegalArgumentException.class,
                () -> calendarService.getAvailableDates(1L, NEXT_WEEK, TODAY));

        verify(googleCalendarAPI, never()).getFreeDates(any(), any());
    }

    @Test
    @DisplayName("getAvailableDates – staff non è un Mentor → IllegalArgumentException")
    void getAvailableDates_staffNotMentor_throwsException() throws GeneralSecurityException, IOException {
        when(staffMemberRepository.findByID(1L)).thenReturn(new Organizer(1L, "Org"));

        assertThrows(IllegalArgumentException.class,
                () -> calendarService.getAvailableDates(1L, TODAY, NEXT_WEEK));

        verify(googleCalendarAPI, never()).getFreeDates(any(), any());
    }

    @Test
    @DisplayName("getAvailableDates – mentor non trovato → IllegalArgumentException")
    void getAvailableDates_mentorNotFound_throwsException() throws GeneralSecurityException, IOException {
        when(staffMemberRepository.findByID(99L)).thenReturn(null);

        assertThrows(IllegalArgumentException.class,
                () -> calendarService.getAvailableDates(99L, TODAY, NEXT_WEEK));

        verify(googleCalendarAPI, never()).getFreeDates(any(), any());
    }

    @Test
    @DisplayName("getAvailableDates – Google Calendar non disponibile → IllegalStateException")
    void getAvailableDates_googleUnavailable_throwsIllegalState() throws Exception {
        when(staffMemberRepository.findByID(1L)).thenReturn(mentor);
        when(googleCalendarAPI.getFreeDates(any(), any()))
                .thenThrow(new IOException("Connection refused"));

        assertThrows(IllegalStateException.class,
                () -> calendarService.getAvailableDates(1L, TODAY, NEXT_WEEK));
    }

    // -----------------------------------------------------------------------
    // bookDate
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("bookDate – prenotazione valida → salva SupportRequest con googleEventID")
    void bookDate_validData_savesSupportRequest() throws Exception {
        when(staffMemberRepository.findByID(1L)).thenReturn(mentor);
        when(teamRepository.findByID(1L)).thenReturn(team);
        when(googleCalendarAPI.getFreeDates(TOMORROW, TOMORROW))
                .thenReturn(List.of(TOMORROW));
        when(googleCalendarAPI.createEvent(TOMORROW, "Team Alpha", "Mentor One"))
                .thenReturn("google-event-123");
        when(supportRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        SupportRequest result = calendarService.bookDate(1L, 1L, 1L, 1L, TOMORROW);

        assertNotNull(result);
        assertEquals(TOMORROW, result.getRequestedDate());
        assertEquals("google-event-123", result.getGoogleEventID());
        assertEquals(1L, result.getMentorID());
        assertEquals(1L, result.getTeamID());
        verify(supportRepository).save(any());
    }

    @Test
    @DisplayName("bookDate – parametro null → IllegalArgumentException 'Invalid data'")
    void bookDate_nullData_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> calendarService.bookDate(null, 1L, 1L, 1L, TOMORROW));
        assertThrows(IllegalArgumentException.class,
                () -> calendarService.bookDate(1L, null, 1L, 1L, TOMORROW));
        assertThrows(IllegalArgumentException.class,
                () -> calendarService.bookDate(1L, 1L, null, 1L, TOMORROW));
        assertThrows(IllegalArgumentException.class,
                () -> calendarService.bookDate(1L, 1L, 1L, null, TOMORROW));
        assertThrows(IllegalArgumentException.class,
                () -> calendarService.bookDate(1L, 1L, 1L, 1L, null));

        verify(supportRepository, never()).save(any());
    }

    @Test
    @DisplayName("bookDate – data nel passato → IllegalArgumentException")
    void bookDate_pastDate_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> calendarService.bookDate(1L, 1L, 1L, 1L, TODAY.minusDays(1)));

        verify(supportRepository, never()).save(any());
    }

    @Test
    @DisplayName("bookDate – staff non è un Mentor → IllegalArgumentException")
    void bookDate_staffNotMentor_throwsException() {
        when(staffMemberRepository.findByID(1L)).thenReturn(new Organizer(1L, "Org"));

        assertThrows(IllegalArgumentException.class,
                () -> calendarService.bookDate(1L, 1L, 1L, 1L, TOMORROW));

        verify(supportRepository, never()).save(any());
    }

    @Test
    @DisplayName("bookDate – team non trovato → IllegalArgumentException")
    void bookDate_teamNotFound_throwsException() throws Exception {
        when(staffMemberRepository.findByID(1L)).thenReturn(mentor);

        assertThrows(IllegalArgumentException.class,
                () -> calendarService.bookDate(1L, 99L, 1L, 1L, TOMORROW));

        verify(supportRepository, never()).save(any());
    }

    @Test
    @DisplayName("bookDate – data non disponibile sul calendario → IllegalArgumentException")
    void bookDate_dateNotAvailable_throwsException() throws Exception {
        when(staffMemberRepository.findByID(1L)).thenReturn(mentor);
        when(teamRepository.findByID(1L)).thenReturn(team);
        when(googleCalendarAPI.getFreeDates(TOMORROW, TOMORROW))
                .thenReturn(List.of());

        assertThrows(IllegalArgumentException.class,
                () -> calendarService.bookDate(1L, 1L, 1L, 1L, TOMORROW));

        verify(supportRepository, never()).save(any());
    }

    @Test
    @DisplayName("bookDate – Google non raggiungibile per verifica → IllegalStateException")
    void bookDate_googleUnavailableOnCheck_throwsIllegalState() throws Exception {
        when(staffMemberRepository.findByID(1L)).thenReturn(mentor);
        when(teamRepository.findByID(1L)).thenReturn(team);
        when(googleCalendarAPI.getFreeDates(any(), any()))
                .thenThrow(new IOException("Connection refused"));

        assertThrows(IllegalStateException.class,
                () -> calendarService.bookDate(1L, 1L, 1L, 1L, TOMORROW));

        verify(supportRepository, never()).save(any());
    }

    @Test
    @DisplayName("bookDate – Google non raggiungibile per creazione evento → IllegalStateException")
    void bookDate_googleUnavailableOnCreate_throwsIllegalState() throws Exception {
        when(staffMemberRepository.findByID(1L)).thenReturn(mentor);
        when(teamRepository.findByID(1L)).thenReturn(team);
        when(googleCalendarAPI.getFreeDates(TOMORROW, TOMORROW))
                .thenReturn(List.of(TOMORROW));
        when(googleCalendarAPI.createEvent(any(), any(), any()))
                .thenThrow(new IOException("Connection refused"));

        assertThrows(IllegalStateException.class,
                () -> calendarService.bookDate(1L, 1L, 1L, 1L, TOMORROW));

        verify(supportRepository, never()).save(any());
    }

    // -----------------------------------------------------------------------
    // cancelBooking
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("cancelBooking – prenotazione valida → elimina da Google e dal repository")
    void cancelBooking_existingBooking_deletesFromGoogleAndRepo() throws Exception {
        SupportRequest request = new SupportRequest(1L, TOMORROW, 1L, 1L, 1L, 1L);
        request.setGoogleEventID("google-event-123");
        when(supportRepository.findByID(1L)).thenReturn(request);

        calendarService.cancelBooking(1L);

        verify(googleCalendarAPI).deleteEvent("google-event-123");
        verify(supportRepository).delete(request);
    }

    @Test
    @DisplayName("cancelBooking – requestID null → IllegalArgumentException")
    void cancelBooking_nullID_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> calendarService.cancelBooking(null));

        verify(supportRepository, never()).delete(any());
    }

    @Test
    @DisplayName("cancelBooking – prenotazione non trovata → IllegalArgumentException")
    void cancelBooking_notFound_throwsException() {
        when(supportRepository.findByID(99L)).thenReturn(null);

        assertThrows(IllegalArgumentException.class,
                () -> calendarService.cancelBooking(99L));

        verify(supportRepository, never()).delete(any());
    }

    @Test
    @DisplayName("cancelBooking – Google fallisce → cancella comunque localmente")
    void cancelBooking_googleFails_stillDeletesLocally() throws Exception {
        SupportRequest request = new SupportRequest(1L, TOMORROW, 1L, 1L, 1L, 1L);
        request.setGoogleEventID("google-event-123");
        when(supportRepository.findByID(1L)).thenReturn(request);
        doThrow(new IOException("Google error"))
                .when(googleCalendarAPI).deleteEvent(any());

        assertDoesNotThrow(() -> calendarService.cancelBooking(1L));

        verify(supportRepository).delete(request);
    }

    @Test
    @DisplayName("cancelBooking – nessun googleEventID → non chiama Google Calendar")
    void cancelBooking_noGoogleEventID_skipsGoogleCall() throws Exception {
        SupportRequest request = new SupportRequest(1L, TOMORROW, 1L, 1L, 1L, 1L);
        when(supportRepository.findByID(1L)).thenReturn(request);

        calendarService.cancelBooking(1L);

        verify(googleCalendarAPI, never()).deleteEvent(any());
        verify(supportRepository).delete(request);
    }
}