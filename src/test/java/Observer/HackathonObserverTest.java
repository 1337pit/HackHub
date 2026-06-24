package Observer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import unicam.hackhub.model.*;
import unicam.hackhub.model.observer.*;
import unicam.hackhub.model.state.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HackathonObserverTest {

    private Hackathon hackathon;
    private Judge judge;
    private Mentor mentor;
    private Team team;

    @BeforeEach
    void setUp() {
        judge = new Judge(1L, "Judge Joe");
        mentor = new Mentor(2L, "Mentor Mario", "mentor@example.com", null);
        team = new Team(3L, "Team Alpha", new ArrayList<>());

        hackathon = new Hackathon(
                "HackHub Test", "Rulebook",
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(2),
                LocalDate.now().plusDays(3),
                "Camerino", "1000€",
                new RegistrationState(),
                5,
                new Organizer(10L, "Organizer"),
                judge,
                List.of(mentor)
        );
        hackathon.setId(1L);
    }

    // =========================================================================
    // addObserver TESTS
    // =========================================================================

    @Test
    @DisplayName("addObserver - aggiunge observer correttamente")
    void addObserver_AddsSuccessfully() {
        hackathon.addObserver(judge);
        assertTrue(hackathon.getObservers().contains(judge));
    }

    @Test
    @DisplayName("addObserver - non aggiunge duplicati")
    void addObserver_NoDuplicates() {
        hackathon.addObserver(judge);
        hackathon.addObserver(judge);
        assertEquals(1, hackathon.getObservers().size());
    }

    @Test
    @DisplayName("addObserver - observer null → IllegalArgumentException")
    void addObserver_NullObserver_ThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> hackathon.addObserver(null));
    }

    @Test
    @DisplayName("addObserver - più observer diversi")
    void addObserver_MultipleObservers() {
        hackathon.addObserver(judge);
        hackathon.addObserver(mentor);
        hackathon.addObserver(team);
        assertEquals(3, hackathon.getObservers().size());
    }

    // =========================================================================
    // removeObserver TESTS
    // =========================================================================

    @Test
    @DisplayName("removeObserver - rimuove observer correttamente")
    void removeObserver_RemovesSuccessfully() {
        hackathon.addObserver(judge);
        hackathon.removeObserver(judge);
        assertFalse(hackathon.getObservers().contains(judge));
    }

    @Test
    @DisplayName("removeObserver - rimuove solo l'observer specificato")
    void removeObserver_RemovesOnlySpecified() {
        hackathon.addObserver(judge);
        hackathon.addObserver(mentor);
        hackathon.removeObserver(judge);

        assertFalse(hackathon.getObservers().contains(judge));
        assertTrue(hackathon.getObservers().contains(mentor));
    }

    @Test
    @DisplayName("removeObserver - rimozione observer non presente non lancia eccezioni")
    void removeObserver_NotPresent_DoesNotThrow() {
        assertDoesNotThrow(() -> hackathon.removeObserver(judge));
    }

    // =========================================================================
    // notifyObservers TESTS
    // =========================================================================

    @Test
    @DisplayName("notifyObservers - notifica tutti gli observer registrati")
    void notifyObservers_NotifiesAll() {
        // Usiamo observer di test per tracciare la notifica
        TestObserver observer1 = new TestObserver();
        TestObserver observer2 = new TestObserver();

        hackathon.addObserver(observer1);
        hackathon.addObserver(observer2);

        hackathon.setState(new InProgressState());
        hackathon.notifyObservers();

        assertTrue(observer1.wasNotified());
        assertTrue(observer2.wasNotified());
    }

    @Test
    @DisplayName("notifyObservers - nessun observer → nessuna eccezione")
    void notifyObservers_NoObservers_DoesNotThrow() {
        hackathon.setState(new InProgressState());
        assertDoesNotThrow(() -> hackathon.notifyObservers());
    }

    @Test
    @DisplayName("notifyObservers - observer riceve lo stato corretto")
    void notifyObservers_CorrectStateReceived() {
        TestObserver observer = new TestObserver();
        hackathon.addObserver(observer);

        HackathonState newState = new InProgressState();
        hackathon.setState(newState);
        hackathon.notifyObservers();

        assertSame(newState, observer.getLastState());
    }

    // =========================================================================
    // changeState TESTS (integrazione State + Observer)
    // =========================================================================

    @Test
    @DisplayName("changeState - cambia stato e notifica automaticamente gli observer")
    void changeState_ChangesStateAndNotifies() {
        TestObserver observer = new TestObserver();
        hackathon.addObserver(observer);

        hackathon.changeState(new InProgressState());

        assertInstanceOf(InProgressState.class, hackathon.getState());
        assertTrue(observer.wasNotified());
        assertInstanceOf(InProgressState.class, observer.getLastState());
    }

    @Test
    @DisplayName("changeState - stato null → IllegalArgumentException")
    void changeState_NullState_ThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> hackathon.changeState(null));
    }

    @Test
    @DisplayName("changeState - sequenza completa degli stati")
    void changeState_FullStateSequence() {
        TestObserver observer = new TestObserver();
        hackathon.addObserver(observer);

        hackathon.changeState(new InProgressState());
        assertInstanceOf(InProgressState.class, observer.getLastState());

        hackathon.changeState(new EvaluationState());
        assertInstanceOf(EvaluationState.class, observer.getLastState());

        hackathon.changeState(new ConcludedState());
        assertInstanceOf(ConcludedState.class, observer.getLastState());
    }

    @Test
    @DisplayName("changeState - observer rimosso non riceve notifica")
    void changeState_RemovedObserver_NotNotified() {
        TestObserver observer = new TestObserver();
        hackathon.addObserver(observer);
        hackathon.removeObserver(observer);

        hackathon.changeState(new InProgressState());

        assertFalse(observer.wasNotified());
    }

    // =========================================================================
    // Judge Observer TESTS
    // =========================================================================

    @Test
    @DisplayName("Judge.update - reagisce a EvaluationState")
    void judge_NotifiedOnEvaluationState() {
        hackathon.addObserver(judge);
        assertDoesNotThrow(() -> hackathon.changeState(new EvaluationState()));
    }

    @Test
    @DisplayName("Judge.update - non lancia eccezioni su altri stati")
    void judge_NoExceptionOnOtherStates() {
        hackathon.addObserver(judge);
        assertDoesNotThrow(() -> hackathon.changeState(new InProgressState()));
        assertDoesNotThrow(() -> hackathon.changeState(new RegistrationState()));
        assertDoesNotThrow(() -> hackathon.changeState(new ConcludedState()));
    }

    // =========================================================================
    // Mentor Observer TESTS
    // =========================================================================

    @Test
    @DisplayName("Mentor.update - reagisce a InProgressState")
    void mentor_NotifiedOnInProgressState() {
        hackathon.addObserver(mentor);
        assertDoesNotThrow(() -> hackathon.changeState(new InProgressState()));
    }

    @Test
    @DisplayName("Mentor.update - non lancia eccezioni su altri stati")
    void mentor_NoExceptionOnOtherStates() {
        hackathon.addObserver(mentor);
        assertDoesNotThrow(() -> hackathon.changeState(new EvaluationState()));
        assertDoesNotThrow(() -> hackathon.changeState(new RegistrationState()));
        assertDoesNotThrow(() -> hackathon.changeState(new ConcludedState()));
    }

    // =========================================================================
    // Team Observer TESTS
    // =========================================================================

    @Test
    @DisplayName("Team.update - reagisce a InProgressState")
    void team_NotifiedOnInProgressState() {
        hackathon.addObserver(team);
        assertDoesNotThrow(() -> hackathon.changeState(new InProgressState()));
    }

    @Test
    @DisplayName("Team.update - reagisce a EvaluationState")
    void team_NotifiedOnEvaluationState() {
        hackathon.addObserver(team);
        assertDoesNotThrow(() -> hackathon.changeState(new EvaluationState()));
    }

    @Test
    @DisplayName("Team.update - reagisce a ConcludedState")
    void team_NotifiedOnConcludedState() {
        hackathon.addObserver(team);
        assertDoesNotThrow(() -> hackathon.changeState(new ConcludedState()));
    }

    // =========================================================================
    // Integration TESTS
    // =========================================================================

    @Test
    @DisplayName("Observer pattern - tutti gli observer notificati sul cambio stato")
    void allObservers_NotifiedOnStateChange() {
        hackathon.addObserver(judge);
        hackathon.addObserver(mentor);
        hackathon.addObserver(team);

        assertDoesNotThrow(() -> hackathon.changeState(new InProgressState()));
        assertInstanceOf(InProgressState.class, hackathon.getState());
    }

    @Test
    @DisplayName("Observer pattern - lista observers vuota dopo rimozione di tutti")
    void removeAllObservers_EmptyList() {
        hackathon.addObserver(judge);
        hackathon.addObserver(mentor);
        hackathon.addObserver(team);

        hackathon.removeObserver(judge);
        hackathon.removeObserver(mentor);
        hackathon.removeObserver(team);

        assertTrue(hackathon.getObservers().isEmpty());
    }

    // =========================================================================
    // Helper class: TestObserver per tracciare le notifiche
    // =========================================================================

    /**
     * Observer di test che traccia se è stato notificato
     * e quale stato ha ricevuto.
     */
    private static class TestObserver implements HackathonObserver {
        private boolean notified = false;
        private HackathonState lastState = null;

        @Override
        public void update(HackathonState newState) {
            this.notified = true;
            this.lastState = newState;
        }

        public boolean wasNotified() { return notified; }
        public HackathonState getLastState() { return lastState; }
    }
}