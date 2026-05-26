package unicam.hackhub.model;

public class EvaluationState implements HackathonState {

    @Override
    public void onRegisterTeam() {
        throw new IllegalStateException("Team registration is not allowed during evaluation");
    }

    @Override
    public void onUpload() {
        throw new IllegalStateException("Submission upload is not allowed during evaluation");
    }

}