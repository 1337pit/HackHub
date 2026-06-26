package unicam.hackhub.model;

import unicam.hackhub.model.observer.HackathonObserver;
import unicam.hackhub.model.state.InProgressState;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Mentor implements StaffMember, HackathonObserver {

    private Long id;
    private String name;
    private String email;
    private List<Slot> listSlots = new ArrayList<>();
    private Hackathon hackathon;

    public Mentor(Long id, String name, String email, List<Slot> listSlots, Hackathon hackathon) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.listSlots = listSlots;
        this.hackathon = hackathon;
    }

//    public Mentor(Long id, String name, String email, Hackathon hackathon) {
//        this.id = id;
//        this.name = name;
//        this.email = email;
//        this.hackathon = hackathon;
//    }

    /**
     * Notifica il Mentore del cambio di stato dell'Hackathon.
     * Il Mentore è interessato alla transizione a InProgressState.
     */
    @Override
    public void update(HackathonState newState) {
        if (newState instanceof InProgressState) {
            System.out.println("Mentor [" + name + "] notified: hackathon started. You can now support teams.");
        }
    }

    public void proposeCall() {
        // TODO
    }

    public void reportTeam(Team team, String description) {
        // TODO
    }

    public List<Slot> getSlotsAvailable(List<Slot> ListSlots) {
        List<Slot> slotsAvailable = new ArrayList<>();

        for (Slot s : this.listSlots) {
            if (s.isAvailable()) {
                slotsAvailable.add(s);
            }
        }
        return slotsAvailable;
    }

    public Slot selectSlot(List<Slot> listSlotsAvailable) {

        // Mostro la lista al membro dello staff
        System.out.println("Seleziona uno dei seguenti slot: ");
        for (Slot s : listSlotsAvailable) {
            System.out.println(s);
        }

        // Il membro dello staff sceglie lo slot
        Slot chosenSlot = null;
        Scanner scanner = new Scanner(System.in);
        while (chosenSlot == null) {
            System.out.print("\nInserisci il numero dello slot che desideri scegliere: ");

            if (scanner.hasNextInt()) {
                int choice = scanner.nextInt();
                for (Slot s : listSlotsAvailable) {
                    if (s.getId() == choice)
                        return chosenSlot = s;
                }

            } else {
                System.out.println("ID non valido. Per favore, inserisci un numero valido.");
                scanner.next(); // Pulisce il buffer dello scanner da input errati
            }
        }
        return null;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {return email;}

    public List<Slot> getListSlots() {return listSlots;}

    public Hackathon getHackathon() {
        return hackathon;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {this.email = email;}

    public void setSlot(Slot slot) {
        if (slot != null)
            this.listSlots.add(slot);
    }

    public void setHackathon(Hackathon hackathon) {
        this.hackathon = hackathon;
    }

    public static class Slot {

        private Long id;
        private LocalDate startSlot;
        private LocalDate endSlot;
        private boolean available;

        public Slot(Long id, LocalDate startSlot, LocalDate endSlot, boolean available) {
            this.id = id;
            this.startSlot = startSlot;
            this.endSlot = endSlot;
            this.available = available;
        }

        public boolean isAvailable() {
            return available;
        }

        public void setAvailable(boolean available) {
            this.available = available;
        }

        public Long getId() {
            return id;
        }
        public void setId(Long id) {
            this.id = id;
        }

        public LocalDate getStartSlot() {
            return startSlot;
        }
        public void setStartSlot(LocalDate startSlot) {
            this.startSlot = startSlot;
        }

        public LocalDate getEndSlot() {
            return endSlot;
        }
        public void setEndSlot(LocalDate endSlot) {
            this.endSlot = endSlot;
        }
    }

}