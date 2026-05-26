package unicam.hackhub.model;

public class RegistrationState implements HackathonState {

    @Override
    public void onRegisterTeam() {
        // Team registration is allowed in this state.
    }

    @Override
    public void onUpload() {
        throw new IllegalStateException("Submission upload is not allowed during registration");
    }

}