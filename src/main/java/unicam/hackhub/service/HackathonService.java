package unicam.hackhub.service;

import org.springframework.stereotype.Service;
import unicam.hackhub.model.*;
import unicam.hackhub.model.state.ConcludedState;
import unicam.hackhub.repository.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HackathonService {

    private final HackathonRepository hackathonRepository;
    private final StaffMemberRepository staffMemberRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final RegistrationRepository registrationRepository;
    private final SupportRepository supportRepository;


    public HackathonService(HackathonRepository hackathonRepository,
                            StaffMemberRepository staffMemberRepository,
                            UserRepository userRepository, TeamRepository teamRepository,
                            RegistrationRepository registrationRepository,
                            SupportRepository supportRepository) {
        this.hackathonRepository = hackathonRepository;
        this.staffMemberRepository = staffMemberRepository;
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
        this.registrationRepository = registrationRepository;
        this.supportRepository = supportRepository;
    }

    /**
     * Crea un hackathon.
     */
    public Hackathon createHackathon(String name, String rulebook, LocalDate registrationDeadline,
                                     LocalDate startDate, LocalDate endDate, String location,
                                     String prize, HackathonState state, int maxTeamSize,
                                     Organizer organizer, Long mentorID, Long judgeID) {

        validateDates(name, rulebook, registrationDeadline, startDate, endDate,
                location, prize, state, maxTeamSize, organizer, mentorID, judgeID);

        existsHackathonByName(name);

        Mentor mentor = (Mentor) staffMemberRepository.findById(mentorID)
                .filter(s -> s instanceof Mentor)
                .orElseThrow(() -> new IllegalArgumentException("Staff member not found"));

        Judge judge = (Judge) staffMemberRepository.findById(judgeID)
                .filter(s -> s instanceof Judge)
                .orElseThrow(() -> new IllegalArgumentException("Staff member not found"));

        Hackathon hackathon = new Hackathon(name, rulebook, registrationDeadline, startDate,
                endDate, location, prize, state, maxTeamSize, organizer, judge, List.of(mentor));

        return hackathonRepository.save(hackathon);
    }

    /**
     * Modifica un hackathon esistente.
     */
    public Hackathon editHackathon(Long hackathonID, String name, String rulebook,
                                   LocalDate registrationDeadline, String location,
                                   String prize, int maxTeamSize, Long judgeID, Long mentorID) {
        Hackathon hackathon = hackathonRepository.findById(hackathonID)
                .orElseThrow(() -> new IllegalArgumentException("Hackathon not found"));

        Judge judge = null;
        if (judgeID != null) {
            judge = (Judge) staffMemberRepository.findById(judgeID)
                    .filter(s -> s instanceof Judge)
                    .orElseThrow(() -> new IllegalArgumentException("Judge not found"));
        }

        Mentor mentor = null;
        if (mentorID != null) {
            mentor = (Mentor) staffMemberRepository.findById(mentorID)
                    .filter(s -> s instanceof Mentor)
                    .orElseThrow(() -> new IllegalArgumentException("Mentor not found"));
        }

        checkHackathonAvailability(hackathon);

        if (name != null) hackathon.setNameHackathon(name);
        if (rulebook != null) hackathon.setRulebook(rulebook);
        if (registrationDeadline != null) hackathon.setRegistrationDeadline(registrationDeadline);
        if (location != null) hackathon.setLocation(location);
        if (prize != null) hackathon.setPrize(prize);
        if (maxTeamSize > 0) hackathon.setMaxTeamSize(maxTeamSize);
        if (judge != null) hackathon.setJudge(judge);
        if (mentor != null) addMentor(mentor.getEmail(), hackathonID);

        return hackathonRepository.save(hackathon);
    }

    /**
     * Aggiunge un Mentore a un hackathon.
     */
    public Mentor addMentor(String email, Long hackathonID) {
        Hackathon hackathon = hackathonRepository.findById(hackathonID)
                .orElseThrow(() -> new IllegalArgumentException("Hackathon not found"));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        staffMemberRepository.findByEmail(email).ifPresent(staff -> {
            if (staff instanceof Mentor)
                throw new IllegalArgumentException("User already Mentor");
            if (staff instanceof Judge)
                throw new IllegalArgumentException("User already Judge");
        });

        Mentor mentor = new Mentor(null, user.getName(), email, hackathon);
        hackathon.addMentor(mentor);
        return mentor;
    }

    /**
     * Restituisce gli hackathon assegnati a un membro dello staff.
     */
    public List<Hackathon> getAssignedHackathons(Long staffMemberID) {
        if (staffMemberID == null)
            throw new IllegalArgumentException("Staff member ID cannot be null");

        staffMemberRepository.findById(staffMemberID)
                .orElseThrow(() -> new IllegalArgumentException("Staff member not found"));

        return hackathonRepository.findByStaffMemberId(staffMemberID);
    }

    /**
     * Restituisce i team registrati per un hackathon assegnato allo staff member.
     */
    public List<Registration> getParticipants(Long staffMemberID, Long hackathonID) {
        if (staffMemberID == null || hackathonID == null)
            throw new IllegalArgumentException("Staff member ID and hackathon ID cannot be null");

        Hackathon hackathon = checkAssignedHackathon(staffMemberID, hackathonID);

        List<Registration> registrations = registrationRepository.findByHackathon(hackathon);
        if (registrations.isEmpty())
            throw new IllegalArgumentException("No participant registered");

        return registrations;
    }

    /**
     * Recupera la lista delle richieste di supporto per un hackathon.
     */
    public List<SupportRequest> getRequestsSupport(Long mentorID, Long hackathonID) {
        if (mentorID == null || hackathonID == null)
            throw new IllegalArgumentException("Mentor ID and hackathon ID cannot be null");

        Mentor mentor = (Mentor) staffMemberRepository.findById(mentorID)
                .filter(s -> s instanceof Mentor)
                .orElseThrow(() -> new IllegalArgumentException("Mentor not found"));

        checkAssignedHackathon(mentorID, hackathonID);

        List<SupportRequest> requestsSupport = supportRepository.findByMentor(mentor);

        if (requestsSupport == null || requestsSupport.isEmpty())
            throw new IllegalArgumentException("No request support found");

        return requestsSupport;
    }

    /**
     * Dichiara il team vincitore.
     */
    public void declareWinner(Long organizerID, Long teamID, Long hackathonID, double prizeAmount) {
        if (organizerID == null || teamID == null || hackathonID == null)
            throw new IllegalArgumentException("IDs cannot be null");

        // 1. Verifica che l'organizzatore esista e abbia i permessi
        User organizer = userRepository.findById(organizerID)
                .orElseThrow(() -> new IllegalArgumentException("Organizer not found"));
        // (Opzionale) if (!organizer.isOrganizer()) throw new SecurityException("Not authorized");

        // 2. Recupera l'Hackathon
        Hackathon hackathon = hackathonRepository.findById(hackathonID)
                .orElseThrow(() -> new IllegalArgumentException("Hackathon not found"));

        // 3. Recupera il Team vincitore
        Team winningTeam = teamRepository.findById(teamID)
                .orElseThrow(() -> new IllegalArgumentException("Team not found"));
        hackathon.setWinnerTeam(hackathonID);

        // 4. Salva lo stato nel Database
        hackathon.changeState(new ConcludedState());
        hackathonRepository.save(hackathon);

        System.out.println("L'organizzatore " + organizer.getName() + " ha proclamato vincitore il Team: " + winningTeam.getTeamName());
    }

    public void changeState(HackathonState state) {
        state.onUpload();
    }

    public void existsHackathonByName(String name) {
        hackathonRepository.findByNameHackathon(name).ifPresent(h -> {
            throw new IllegalArgumentException("Name already used");
        });
    }

    public void validateDates(String name, String rulebook, LocalDate registrationDeadline,
                              LocalDate startDate, LocalDate endDate, String location,
                              String prize, HackathonState state, int maxTeamSize, Organizer organizer,
                              Long mentorID, Long judgeID) {
        if (name == null || rulebook == null || registrationDeadline == null || startDate == null
                || endDate == null || location == null || prize == null || state == null
                || maxTeamSize <= 0 || organizer == null || mentorID == null || judgeID == null) {
            throw new IllegalArgumentException("Wrong dates selected");
        }

        if (registrationDeadline.isAfter(startDate)) {
            throw new IllegalArgumentException("Registration deadline cannot be after start date");
        }

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
    }

    public void checkHackathonAvailability(Hackathon hackathon) {
        if (!(hackathon.isRegistrationOpen())) {
            throw new IllegalArgumentException("Hackathon is not open");
        }
    }

    public void checkTeamSize(Team team, Hackathon hackathon) {
        if (team.getSize() > hackathon.getMaxTeamSize()) {
            throw new IllegalArgumentException("Team size exceeds limit");
        }
    }

    public void checkTeamAlreadyRegistered(Registration registration) {
        if (registration != null && registration.exists()) {
            throw new IllegalArgumentException("Team already registered");
        }
    }

    /**
     * Recupera la lista delle sottomissioni per un hackathon.
     */
    public List<Submission> getSubmissions(Long staffMemberID, Long hackathonID) {
        if (staffMemberID == null || hackathonID == null)
            throw new IllegalArgumentException("Staff member ID and hackathon ID cannot be null");

        Hackathon hackathon = checkAssignedHackathon(staffMemberID, hackathonID);

        List<Registration> registrations = registrationRepository.findByHackathon(hackathon);

        if (registrations == null || registrations.isEmpty())
            throw new IllegalArgumentException("No submissions found");

        List<Submission> submissions = registrations.stream()
                .map(r -> r.getTeam().getSubmission())
                .filter(s -> s != null)
                .collect(Collectors.toList());

        if (submissions.isEmpty())
            throw new IllegalArgumentException("No submissions found");

        return submissions;
    }

    /**
     * Verifica che un hackathon sia tra quelli assegnati allo staff member
     * e ne restituisce l'entità. Centralizza una logica ripetuta in più metodi.
     */
    private Hackathon checkAssignedHackathon(Long staffMemberID, Long hackathonID) {
        boolean assigned = getAssignedHackathons(staffMemberID).stream()
                .anyMatch(h -> h.getId() != null && h.getId().equals(hackathonID));

        if (!assigned)
            throw new IllegalArgumentException("Hackathon not assigned to staff member");

        return hackathonRepository.findById(hackathonID)
                .orElseThrow(() -> new IllegalArgumentException("Hackathon not found"));
    }
}