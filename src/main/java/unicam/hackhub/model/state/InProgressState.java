package unicam.hackhub.model.state;

import unicam.hackhub.model.HackathonState;

public class InProgressState implements HackathonState {

    @Override
    public void onRegisterTeam() {
        throw new IllegalStateException("Team registration is not allowed while hackathon is in progress");
    }

    @Override
    public void onUpload() {
        // Submission upload is allowed in this state.
    }

}