package Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import unicam.hackhub.model.Submission;
import unicam.hackhub.model.Team;
import unicam.hackhub.repository.SubmissionRepository;
import unicam.hackhub.repository.TeamRepository;
import unicam.hackhub.service.SubmissionService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubmissionServiceTest {

    @Mock
    private SubmissionRepository submissionRepository;

    @Mock
    private TeamRepository teamRepository;

    private SubmissionService submissionService;

    private Team team;
    private Submission submission;

    @BeforeEach
    void setUp() {
        submissionService = new SubmissionService(submissionRepository, teamRepository);

        team = new Team(1L, "Team Alpha", List.of());
        submission = new Submission(1L, "Initial Submission");
    }

    @Test
    @DisplayName("uploadSubmission - valid team and submission")
    void uploadSubmission_validData_returnsSubmission() {
        when(teamRepository.findByID(1L)).thenReturn(team);
        when(submissionRepository.save(submission)).thenReturn(submission);
        when(teamRepository.save(team)).thenReturn(team);

        Submission result = submissionService.uploadSubmission(1L, submission);

        assertNotNull(result);
        assertEquals("Initial Submission", result.getName());
        assertEquals(submission, team.getSubmission());

        verify(teamRepository).findByID(1L);
        verify(submissionRepository).save(submission);
        verify(teamRepository).save(team);
    }

    @Test
    @DisplayName("uploadSubmission - null data throws exception")
    void uploadSubmission_nullData_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> submissionService.uploadSubmission(null, submission));

        assertThrows(IllegalArgumentException.class,
                () -> submissionService.uploadSubmission(1L, null));
    }

    @Test
    @DisplayName("uploadSubmission - team not found throws exception")
    void uploadSubmission_teamNotFound_throwsException() {
        when(teamRepository.findByID(1L)).thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> submissionService.uploadSubmission(1L, submission));

        assertEquals("Team not found", exception.getMessage());

        verify(submissionRepository, never()).save(any());
        verify(teamRepository, never()).save(any());
    }

    @Test
    @DisplayName("updateSubmission - existing submission")
    void updateSubmission_existingSubmission_returnsUpdatedSubmission() {
        when(submissionRepository.findByID(1L)).thenReturn(submission);
        when(submissionRepository.save(submission)).thenReturn(submission);

        Submission result = submissionService.updateSubmission(1L, "Updated Submission");

        assertNotNull(result);
        assertEquals("Updated Submission", result.getName());
        assertNotNull(result.getSubmissionOnDate());

        verify(submissionRepository).findByID(1L);
        verify(submissionRepository).save(submission);
    }

    @Test
    @DisplayName("updateSubmission - invalid data throws exception")
    void updateSubmission_invalidData_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> submissionService.updateSubmission(null, "Updated Submission"));

        assertThrows(IllegalArgumentException.class,
                () -> submissionService.updateSubmission(1L, null));

        assertThrows(IllegalArgumentException.class,
                () -> submissionService.updateSubmission(1L, ""));
    }

    @Test
    @DisplayName("updateSubmission - submission not found throws exception")
    void updateSubmission_notFound_throwsException() {
        when(submissionRepository.findByID(1L)).thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> submissionService.updateSubmission(1L, "Updated Submission"));

        assertEquals("Submission not found", exception.getMessage());

        verify(submissionRepository, never()).save(any());
    }

}