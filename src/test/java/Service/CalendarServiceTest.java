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
import unicam.hackhub.service.CalendarAPI;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalendarServiceTest {

    @Mock private StaffMemberRepository staffMemberRepository;
    @Mock private SupportRepository supportRepository;
    @Mock private TeamRepository teamRepository;
    @Mock private HackathonRepository hackathonRepository;
    @Mock private UserRepository userRepository;
    @Mock private CalendarAPI fakeCalendarService; // Sostituito qui

    @InjectMocks
    private CalendarService calendarService;

    private Mentor mentor;
    private Team team;
    private Hackathon hackathon;
    private User user;

    private final LocalDate TODAY     = LocalDate.now();
    private final LocalDate TOMORROW  = TODAY.plusDays(1);
    private final LocalDate NEXT_WEEK = TODAY.plusDays(7);

    @BeforeEach
    void setUp() {
        // Passa null per email e hackathon per soddisfare il costruttore a 4 parametri
        mentor    = new Mentor(1L, "Mentor One", "mentor@test.it", null);
        team      = new Team(1L, "Team Alpha", List.of());
        hackathon = new Hackathon();
        user      = new User(1L, "Alice", "alice@test.it");
    }

    // =========================================================================
    // 1. Test per getAvailableDates
    // =========================================================================

    @Test
    @DisplayName("getAvailableDates – range valido → restituisce date libere dal FakeCalendar")
    void getAvailableDates_validData_returnsFreeDates() {
        when(staffMemberRepository.findById(1L)).thenReturn(Optional.of(mentor));
        when(fakeCalendarService.getFreeDates(TODAY, NEXT_WEEK))
                .thenReturn(List.of(TOMORROW, TODAY.plusDays(3)));

        List<LocalDate> result = calendarService.getAvailableDates(1L, TODAY, NEXT_WEEK);

        assertEquals(2, result.size());
        assertTrue(result.contains(TOMORROW));
        verify(fakeCalendarService).getFreeDates(TODAY, NEXT_WEEK);
    }

    @Test
    @DisplayName("getAvailableDates – parametro null → IllegalArgumentException 'Invalid data'")
    void getAvailableDates_nullData_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> calendarService.getAvailableDates(null, TODAY, NEXT_WEEK));
        assertThrows(IllegalArgumentException.class, () -> calendarService.getAvailableDates(1L, null, NEXT_WEEK));
        assertThrows(IllegalArgumentException.class, () -> calendarService.getAvailableDates(1L, TODAY, null));

        verify(fakeCalendarService, never()).getFreeDates(any(), any());
    }

    @Test
    @DisplayName("getAvailableDates – data di fine precedente a quella di inizio → IllegalArgumentException")
    void getAvailableDates_endNotAfterStart_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> calendarService.getAvailableDates(1L, NEXT_WEEK, TODAY));

        verify(fakeCalendarService, never()).getFreeDates(any(), any());
    }

    @Test
    @DisplayName("getAvailableDates – lo staff trovato non è un istanza di Mentor → IllegalArgumentException")
    void getAvailableDates_staffNotMentor_throwsException() {
        Organizer organizer = new Organizer(1L, "Organizer One");
        when(staffMemberRepository.findById(1L)).thenReturn(Optional.of(organizer));

        assertThrows(IllegalArgumentException.class, () -> calendarService.getAvailableDates(1L, TODAY, NEXT_WEEK));

        verify(fakeCalendarService, never()).getFreeDates(any(), any());
    }

    // =========================================================================
    // 2. Test per bookDate
    // =========================================================================

    @Test
    @DisplayName("bookDate – prenotazione valida → salva SupportRequest con l'ID fittizio dell'evento")
    void bookDate_validData_savesSupportRequest() {
        when(staffMemberRepository.findById(1L)).thenReturn(Optional.of(mentor));
        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
        when(hackathonRepository.findById(1L)).thenReturn(Optional.of(hackathon));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        when(fakeCalendarService.getFreeDates(TOMORROW, TOMORROW)).thenReturn(List.of(TOMORROW));
        when(fakeCalendarService.createEvent(TOMORROW)).thenReturn("fake-event-123");
        when(supportRepository.save(any(SupportRequest.class))).thenAnswer(i -> i.getArgument(0));

        SupportRequest result = calendarService.bookDate(1L, 1L, 1L, 1L, TOMORROW);

        assertNotNull(result);
        assertEquals(TOMORROW, result.getRequestedDate());
        assertEquals("fake-event-123", result.getGoogleEventID());
        verify(supportRepository).save(any(SupportRequest.class));
    }

    @Test
    @DisplayName("bookDate – parametri obbligatori null → IllegalArgumentException")
    void bookDate_nullData_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> calendarService.bookDate(null, 1L, 1L, 1L, TOMORROW));
        assertThrows(IllegalArgumentException.class, () -> calendarService.bookDate(1L, null, 1L, 1L, TOMORROW));
        assertThrows(IllegalArgumentException.class, () -> calendarService.bookDate(1L, 1L, 1L, 1L, null));

        verify(supportRepository, never()).save(any());
    }

    @Test
    @DisplayName("bookDate – tentativo di prenotare una data passata → IllegalArgumentException")
    void bookDate_pastDate_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> calendarService.bookDate(1L, 1L, 1L, 1L, TODAY.minusDays(1)));

        verify(supportRepository, never()).save(any());
    }

    @Test
    @DisplayName("bookDate – slot temporale non disponibile nel calendario → IllegalArgumentException")
    void bookDate_dateNotAvailable_throwsException() {
        when(staffMemberRepository.findById(1L)).thenReturn(Optional.of(mentor));
        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
        when(hackathonRepository.findById(1L)).thenReturn(Optional.of(hackathon));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Calendario occupato per questo giorno (ritorna lista vuota)
        when(fakeCalendarService.getFreeDates(TOMORROW, TOMORROW)).thenReturn(List.of());

        assertThrows(IllegalArgumentException.class, () -> calendarService.bookDate(1L, 1L, 1L, 1L, TOMORROW));

        verify(fakeCalendarService, never()).createEvent(any());
        verify(supportRepository, never()).save(any());
    }

    // =========================================================================
    // 3. Test per cancelBooking
    // =========================================================================

    @Test
    @DisplayName("cancelBooking – prenotazione esistente → rimuove slot dal FakeCalendar e cancella dal database")
    void cancelBooking_existingBooking_success() {
        SupportRequest request = new SupportRequest(1L, TOMORROW, hackathon, user, team, mentor);
        request.setGoogleEventID("fake-event-123");

        when(supportRepository.findById(1L)).thenReturn(Optional.of(request));

        calendarService.cancelBooking(1L);

        // Verifica il rilascio dello slot di tempo
        verify(fakeCalendarService).deleteEvent("fake-event-123", TOMORROW);
        verify(supportRepository).delete(request);
    }

    @Test
    @DisplayName("cancelBooking – prenotazione non trovata a sistema → Lancia eccezione")
    void cancelBooking_notFound_throwsException() {
        when(supportRepository.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> calendarService.cancelBooking(99L));

        assertEquals("Booking not found", ex.getMessage());
        verify(supportRepository, never()).delete(any());
    }
}