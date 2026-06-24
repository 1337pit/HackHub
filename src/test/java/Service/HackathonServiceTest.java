package Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import unicam.hackhub.model.*;
import unicam.hackhub.model.state.RegistrationState;
import unicam.hackhub.repository.*;
import unicam.hackhub.service.HackathonService;
import java.util.List;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class HackathonServiceTest {

    private HackathonService hackathonService;
    private HackathonRepository hackathonRepository;
    private StaffMemberRepository staffMemberRepository;
    private UserRepository userRepository;
    private Organizer organizer;
    private Hackathon hackathon;
    private RegistrationRepository registrationRepository;

    @BeforeEach
    void setUp() {
        hackathonRepository = new HackathonRepositoryImplementation();
        staffMemberRepository = new StaffMemberRepositoryImplementation();
        userRepository = new UserRepositoryImplementation();
        registrationRepository = new RegistrationRepositoryImplementation();

        hackathonService = new HackathonService(
                hackathonRepository,
                staffMemberRepository,
                userRepository,
                registrationRepository
        );
        organizer = new Organizer(1L, "Organizer");

        hackathon = new Hackathon(
                "Base", "Rules", LocalDate.now(), LocalDate.now().plusDays(1), LocalDate.now().plusDays(2),
                "Loc", "Prize", new RegistrationState(), 5, organizer, new Judge(3L, "J"), new ArrayList<>()
        );

        // Seed data needed for hackathon creation tests
        staffMemberRepository.save(new Mentor(2L, "Mentor", "mentor@google.com", hackathon));
        staffMemberRepository.save(new Judge(3L, "Judge"));
    }

    // =========================================================================
    // CREATE HACKATHON TESTS
    // =========================================================================

    @Test
    void createHackathon_Success() {
        Hackathon hackathon = hackathonService.createHackathon(
                "HackHub Test",
                "Rulebook",
                LocalDate.now().plusDays(5),
                LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(12),
                "Camerino",
                "1000",
                new RegistrationState(),
                5,
                organizer,
                2L,
                3L
        );

        assertNotNull(hackathon);
        assertEquals("HackHub Test", hackathon.getNameHackathon());
        assertEquals(5, hackathon.getMaxTeamSize());
    }

    @Test
    void createHackathon_DuplicateName_ThrowsException() {
        hackathonService.createHackathon(
                "HackHub Test",
                "Rulebook",
                LocalDate.now().plusDays(5),
                LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(12),
                "Camerino",
                "1000",
                new RegistrationState(),
                5,
                organizer,
                2L,
                3L
        );

        assertThrows(IllegalArgumentException.class, () -> hackathonService.createHackathon(
                "HackHub Test",
                "Rulebook",
                LocalDate.now().plusDays(5),
                LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(12),
                "Camerino",
                "1000",
                new RegistrationState(),
                5,
                organizer,
                2L,
                3L
        ));
    }

    @Test
    void createHackathon_StaffNotFound_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> hackathonService.createHackathon(
                "Unique Name",
                "Rulebook",
                LocalDate.now().plusDays(5),
                LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(12),
                "Camerino",
                "1000",
                new RegistrationState(),
                5,
                organizer,
                999L, // Non-existent mentor ID
                3L
        ));
    }

    // =========================================================================
    // VALIDATION TESTS
    // =========================================================================

    @Test
    void validateDates_InvalidMaxTeamSize_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> hackathonService.validateDates(
                "HackHub Test",
                "Rulebook",
                LocalDate.now().plusDays(5),
                LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(12),
                "Camerino",
                "1000",
                new RegistrationState(),
                0, // Invalid size
                organizer,
                2L,
                3L
        ));
    }

    @Test
    void validateDates_RegistrationDeadlineAfterStartDate_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> hackathonService.validateDates(
                "HackHub Test",
                "Rulebook",
                LocalDate.now().plusDays(10), // Deadline
                LocalDate.now().plusDays(5),  // Start date (before deadline)
                LocalDate.now().plusDays(12),
                "Camerino",
                "1000",
                new RegistrationState(),
                5,
                organizer,
                2L,
                3L
        ));
    }

    @Test
    void validateDates_StartDateAfterEndDate_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> hackathonService.validateDates(
                "HackHub Test",
                "Rulebook",
                LocalDate.now().plusDays(2),
                LocalDate.now().plusDays(15), // Start date
                LocalDate.now().plusDays(10), // End date (before start)
                "Camerino",
                "1000",
                new RegistrationState(),
                5,
                organizer,
                2L,
                3L
        ));
    }

    // =========================================================================
    // ADD MENTOR TESTS
    // =========================================================================

    @Test
    void addMentor_Success() {
        // Create an existing Hackathon context
        Hackathon hackathon = new Hackathon("Hackathon 1", "Rules", LocalDate.now(), LocalDate.now().plusDays(1), LocalDate.now().plusDays(2), "Loc", "Prize", new RegistrationState(), 5, organizer, new Judge(3L, "J"), Collections.emptyList());
        hackathon.setId(100L);
        hackathonRepository.save(hackathon);
        Long hackathonId = hackathon.getId();

        // Setup a non-staff user
        User user = new User(10L, "John Doe", "john@example.com");
        userRepository.save(user);

        assertDoesNotThrow(() -> hackathonService.addMentor("john@example.com", hackathonId));
    }

    @Test
    void addMentor_UserNotFound_ThrowsException() {
        Hackathon hackathon = new Hackathon("Hackathon 2", "Rules", LocalDate.now(), LocalDate.now().plusDays(1), LocalDate.now().plusDays(2), "Loc", "Prize", new RegistrationState(), 5, organizer, new Judge(3L, "J"), Collections.emptyList());
        hackathon.setId(101L);
        hackathonRepository.save(hackathon);

        assertThrows(IllegalArgumentException.class, () ->
                hackathonService.addMentor("nonexistent@example.com", hackathon.getId())
        );
    }

    @Test
    void addMentor_UserAlreadyMentor_ThrowsException() {
        Hackathon hackathon = new Hackathon("Hackathon 3", "Rules", LocalDate.now(), LocalDate.now().plusDays(1), LocalDate.now().plusDays(2), "Loc", "Prize", new RegistrationState(), 5, organizer, new Judge(3L, "J"), Collections.emptyList());
        hackathon.setId(102L);
        hackathonRepository.save(hackathon);

        User user = new User(11L, "Existing Mentor", "mentor@example.com");
        userRepository.save(user);

        // Make the user a Mentor in the repository database state
        Mentor existingMentor = new Mentor(user.getId(), user.getName(), user.getEmail(), hackathon);
        staffMemberRepository.save(existingMentor);

        assertThrows(IllegalArgumentException.class, () ->
                hackathonService.addMentor("mentor@example.com", hackathon.getId())
        );
    }

    // =========================================================================
    // AVAILABILITY & TEAM SIZE CHECKS
    // =========================================================================

    @Test
    void checkTeamSize_ExceedsLimit_ThrowsException() {
        Hackathon hackathon = new Hackathon();
        hackathon.setMaxTeamSize(4);

        Team overSizedTeam = new Team();

        ArrayList<User> members = new ArrayList<>();
        members.add(new User(1L, "User 1", "user1@example.com"));
        members.add(new User(2L, "User 2", "user2@example.com"));
        members.add(new User(3L, "User 3", "user3@example.com"));
        members.add(new User(4L, "User 4", "user4@example.com"));
        members.add(new User(5L, "User 5", "user5@example.com"));

        overSizedTeam.setMembers(members);

        assertThrows(IllegalArgumentException.class, () ->
                hackathonService.checkTeamSize(overSizedTeam, hackathon)
        );
    }

    @Test
    void checkTeamSize_WithinLimit_DoesNotThrow() {
        Hackathon hackathon = new Hackathon();
        hackathon.setMaxTeamSize(5);

        Team validTeam = new Team();
        validTeam.getSize();

        assertDoesNotThrow(() -> hackathonService.checkTeamSize(validTeam, hackathon));
    }

    // =========================================================================
    // GET ASSIGNED HACKATHONS TESTS
    // =========================================================================

    @Test
    void getAssignedHackathons_Success() {
        Mentor assignedMentor = new Mentor(20L, "Assigned Mentor",
                "assigned@example.com", null);
        staffMemberRepository.save(assignedMentor);

        Hackathon assignedHackathon = new Hackathon(
                "Assigned Hackathon",
                "Rules",
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(2),
                LocalDate.now().plusDays(3),
                "Camerino",
                "Prize",
                new RegistrationState(),
                5,
                organizer,
                new Judge(30L, "Judge"),
                List.of(assignedMentor)
        );
        assignedHackathon.setId(200L);

        Hackathon otherHackathon = new Hackathon(
                "Other Hackathon",
                "Rules",
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(2),
                LocalDate.now().plusDays(3),
                "Online",
                "Prize",
                new RegistrationState(),
                5,
                new Organizer(40L, "Other Organizer"),
                new Judge(41L, "Other Judge"),
                new ArrayList<>()
        );
        otherHackathon.setId(201L);

        hackathonRepository.save(assignedHackathon);
        hackathonRepository.save(otherHackathon);

        List<Hackathon> result =
                hackathonService.getAssignedHackathons(20L);

        assertEquals(1, result.size());
        assertEquals(assignedHackathon, result.get(0));
    }

    @Test
    void getAssignedHackathons_StaffNotFound_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                hackathonService.getAssignedHackathons(999L)
        );
    }

    @Test
    void getAssignedHackathons_NullID_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                hackathonService.getAssignedHackathons(null)
        );
    }

    // =========================================================================
    // GET PARTICIPANTS TESTS
    // =========================================================================

    @Test
    void getParticipants_Success() {
        Mentor staffMember = new Mentor(
                20L,
                "Assigned Mentor",
                "mentor@example.com",
                null
        );
        staffMemberRepository.save(staffMember);

        Hackathon assignedHackathon = new Hackathon(
                "Assigned Hackathon",
                "Rules",
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(2),
                LocalDate.now().plusDays(3),
                "Camerino",
                "Prize",
                new RegistrationState(),
                5,
                organizer,
                new Judge(30L, "Judge"),
                List.of(staffMember)
        );
        assignedHackathon.setId(300L);
        hackathonRepository.save(assignedHackathon);

        Team team = new Team(
                50L,
                "Team Alpha",
                List.of(new User(51L, "Andrea", "andrea@example.com"))
        );

        Registration registration =
                new Registration(60L, team, assignedHackathon);

        registrationRepository.save(registration);

        List<Registration> result =
                hackathonService.getParticipants(20L, 300L);

        assertEquals(1, result.size());
        assertEquals(registration, result.get(0));
        assertEquals(team, result.get(0).getTeam());
    }

    @Test
    void getParticipants_NoParticipants_ThrowsException() {
        Mentor staffMember = new Mentor(
                21L,
                "Assigned Mentor",
                "mentor2@example.com",
                null
        );
        staffMemberRepository.save(staffMember);

        Hackathon assignedHackathon = new Hackathon(
                "Empty Hackathon",
                "Rules",
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(2),
                LocalDate.now().plusDays(3),
                "Online",
                "Prize",
                new RegistrationState(),
                5,
                organizer,
                new Judge(31L, "Judge"),
                List.of(staffMember)
        );
        assignedHackathon.setId(301L);
        hackathonRepository.save(assignedHackathon);

        assertThrows(IllegalArgumentException.class, () ->
                hackathonService.getParticipants(21L, 301L)
        );
    }

    @Test
    void getParticipants_HackathonNotAssigned_ThrowsException() {
        Mentor staffMember = new Mentor(
                22L,
                "Staff Member",
                "mentor3@example.com",
                null
        );
        staffMemberRepository.save(staffMember);

        Hackathon otherHackathon = new Hackathon(
                "Other Hackathon",
                "Rules",
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(2),
                LocalDate.now().plusDays(3),
                "Online",
                "Prize",
                new RegistrationState(),
                5,
                organizer,
                new Judge(32L, "Judge"),
                new ArrayList<>()
        );
        otherHackathon.setId(302L);
        hackathonRepository.save(otherHackathon);

        assertThrows(IllegalArgumentException.class, () ->
                hackathonService.getParticipants(22L, 302L)
        );
    }

    @Test
    void getParticipants_NullID_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                hackathonService.getParticipants(null, 300L)
        );

        assertThrows(IllegalArgumentException.class, () ->
                hackathonService.getParticipants(20L, null)
        );
    }

    // =========================================================================
    // GET SUBMISSIONS TESTS
    // =========================================================================

    @Test
    void getSubmissions_Success() {
        Mentor staffMember = new Mentor(
                20L,
                "Assigned Mentor",
                "mentor@example.com",
                null
        );
        staffMemberRepository.save(staffMember);

        Hackathon assignedHackathon = new Hackathon(
                "Assigned Hackathon",
                "Rules",
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(2),
                LocalDate.now().plusDays(3),
                "Camerino",
                "Prize",
                new RegistrationState(),
                5,
                organizer,
                new Judge(30L, "Judge"),
                List.of(staffMember)
        );
        assignedHackathon.setId(400L);
        hackathonRepository.save(assignedHackathon);

        Submission submission = new Submission(1L, "My Submission");
        Team team = new Team(50L, "Team Alpha",
                List.of(new User(51L, "Andrea", "andrea@example.com")));
        team.setSubmission(submission);

        Registration registration = new Registration(60L, team, assignedHackathon);
        registrationRepository.save(registration);

        List<Submission> result = hackathonService.getSubmissions(20L, 400L);

        assertEquals(1, result.size());
        assertEquals("My Submission", result.get(0).getName());
    }

    @Test
    void getSubmissions_NullParams_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                hackathonService.getSubmissions(null, 400L)
        );
        assertThrows(IllegalArgumentException.class, () ->
                hackathonService.getSubmissions(20L, null)
        );
    }

    @Test
    void getSubmissions_HackathonNotAssigned_ThrowsException() {
        Mentor staffMember = new Mentor(
                21L,
                "Assigned Mentor",
                "mentor2@example.com",
                null
        );
        staffMemberRepository.save(staffMember);

        Hackathon otherHackathon = new Hackathon(
                "Other Hackathon",
                "Rules",
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(2),
                LocalDate.now().plusDays(3),
                "Online",
                "Prize",
                new RegistrationState(),
                5,
                organizer,
                new Judge(31L, "Judge"),
                new ArrayList<>()  // staffMember non assegnato
        );
        otherHackathon.setId(401L);
        hackathonRepository.save(otherHackathon);

        assertThrows(IllegalArgumentException.class, () ->
                hackathonService.getSubmissions(21L, 401L)
        );
    }

    @Test
    void getSubmissions_NoRegistrations_ThrowsException() {
        Mentor staffMember = new Mentor(
                22L,
                "Assigned Mentor",
                "mentor3@example.com",
                null
        );
        staffMemberRepository.save(staffMember);

        Hackathon assignedHackathon = new Hackathon(
                "Empty Hackathon",
                "Rules",
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(2),
                LocalDate.now().plusDays(3),
                "Online",
                "Prize",
                new RegistrationState(),
                5,
                organizer,
                new Judge(32L, "Judge"),
                List.of(staffMember)
        );
        assignedHackathon.setId(402L);
        hackathonRepository.save(assignedHackathon);

        // Nessuna registrazione salvata
        assertThrows(IllegalArgumentException.class, () ->
                hackathonService.getSubmissions(22L, 402L)
        );
    }

    @Test
    void getSubmissions_TeamWithoutSubmission_ThrowsException() {
        Mentor staffMember = new Mentor(
                23L,
                "Assigned Mentor",
                "mentor4@example.com",
                null
        );
        staffMemberRepository.save(staffMember);

        Hackathon assignedHackathon = new Hackathon(
                "Hackathon No Sub",
                "Rules",
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(2),
                LocalDate.now().plusDays(3),
                "Online",
                "Prize",
                new RegistrationState(),
                5,
                organizer,
                new Judge(33L, "Judge"),
                List.of(staffMember)
        );
        assignedHackathon.setId(403L);
        hackathonRepository.save(assignedHackathon);

        // Team senza submission
        Team team = new Team(55L, "Team Beta",
                List.of(new User(56L, "Marco", "marco@example.com")));

        Registration registration = new Registration(65L, team, assignedHackathon);
        registrationRepository.save(registration);

        assertThrows(IllegalArgumentException.class, () ->
                hackathonService.getSubmissions(23L, 403L)
        );
    }
}