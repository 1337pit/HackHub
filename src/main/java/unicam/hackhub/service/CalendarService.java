package unicam.hackhub.service;

import org.springframework.stereotype.Service;
import unicam.hackhub.model.Mentor;
import unicam.hackhub.model.SupportRequest;
import unicam.hackhub.model.Team;
import unicam.hackhub.repository.HackathonRepository;
import unicam.hackhub.repository.StaffMemberRepository;
import unicam.hackhub.repository.SupportRepository;
import unicam.hackhub.repository.TeamRepository;
import unicam.hackhub.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;

@Service
public class CalendarService {

    private final StaffMemberRepository staffMemberRepository;
    private final SupportRepository supportRepository;
    private final TeamRepository teamRepository;
    private final HackathonRepository hackathonRepository;
    private final UserRepository userRepository;
    private final CalendarAPI fakeCalendarService; // Sostituito qui

    public CalendarService(StaffMemberRepository staffMemberRepository,
                           SupportRepository supportRepository,
                           TeamRepository teamRepository,
                           HackathonRepository hackathonRepository,
                           UserRepository userRepository,
                           CalendarAPI fakeCalendarService) {
        this.staffMemberRepository = staffMemberRepository;
        this.supportRepository = supportRepository;
        this.teamRepository = teamRepository;
        this.hackathonRepository = hackathonRepository;
        this.userRepository = userRepository;
        this.fakeCalendarService = fakeCalendarService;
    }

    public List<LocalDate> getAvailableDates(Long mentorID, LocalDate from, LocalDate to) {
        if (mentorID == null || from == null || to == null)
            throw new IllegalArgumentException("Invalid data");
        if (!to.isAfter(from))
            throw new IllegalArgumentException("End date must be after start date");

        staffMemberRepository.findById(mentorID)
                .filter(s -> s instanceof Mentor)
                .orElseThrow(() -> new IllegalArgumentException("Mentor not found"));

        return fakeCalendarService.getFreeDates(from, to);
    }

    public SupportRequest bookDate(Long mentorID, Long teamID, Long hackathonID,
                                   Long userID, LocalDate date) {
        if (mentorID == null || teamID == null || hackathonID == null
                || userID == null || date == null)
            throw new IllegalArgumentException("Invalid data");
        if (date.isBefore(LocalDate.now()))
            throw new IllegalArgumentException("Cannot book a past date");

        Mentor mentor = (Mentor) staffMemberRepository.findById(mentorID)
                .filter(s -> s instanceof Mentor)
                .orElseThrow(() -> new IllegalArgumentException("Mentor not found"));

        Team team = teamRepository.findById(teamID)
                .orElseThrow(() -> new IllegalArgumentException("Team not found"));

        var hackathon = hackathonRepository.findById(hackathonID)
                .orElseThrow(() -> new IllegalArgumentException("Hackathon not found"));

        var user = userRepository.findById(userID)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<LocalDate> freeDates = fakeCalendarService.getFreeDates(date, date);
        if (freeDates.isEmpty())
            throw new IllegalArgumentException("Date not available");

        // Genera l'ID fittizio e occupa lo slot di tempo
        String fakeEventID = fakeCalendarService.createEvent(date);

        SupportRequest request = new SupportRequest(
                null, date, hackathon, user, team, mentor);
        request.setGoogleEventID(fakeEventID); // Manteniamo la property o la rinominiamo in eventID

        return supportRepository.save(request);
    }

    public void cancelBooking(Long requestID) {
        if (requestID == null)
            throw new IllegalArgumentException("Request ID cannot be null");

        SupportRequest request = supportRepository.findById(requestID)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        if (request.getGoogleEventID() != null) {
            // Rilascia lo slot temporale nel calendario fittizio
            fakeCalendarService.deleteEvent(request.getGoogleEventID(), request.getRequestedDate());
        }

        supportRepository.delete(request);
    }
}