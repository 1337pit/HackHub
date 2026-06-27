package unicam.hackhub.model;

import unicam.hackhub.model.observer.*;
import unicam.hackhub.model.state.*;

import java.util.List;

public class Team implements HackathonObserver {

    private Long id;
    private String teamName;
    private Submission submission;
    private Hackathon hackathon;
    private List<User> members;

    public Team() {

    }

    public Team(Long id, String teamName, List<User> members) {
        this.id = id;
        this.teamName = teamName;
        this.members = members;
    }

    public Invite createInvite(Long userID) {
        return new Invite();
    }

    public void deleteInvite(Long inviteID) {
        // TODO
    }

    /**
     * Notifica il Team del cambio di stato dell'Hackathon.
     * Il Team è interessato alla transizione a InProgressState
     * (può iniziare a caricare submission) e a ConcludedState.
     */
    @Override
    public void update(HackathonState newState) {
        if (newState instanceof InProgressState) {
            System.out.println("Team [" + teamName + "] notificato: Ora si possono aggiungere sottomissioni");
        } else if (newState instanceof EvaluationState) {
            System.out.println("Team [" + teamName + "] notificato: In attesa della valutazione...");
        } else if (newState instanceof ConcludedState) {
            System.out.println("Team [" + teamName + "] notified: Hackathon concluso, è possibile visualizzare i risultati");
        }
    }

    public int getSize() {
        if (members == null) {
            return 0;
        }

        return members.size();
    }

    public Long getId() {
        return id;
    }

    public String getTeamName() {
        return teamName;
    }

    public List<User> getMembers() {
        return members;
    }

    public Submission getSubmission() {
        return submission;
    }

    public Hackathon getHackathon() {
        return hackathon;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public void setMembers(List<User> members) {
        this.members = members;
    }

    public void setSubmission(Submission submission) {
        this.submission = submission;
    }

    public void setHackathon(Hackathon hackathon) {
        this.hackathon = hackathon;
    }

}