package Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import unicam.hackhub.model.*;
import unicam.hackhub.repository.*;
import unicam.hackhub.service.HackathonService;

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

    @BeforeEach
    void setUp() {
        hackathonRepository = new HackathonRepositoryImplementation();
        staffMemberRepository = new StaffMemberRepositoryImplementation();
        userRepository = new UserRepositoryImplementation();

        hackathonService = new HackathonService(hackathonRepository, staffMemberRepository, userRepository);
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
        hackathonRepository.save(hackathon);
        Long hackathonId = hackathon.getId(); // Assuming ID generation or setup logic handles this

        // Setup a non-staff user
        User user = new User(10L, "John Doe", "john@example.com");
        userRepository.save(user);

        assertDoesNotThrow(() -> hackathonService.addMentor("john@example.com", hackathonId));
    }

    @Test
    void addMentor_UserNotFound_ThrowsException() {
        Hackathon hackathon = new Hackathon("Hackathon 2", "Rules", LocalDate.now(), LocalDate.now().plusDays(1), LocalDate.now().plusDays(2), "Loc", "Prize", new RegistrationState(), 5, organizer, new Judge(3L, "J"), Collections.emptyList());
        hackathonRepository.save(hackathon);

        assertThrows(IllegalArgumentException.class, () ->
                hackathonService.addMentor("nonexistent@example.com", hackathon.getId())
        );
    }

    @Test
    void addMentor_UserAlreadyMentor_ThrowsException() {
        Hackathon hackathon = new Hackathon("Hackathon 3", "Rules", LocalDate.now(), LocalDate.now().plusDays(1), LocalDate.now().plusDays(2), "Loc", "Prize", new RegistrationState(), 5, organizer, new Judge(3L, "J"), Collections.emptyList());
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
        overSizedTeam.getSize(); // Explicitly bigger than 4

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
}