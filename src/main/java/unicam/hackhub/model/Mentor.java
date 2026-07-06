package unicam.hackhub.model;

import jakarta.persistence.*;
import unicam.hackhub.model.observer.HackathonObserver;
import unicam.hackhub.model.state.InProgressState;

@Entity
@Table(name = "mentors")
public class Mentor extends StaffMember implements HackathonObserver {

    public Mentor() {}

    public Mentor(Long id, String name, String email, Hackathon hackathon) {
        super(id, name);
        this.email = email;
        this.hackathon = hackathon;
    }

    /**
     * Notifica il Mentore del cambio di stato dell'Hackathon.
     * Il Mentore è interessato alla transizione a InProgressState.
     */
    @Override
    public void update(HackathonState newState) {
        if (newState instanceof InProgressState)
            System.out.println("Mentor [" + name + "] notificato: hackathon iniziato, ora è possibile offrire supporto");
    }

}