package unicam.hackhub.model;

public class Evaluation {

    private Long id;
    private String briefJudgement;
    private int grade;

    public Evaluation() {

    }

    public Evaluation(String briefJudgement, int grade) {
        this.briefJudgement = briefJudgement;
        this.grade = grade;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {this.id = id;}

    public String getBriefJudgement() {
        return briefJudgement;
    }

    public int getGrade() {
        return grade;
    }

    public void setBriefJudgement(String briefJudgement) {
        this.briefJudgement = briefJudgement;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }


}