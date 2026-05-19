package unicam.hackhub.model;

public class User {

    private Long id;
    private String name;
    private Team currentTeam;
    private String email;

    public User() {

    }

    public User(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public Team createTeam() {
        Team team = new Team();
        this.currentTeam = team;
        return team;
    }

    public void acceptInvite(Long inviteID) {
        // TODO
    }

    public void declineInvite(Long inviteID) {
        // TODO
    }

    public boolean hasTeam() {
        return currentTeam != null;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Team getCurrentTeam() {
        return currentTeam;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCurrentTeam(Team currentTeam) {
        this.currentTeam = currentTeam;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}