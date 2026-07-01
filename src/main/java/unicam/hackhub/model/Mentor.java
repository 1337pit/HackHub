package unicam.hackhub.model;

import unicam.hackhub.model.observer.HackathonObserver;
import unicam.hackhub.model.state.InProgressState;

public class Mentor implements StaffMember, HackathonObserver {

    private Long id;
    private String name;
    private String email;
    private Hackathon hackathon;

    public Mentor() {}

    public Mentor(Long id, String name, String email, Hackathon hackathon) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.hackathon = hackathon;
    }

    /**
     * Notifica il Mentore del cambio di stato dell'Hackathon.
     * Il Mentore è interessato alla transizione a InProgressState.
     */
    @Override
    public void update(HackathonState newState) {
        if (newState instanceof InProgressState) {
            System.out.println("Mentor [" + name + "] notificato: hackathon iniziato, ora è possibile offrire supporto");
        }
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public Hackathon getHackathon() { return hackathon; }

    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setHackathon(Hackathon hackathon) { this.hackathon = hackathon; }

}