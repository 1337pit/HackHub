package unicam.hackhub.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import unicam.hackhub.model.observer.HackathonObservable;
import unicam.hackhub.model.observer.HackathonObserver;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "hackathons")
public class Hackathon implements HackathonObservable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nameHackathon;

    @Column(nullable = false)
    private String rulebook;

    @Column(nullable = false)
    private LocalDate registrationDeadline;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column
    private String location;

    @Column
    private String prize;

    @Column(nullable = false)
    private String stateName;

    @Transient
    private HackathonState state;

    @Column(nullable = false)
    private int maxTeamSize;

    @ManyToOne
    @JoinColumn(name = "organizer_id")
    private Organizer organizer;

    @OneToOne
    @JoinColumn(name = "judge_id")
    private Judge judge;

    @OneToMany(mappedBy = "hackathon", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Mentor> listMentors = new ArrayList<>();

    @OneToMany(mappedBy = "hackathon", cascade = CascadeType.ALL)
    @JsonManagedReference("hackathon-registrations")
    private List<Registration> registrations = new ArrayList<>();

    @Transient
    private List<HackathonObserver> observers = new ArrayList<>();

    private Long WinnerTeam;

    public Hackathon() {}

    public Hackathon(Long id, String nameHackathon) {
        this.id = id;
        this.nameHackathon = nameHackathon;
    }

    public Hackathon(String name, String rulebook, LocalDate registrationDeadline,
                     LocalDate startDate, LocalDate endDate, String location, String prize,
                     HackathonState state, int maxTeamSize, Organizer organizer,
                     Judge judge, List<Mentor> listMentors) {
        this.nameHackathon = name;
        this.rulebook = rulebook;
        this.registrationDeadline = registrationDeadline;
        this.startDate = startDate;
        this.endDate = endDate;
        this.location = location;
        this.prize = prize;
        this.state = state;
        this.stateName = state != null ? state.getClass().getSimpleName() : null;
        this.maxTeamSize = maxTeamSize;
        this.organizer = organizer;
        this.judge = judge;
        this.listMentors = listMentors != null ? new ArrayList<>(listMentors) : new ArrayList<>();
    }

    @Override
    public void addObserver(HackathonObserver observer) {
        if (observer == null) throw new IllegalArgumentException("Observer cannot be null");
        if (!observers.contains(observer)) observers.add(observer);
    }

    @Override
    public void removeObserver(HackathonObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for (HackathonObserver observer : observers)
            observer.update(this.state);
    }

    public void changeState(HackathonState newState) {
        if (newState == null) throw new IllegalArgumentException("New state cannot be null");
        this.state = newState;
        this.stateName = newState.getClass().getSimpleName();
        notifyObservers();
    }

    public boolean isRegistrationOpen() {
        if (registrationDeadline == null) return false;
        return !LocalDate.now().isAfter(registrationDeadline);
    }

    public void addMentor(Mentor mentor) {
        if (mentor == null) throw new NullPointerException("mentor cannot be null");
        this.listMentors.add(mentor);
    }
}