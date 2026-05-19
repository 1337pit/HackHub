package unicam.hackhub.repository;

import unicam.hackhub.model.Submission;

import java.util.List;

public interface SubmissionRepository {

    public void findByID(Long submissionID);

    public Submission save(Submission entity);

    public void saveAll(List<Submission> entities);

}
