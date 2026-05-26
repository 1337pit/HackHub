package unicam.hackhub.model;

public class ConcludedState implements HackathonState {

    @Override
    public void onRegisterTeam() {
        throw new IllegalStateException("Team registration is not allowed after hackathon conclusion");
    }

    @Override
    public void onUpload() {
        throw new IllegalStateException("Submission upload is not allowed after hackathon conclusion");
    }

}