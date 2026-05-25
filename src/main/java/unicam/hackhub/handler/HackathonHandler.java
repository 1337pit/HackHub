package unicam.hackhub.handler;

import unicam.hackhub.model.*;
import unicam.hackhub.service.HackathonService;

import java.time.LocalDate;

public class HackathonHandler {

    private final HackathonService hackathonService;

    public HackathonHandler(HackathonService hackathonService) {
        this.hackathonService = hackathonService;
    }

    /**
     * Gestisce la richiesta di creazione hackathon.
     * Corrisponde al metodo createHackathon nel sequence diagram.
     *
     * @param name                  Nome dell'hackathon
     * @param rulebook              Regolamento del team
     * @param registrationDeadline  Deadline per la registrazione di un team all'hackathon
     * @param startDate             Data inizio hackathon
     * @param endDate               Data fine hackathon
     * @param location              Location dell'hackathon
     * @param prize                 Premio dell'hackathon
     * @param state                 Stato dell'hackathon
     * @param maxTeamSize           Numero massimo di team all'interno di un hackathon
     * @param organizer             Organizzatore dell'hackathon
     * @param mentorID              ID del mentore dell'hackathon
     * @param judgeID               ID del giudice dell'hackathon
     * @return L'hackathon creato, o null in caso di errore
     */
    public Hackathon createHackathon(String name, String rulebook, LocalDate registrationDeadline,
                                LocalDate startDate, LocalDate endDate, String location,
                                String prize, HackathonState state, int maxTeamSize, Organizer organizer,
                                Long mentorID, Long judgeID) {
        try {
            return hackathonService.createHackathon(name, rulebook, registrationDeadline, startDate,
                                 endDate, location, prize, state, maxTeamSize, organizer, mentorID, judgeID);
        } catch (IllegalArgumentException e) {
            System.err.println("createHackathon error: " + e.getMessage());
            return null;
        }
    }

    public void addMentor(Long mentorID) {
        try {
            hackathonService.addMentor(mentorID);
        } catch (IllegalArgumentException e) {
            System.err.println("addMentor error: " + e.getMessage());
        }
    }

    public void declareWinner(Team team) {
        try {
            hackathonService.declareWinner(team);
        } catch (IllegalArgumentException e) {
            System.err.println("declareWinner error: " + e.getMessage());
        }
    }

    public void changeState(HackathonState state) {
        try {
            hackathonService.changeState(state);
        } catch (IllegalArgumentException e) {
            System.err.println("changeState error: " + e.getMessage());
        }
    }
}
