package unicam.hackhub.service;

import unicam.hackhub.model.*;
import unicam.hackhub.repository.*;

import java.time.LocalDate;
import java.util.List;

public class HackathonService {

    private final HackathonRepository hackathonRepository;
    private final StaffMemberRepository staffMemberRepository;
    private final UserRepository userRepository;
    private final RegistrationRepository registrationRepository;


    public HackathonService(HackathonRepository hackathonRepository,
                            StaffMemberRepository staffMemberRepository,
                            UserRepository userRepository,
                            RegistrationRepository registrationRepository) {
        this.hackathonRepository = hackathonRepository;
        this.staffMemberRepository = staffMemberRepository;
        this.userRepository = userRepository;
        this.registrationRepository = registrationRepository;
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
        List<Mentor> mentor = staffMemberRepository.getMentor(mentorID);

        // 4. Prende il giudice
        Judge judge = staffMemberRepository.getJudge(judgeID);

        if(mentor == null || mentor.isEmpty() || judge == null){
            throw new IllegalArgumentException("Staff member not found");
        }

        // 5. Crea l'hackathon
        Hackathon hackathon = new Hackathon(name, rulebook, registrationDeadline, startDate, endDate,
                                            location, prize, state, maxTeamSize, organizer, judge, mentor);

        // 6. Salva l'hackathon
        hackathonRepository.save(hackathon);

        return hackathon;
    }

    /**
     * Mmodifica un hackathon seguendo il flusso del sequence diagram:
     * 1. Verifica che l'hackathon esiste
     * 2. Verifica che l'hackathon sia nello stato "in corso"
     * 3. Delega la modifica dell'hackathon all'organizzatore
     * 4. Modifica e salva l'hackathon modificato
     */
    public Hackathon editHackathon(Long hackathonID, String name, String rulebook, LocalDate registrationDeadline,
                                   String location, String prize, int maxTeamSize, Judge judge, Mentor mentor) {

        // 1. Verifica che l'hackathon esiste
        Hackathon hackathon = hackathonRepository.findByID(hackathonID);
        if(hackathon == null){
            throw new IllegalArgumentException("Hackathon not found");
        }

        // 2. Verifica che l'hackathon è nello stato "in corso"
        hackathon.isRegistrationOpen();

        // 3. Ritorna l'organizzatore dell'hackathon
        Organizer organizer = hackathon.getOrganizer();

        // 4. Delega la modifica dell'hackathon all'organizzatore
        Hackathon editedHackathon = organizer.editHackathon(name, rulebook, registrationDeadline,
                location, prize, maxTeamSize, judge, mentor);

        // 5. Salva l'hackathon modificato
        hackathonRepository.save(editedHackathon);

        return editedHackathon;
    }

    /**
     * Aggiunge un Mentore seguendo il flusso del sequence diagram:
     * 1. Verifica che l'utente esiste
     * 2. Verifica che l'utente non sia già Mentore o Giudice
     * 3. Associa l'utente come Mentore all'hackathon corrente
     * 4. Salva il Mentore
     */
    public void addMentor(String email, Long hackathonID) {

        // 1. Ritorna l'hackathon con l'hackathonID passato
        Hackathon hackathon = hackathonRepository.findByID(hackathonID);

        // 2. Ritorna l'utente con l'email passata
        User user = userRepository.findByEmail(email);

        // 3. Verifica che l'utente esiste
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        // 4. Verifica che l'utente non sia già Mentore o Giudice
        StaffMember staffMember = staffMemberRepository.getStaff(user);
        if (staffMember instanceof Mentor) {
             throw new IllegalArgumentException("User already Mentor");
        }
        if (staffMember instanceof Judge) {
            throw new IllegalArgumentException("User already Judge");
        }

        // 5. Associa l'utente come Mentore all'hackathon corrente
        Mentor mentor = new Mentor(user.getId(), user.getName(), email, hackathon);
        hackathon.addMentor(mentor);

        // 6. Salva il Mentore
        staffMemberRepository.save(mentor);

        // 7. Mostra notifica di successo
        System.out.println("Mentor added succesfully");
    }

    /**
     * Restituisce gli hackathon assegnati a un membro dello staff.
     * Il membro può essere organizzatore, giudice o mentore.
     */
    public List<Hackathon> getAssignedHackathons(Long staffMemberID) {
        if (staffMemberID == null) {
            throw new IllegalArgumentException("Staff member ID cannot be null");
        }

        StaffMember staffMember = staffMemberRepository.findByID(staffMemberID);

        if (staffMember == null) {
            throw new IllegalArgumentException("Staff member not found");
        }

        return hackathonRepository.findByStaffMember(staffMemberID);
    }

    /**
     * Restituisce i team registrati con i relativi partecipanti
     * per un hackathon assegnato al membro dello staff.
     */
    public List<Registration> getParticipants(Long staffMemberID, Long hackathonID) {
        if (staffMemberID == null || hackathonID == null) {
            throw new IllegalArgumentException("Staff member ID and hackathon ID cannot be null");
        }

        List<Hackathon> assignedHackathons = getAssignedHackathons(staffMemberID);

        boolean assigned = false;

        for (Hackathon hackathon : assignedHackathons) {
            if (hackathon.getId() != null && hackathon.getId().equals(hackathonID)) {
                assigned = true;
                break;
            }
        }

        if (!assigned) {
            throw new IllegalArgumentException("Hackathon not assigned to staff member");
        }

        List<Registration> registrations =
                registrationRepository.findByHackathon(hackathonID);

        if (registrations == null || registrations.isEmpty()) {
            throw new IllegalArgumentException("No participant registered");
        }

        return registrations;
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

    /**
     * Recupera la lista delle sottomissioni per un hackathon.
     * Usato nel caso d'uso "Consulta Elenco Sottomissioni" del Membro Staff.
     * 1. Verifica che l'hackathon esista
     * 2. Recupera le sottomissioni tramite SubmissionRepository
     */
    public List<Submission> getSubmissions(Long staffMemberID, Long hackathonID) {
        if (staffMemberID == null || hackathonID == null)
            throw new IllegalArgumentException("Staff member ID and hackathon ID cannot be null");

        List<Hackathon> assignedHackathons = getAssignedHackathons(staffMemberID);

        boolean assigned = false;

        for (Hackathon hackathon : assignedHackathons) {
            if (hackathon.getId() != null && hackathon.getId().equals(hackathonID)) {
                assigned = true;
                break;
            }
        }

        if (!assigned)
            throw new IllegalArgumentException("Hackathon not assigned to staff member");

        List<Registration> registrations = registrationRepository.findByHackathon(hackathonID);

        if (registrations == null || registrations.isEmpty())
            throw new IllegalArgumentException("No submissions found");

        List<Submission> submissions = registrations.stream()
                .map(r -> r.getTeam().getSubmission())
                .filter(s -> s != null)
                .collect(java.util.stream.Collectors.toList());

        if (submissions.isEmpty())
            throw new IllegalArgumentException("No submissions found");

        return submissions;
    }
}
