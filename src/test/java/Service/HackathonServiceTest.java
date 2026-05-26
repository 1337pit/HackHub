package Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import unicam.hackhub.model.Hackathon;
import unicam.hackhub.model.Judge;
import unicam.hackhub.model.Mentor;
import unicam.hackhub.model.Organizer;
import unicam.hackhub.model.RegistrationState;
import unicam.hackhub.repository.HackathonRepository;
import unicam.hackhub.repository.HackathonRepositoryImplementation;
import unicam.hackhub.repository.StaffMemberRepository;
import unicam.hackhub.repository.StaffMemberRepositoryImplementation;
import unicam.hackhub.service.HackathonService;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class HackathonServiceTest {

    private HackathonService hackathonService;
    private HackathonRepository hackathonRepository;
    private StaffMemberRepository staffMemberRepository;
    private Organizer organizer;

    @BeforeEach
    void setUp() {
        hackathonRepository = new HackathonRepositoryImplementation();
        staffMemberRepository = new StaffMemberRepositoryImplementation();
        hackathonService = new HackathonService(hackathonRepository, staffMemberRepository);
        organizer = new Organizer(1L, "Organizer");

        staffMemberRepository.save(new Mentor(2L, "Mentor"));
        staffMemberRepository.save(new Judge(3L, "Judge"));
    }

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
                0,
                organizer,
                2L,
                3L
        ));
    }

}