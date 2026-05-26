package Model;

import org.junit.jupiter.api.Test;
import unicam.hackhub.model.ConcludedState;
import unicam.hackhub.model.EvaluationState;
import unicam.hackhub.model.Hackathon;
import unicam.hackhub.model.InProgressState;
import unicam.hackhub.model.RegistrationState;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class HackathonStateTest {

    @Test
    void registrationState_AllowsTeamRegistration() {
        RegistrationState state = new RegistrationState();

        assertDoesNotThrow(state::onRegisterTeam);
    }

    @Test
    void registrationState_DoesNotAllowUpload() {
        RegistrationState state = new RegistrationState();

        assertThrows(IllegalStateException.class, state::onUpload);
    }

    @Test
    void inProgressState_DoesNotAllowTeamRegistration() {
        InProgressState state = new InProgressState();

        assertThrows(IllegalStateException.class, state::onRegisterTeam);
    }

    @Test
    void inProgressState_AllowsUpload() {
        InProgressState state = new InProgressState();

        assertDoesNotThrow(state::onUpload);
    }

    @Test
    void evaluationState_DoesNotAllowTeamRegistrationOrUpload() {
        EvaluationState state = new EvaluationState();

        assertThrows(IllegalStateException.class, state::onRegisterTeam);
        assertThrows(IllegalStateException.class, state::onUpload);
    }

    @Test
    void concludedState_DoesNotAllowTeamRegistrationOrUpload() {
        ConcludedState state = new ConcludedState();

        assertThrows(IllegalStateException.class, state::onRegisterTeam);
        assertThrows(IllegalStateException.class, state::onUpload);
    }

    @Test
    void hackathon_DelegatesOperationsToCurrentState() {
        Hackathon hackathon = new Hackathon();
        hackathon.setState(new RegistrationState());

        assertDoesNotThrow(hackathon::onRegisterTeam);
        assertThrows(IllegalStateException.class, hackathon::onUpload);
    }

    @Test
    void hackathon_WithoutStateThrowsException() {
        Hackathon hackathon = new Hackathon();

        assertThrows(IllegalStateException.class, hackathon::onRegisterTeam);
        assertThrows(IllegalStateException.class, hackathon::onUpload);
    }

}