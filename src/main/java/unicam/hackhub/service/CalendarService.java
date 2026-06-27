package unicam.hackhub.service;

import unicam.hackhub.model.Mentor;
import unicam.hackhub.model.SupportRequest;
import unicam.hackhub.model.StaffMember;
import unicam.hackhub.model.Team;
import unicam.hackhub.repository.SupportRepository;
import unicam.hackhub.repository.StaffMemberRepository;
import unicam.hackhub.repository.TeamRepository;

import java.time.LocalDate;
import java.util.List;

public class CalendarService {

    private final StaffMemberRepository staffMemberRepository;
    private final SupportRepository requestSupportRepository;
    private final TeamRepository teamRepository;
    private final GoogleCalendarAPI googleCalendarAPI;

    public CalendarService(StaffMemberRepository staffMemberRepository,
                           SupportRepository requestSupportRepository,
                           TeamRepository teamRepository,
                           GoogleCalendarAPI googleCalendarAPI) {
        this.staffMemberRepository = staffMemberRepository;
        this.requestSupportRepository = requestSupportRepository;
        this.teamRepository = teamRepository;
        this.googleCalendarAPI = googleCalendarAPI;
    }

    /**
     * Restituisce le date libere del mentor nel range indicato.
     * Le date vengono lette direttamente dal Google Calendar del mentor.
     */
    public List<LocalDate> getAvailableDates(Long mentorID, LocalDate from, LocalDate to) {
        if (mentorID == null || from == null || to == null)
            throw new IllegalArgumentException("Invalid data");
        if (!to.isAfter(from))
            throw new IllegalArgumentException("End date must be after start date");

        StaffMember staff = staffMemberRepository.findByID(mentorID);
        if (!(staff instanceof Mentor))
            throw new IllegalArgumentException("Mentor not found");

        try {
            return googleCalendarAPI.getFreeDates(from, to);
        } catch (Exception e) {
            throw new IllegalStateException("Google Calendar non disponibile: " + e.getMessage());
        }
    }

    /**
     * Prenota una data per una call di supporto.
     * 1. Verifica dati validi
     * 2. Recupera mentor e team
     * 3. Verifica che la data sia libera
     * 4. Crea l'evento su Google Calendar
     * 5. Salva la SupportRequest con l'ID dell'evento Google
     */
    public SupportRequest bookDate(Long mentorID, Long teamID, Long hackathonID,
                                   Long userID, LocalDate date) {
        if (mentorID == null || teamID == null || hackathonID == null
                || userID == null || date == null)
            throw new IllegalArgumentException("Invalid data");
        if (date.isBefore(LocalDate.now()))
            throw new IllegalArgumentException("Cannot book a past date");

        StaffMember staff = staffMemberRepository.findByID(mentorID);
        if (!(staff instanceof Mentor))
            throw new IllegalArgumentException("Mentor not found");
        Mentor mentor = (Mentor) staff;

        Team team = teamRepository.findByID(teamID);
        if (team == null)
            throw new IllegalArgumentException("Team not found");

        // Verifica che la data sia libera sul calendario Google
        List<LocalDate> freeDates;
        try {
            freeDates = googleCalendarAPI.getFreeDates(date, date);
        } catch (Exception e) {
            throw new IllegalStateException("Google Calendar non disponibile: " + e.getMessage());
        }
        if (freeDates.isEmpty())
            throw new IllegalArgumentException("Data non disponibile");

        // Crea l'evento su Google Calendar
        String googleEventID;
        try {
            googleEventID = googleCalendarAPI.createEvent(
                    date, team.getTeamName(), mentor.getName());
        } catch (Exception e) {
            throw new IllegalStateException("Impossibile creare l'evento: " + e.getMessage());
        }

        // Salva la prenotazione
        SupportRequest request = new SupportRequest(
                null, date, hackathonID, userID, teamID, mentorID);
        request.setGoogleEventID(googleEventID);

        return requestSupportRepository.save(request);
    }

    /**
     * Cancella una prenotazione esistente e rimuove l'evento da Google Calendar.
     */
    public void cancelBooking(Long requestID) {
        if (requestID == null)
            throw new IllegalArgumentException("Request ID cannot be null");

        SupportRequest request = requestSupportRepository.findByID(requestID);
        if (request == null)
            throw new IllegalArgumentException("Booking not found");

        // Rimuove l'evento da Google Calendar
        if (request.getGoogleEventID() != null) {
            try {
                googleCalendarAPI.deleteEvent(request.getGoogleEventID());
            } catch (Exception e) {
                System.err.println("Impossibile rimuovere l'evento Google: " + e.getMessage());
                // Non blocca la cancellazione locale
            }
        }

        requestSupportRepository.delete(request);
    }
}