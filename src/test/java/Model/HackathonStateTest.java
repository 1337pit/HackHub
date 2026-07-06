package Model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import unicam.hackhub.model.Hackathon;
import unicam.hackhub.model.state.ConcludedState;
import unicam.hackhub.model.state.EvaluationState;
import unicam.hackhub.model.state.InProgressState;
import unicam.hackhub.model.state.RegistrationState;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HackathonStateTest {

    // =======================================================================
    // 1. Test sui singoli Stati (Unitari)
    // =======================================================================

    @Test
    @DisplayName("RegistrationState – Consente registrazione team ma vieta l'upload")
    void testRegistrationState() {
        RegistrationState state = new RegistrationState();

        assertDoesNotThrow(state::onRegisterTeam);
        assertThrows(IllegalStateException.class, state::onUpload);
    }

    @Test
    @DisplayName("InProgressState – Vieta registrazione team ma consente l'upload")
    void testInProgressState() {
        InProgressState state = new InProgressState();

        assertThrows(IllegalStateException.class, state::onRegisterTeam);
        assertDoesNotThrow(state::onUpload);
    }

    @Test
    @DisplayName("EvaluationState – Vieta sia la registrazione che l'upload")
    void testEvaluationState() {
        EvaluationState state = new EvaluationState();

        assertThrows(IllegalStateException.class, state::onRegisterTeam);
        assertThrows(IllegalStateException.class, state::onUpload);
    }

    @Test
    @DisplayName("ConcludedState – Vieta sia la registrazione che l'upload")
    void testConcludedState() {
        ConcludedState state = new ConcludedState();

        assertThrows(IllegalStateException.class, state::onRegisterTeam);
        assertThrows(IllegalStateException.class, state::onUpload);
    }
}