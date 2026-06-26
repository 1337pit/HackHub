package unicam.hackhub.model;

import unicam.hackhub.model.observer.HackathonObservable;
import unicam.hackhub.model.observer.HackathonObserver;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Hackathon implements HackathonState, HackathonObservable {

    private Long id;
    private String nameHackathon;
    private String rulebook;
    private LocalDate registrationDeadline;
    private LocalDate startDate;
    private LocalDate endDate;
    private String location;
    private String prize;
    private HackathonState state;
    private int maxTeamSize;
    private Organizer organizer;
    private Judge judge;
    private List<Mentor> listMentors = new ArrayList<>();
    private List<HackathonObserver> observers = new ArrayList<>();

    public Hackathon() {

    }

    public Hackathon(Long id, String nameHackathon) {
        this.id = id;
        this.nameHackathon = nameHackathon;
    }

    public Hackathon(String name, String rulebook, LocalDate registrationDeadline, LocalDate startDate,
                     LocalDate endDate, String location, String prize, HackathonState state,
                     int maxTeamSize, Organizer organizer, Judge judge, List<Mentor> listMentors) {
        this.nameHackathon = name;
        this.rulebook = rulebook;
        this.registrationDeadline = registrationDeadline;
        this.startDate = startDate;
        this.endDate = endDate;
        this.location = location;
        this.prize = prize;
        this.state = state;
        this.maxTeamSize = maxTeamSize;
        this.organizer = organizer;
        this.judge = judge;

        if (listMentors == null) {
            this.listMentors = new ArrayList<>();
        } else {
            this.listMentors = new ArrayList<>(listMentors);
        }
    }

    // -------------------------------------------------------------------------
    // HackathonObservable implementation
    // -------------------------------------------------------------------------

    @Override
    public void addObserver(HackathonObserver observer) {
        if (observer == null)
            throw new IllegalArgumentException("Observer cannot be null");
        if (!observers.contains(observer))
            observers.add(observer);
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

    // -------------------------------------------------------------------------
    // HackathonState delegation
    // -------------------------------------------------------------------------

    @Override
    public void onRegisterTeam() {
        if (state == null)
            throw new IllegalStateException("Hackathon state is not defined");
        state.onRegisterTeam();
    }

    @Override
    public void onUpload() {
        if (state == null)
            throw new IllegalStateException("Hackathon state is not defined");
        state.onUpload();
    }

    /**
     * Cambia lo stato dell'hackathon e notifica tutti gli observer.
     * Punto di integrazione tra State pattern e Observer pattern.
     */
    public void changeState(HackathonState newState) {
        if (newState == null)
            throw new IllegalArgumentException("New state cannot be null");
        this.state = newState;
        notifyObservers();
    }

    // -------------------------------------------------------------------------
    // Business logic
    // -------------------------------------------------------------------------

    public boolean isRegistrationOpen() {
        if (registrationDeadline == null)
            return false;
        return !LocalDate.now().isAfter(registrationDeadline);
    }

    public Mentor addMentor(Mentor mentor) {
        if (mentor == null)
            throw new NullPointerException("mentor cannot be null");
        this.listMentors.add(mentor);
        return mentor;
    }

    public Mentor selectMentor(List<Mentor> listMentors) {

        // Mostro la lista al membro dello staff
        System.out.println("Seleziona uno dei seguenti mentori: ");
        for (Mentor m : listMentors) {
            System.out.println(m);
        }

        // Il membro dello staff sceglie il mentore
        Mentor chosenMentor = null;
        Scanner scanner = new Scanner(System.in);
        while (chosenMentor == null) {
            System.out.print("\nInserisci il numero del mentore che desideri scegliere: ");

            if (scanner.hasNextInt()) {
                int choice = scanner.nextInt();
                for (Mentor m : listMentors) {
                    if (m.getId() == choice)
                        return chosenMentor = m;
                }

            } else {
                System.out.println("ID non valido. Per favore, inserisci un numero valido.");
                scanner.next(); // Pulisce il buffer dello scanner da input errati
            }
        }
        return null;
    }

    // -------------------------------------------------------------------------
    // Getters and Setters
    // -------------------------------------------------------------------------

    public Long getId() {
        return id;
    }

    public String getNameHackathon() {
        return nameHackathon;
    }

    public String getRulebook() {
        return rulebook;
    }

    public LocalDate getRegistrationDeadline() {
        return registrationDeadline;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public String getLocation() {
        return location;
    }

    public String getPrize() {
        return prize;
    }

    public HackathonState getState() {
        return state;
    }

    public int getMaxTeamSize() {
        return maxTeamSize;
    }

    public Organizer getOrganizer() {
        return organizer;
    }

    public Judge getJudge() {
        return judge;
    }

    public List<Mentor> getListMentors() {
        return listMentors;
    }

    public List<HackathonObserver> getObservers() {
        return observers;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNameHackathon(String nameHackathon) {
        this.nameHackathon = nameHackathon;
    }

    public void setRulebook(String rulebook) {
        this.rulebook = rulebook;
    }

    public void setRegistrationDeadline(LocalDate registrationDeadline) {
        this.registrationDeadline = registrationDeadline;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setPrize(String prize) {
        this.prize = prize;
    }

    public void setState(HackathonState state) {
        this.state = state;
    }

    public void setMaxTeamSize(int maxTeamSize) {
        this.maxTeamSize = maxTeamSize;
    }

    public void setOrganizer(Organizer organizer) {
        this.organizer = organizer;
    }

    public void setJudge(Judge judge) {
        this.judge = judge;
    }

    public void setMentor(Mentor mentor) {
        if (mentor != null)
            this.listMentors.add(mentor);
    }

}