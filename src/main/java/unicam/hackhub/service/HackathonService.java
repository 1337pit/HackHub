package unicam.hackhub.service;

import unicam.hackhub.model.*;
import unicam.hackhub.repository.HackathonRepository;
import unicam.hackhub.repository.StaffMemberRepository;

import java.time.LocalDate;

public class HackathonService {

    private HackathonRepository hackathonRepository;
    private StaffMemberRepository staffMemberRepository;

    public HackathonService(HackathonRepository hackathonRepository) {
        this.hackathonRepository = hackathonRepository;
    }

    public HackathonService(HackathonRepository hackathonRepository, StaffMemberRepository staffMemberRepository) {
        this.hackathonRepository = hackathonRepository;
        this.staffMemberRepository = staffMemberRepository;
    }

    /**
     * Crea un hackathon seguendo il flusso del sequence diagram:
     * 1. Verifica che il nome non sia già usato
     * 2. Verifica che i parametri passati siano corretti
     * 3. Prende mentore e giudice
     * 4. Crea e salva l'hackathon
     */
    public Hackathon createHackathon(String name, String rulebook, LocalDate registrationDeadline,
                                     LocalDate startDate, LocalDate endDate, String location,
                                     String prize, HackathonState state, int maxTeamSize, Organizer organizer,
                                     Long mentorID, Long judgeID){

        // 1. Verifica che i parametri passati siano corretti
        validateDates(name, rulebook, registrationDeadline, startDate, endDate, location, prize,
                state, maxTeamSize, organizer, mentorID, judgeID);

        // 2. Verifica che il nome dell'hackathon non sia già in uso
        existsHackathonByName(name);

        // 3. Prende il mentore
        Mentor[] mentor = staffMemberRepository.getMentor(mentorID);

        // 4. Prende il giudice
        Judge judge = staffMemberRepository.getJudge(judgeID);

        if(mentor == null || mentor.length == 0 || judge == null){
            throw new IllegalArgumentException("Staff member not found");
        }

        // 5. Crea l'hackathon
        Hackathon hackathon = new Hackathon(name, rulebook, registrationDeadline, startDate, endDate,
                                            location, prize, state, maxTeamSize, organizer, judge, mentor);

        // 6. Salva l'hackathon
        hackathonRepository.save(hackathon);

        return hackathon;
    }

    public void addMentor(Long mentorID) {

    }

    public void declareWinner(Team team){

    }

    public void changeState(HackathonState state) {
        state.onUpload();
    }

    public void existsHackathonByName(String name) {
        Hackathon hackathon = hackathonRepository.findByName(name);
        if(hackathon != null){
            throw new IllegalArgumentException("Name already used");
        }
    }

    public void validateDates(String name, String rulebook, LocalDate registrationDeadline,
                              LocalDate startDate, LocalDate endDate, String location,
                              String prize, HackathonState state, int maxTeamSize, Organizer organizer,
                              Long mentorID, Long judgeID){
        if(name == null || rulebook == null || registrationDeadline == null || startDate == null
                || endDate == null || location == null || prize == null || state == null
                || maxTeamSize <= 0 || organizer == null || mentorID == null || judgeID == null){
            throw new IllegalArgumentException("Wrong dates selected");
        }

        if(registrationDeadline.isAfter(startDate)){
            throw new IllegalArgumentException("Registration deadline cannot be after start date");
        }

        if(startDate.isAfter(endDate)){
            throw new IllegalArgumentException("Start date cannot be after end date");
        }

        if(staffMemberRepository == null){
            throw new IllegalArgumentException("Staff member repository is not defined");
        }

    }

    public void checkHackathonAvailability(Hackathon hackathon) {
        if(!(hackathon.isRegistrationOpen())) {
            throw new IllegalArgumentException("Hackathon is not open");
        }
    }

    public void checkTeamSize(Team team, Hackathon hackathon) {
        if(team.getSize() > hackathon.getMaxTeamSize()) {
            throw new IllegalArgumentException("Team size exceeds limit");
        }
    }

    public void checkTeamAlreadyRegistered(Registration registration){
        if (registration.exists()) {
            throw new IllegalArgumentException("Team already registered");
        }
    }

}
