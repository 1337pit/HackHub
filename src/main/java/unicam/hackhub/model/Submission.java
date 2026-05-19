package unicam.hackhub.model;

import java.time.LocalDate;

public class Submission {

    private Long id;
    private LocalDate submissionOnDate;
    private String name;
    private Evaluation grade;

    public Submission() {

    }

    public Submission(Long id, String name) {
        this.id = id;
        this.name = name;
        this.submissionOnDate = LocalDate.now();
    }

//    public void updateContent(String content) {
//        this.content = content;
//        this.submissionOnDate = LocalDate.now();
//    }

    public Long getId() {
        return id;
    }

    public LocalDate getSubmissionOnDate() {
        return submissionOnDate;
    }

    public String getName() {
        return name;
    }

    public Evaluation getGrade() {
        return grade;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setSubmissionOnDate(LocalDate submissionOnDate) {
        this.submissionOnDate = submissionOnDate;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGrade(Evaluation grade) {
        this.grade = grade;
    }

}
