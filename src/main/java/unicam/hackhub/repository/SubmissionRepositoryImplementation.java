package unicam.hackhub.repository;

import unicam.hackhub.model.Submission;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SubmissionRepositoryImplementation implements SubmissionRepository {

    private final Set<Submission> submissions = new HashSet<Submission>();

    @Override
    public Submission findByID(Long submissionID) {
        return submissions.stream()
                .filter(s -> s.getId().equals(submissionID))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Submission save(Submission submission) {
        submissions.add(submission);
        return submission;
    }

    @Override
    public void saveAll(List<Submission> entities) {
        for(Submission submission : entities){
            submissions.add(submission);
        }
        System.out.println("Submissions saved");
    }

}
