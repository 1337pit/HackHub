package unicam.hackhub.service;

import unicam.hackhub.model.Invite;
import unicam.hackhub.model.enums.InviteState;
import unicam.hackhub.model.Team;
import unicam.hackhub.model.User;
import unicam.hackhub.repository.InviteRepository;
import unicam.hackhub.repository.TeamRepository;
import unicam.hackhub.repository.UserRepository;

import java.util.List;

public class InviteService {

    private final InviteRepository inviteRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    private static long idCounter = 1;

    public InviteService(InviteRepository inviteRepository,
                         TeamRepository teamRepository,
                         UserRepository userRepository) {
        this.inviteRepository = inviteRepository;
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
    }

    /**
     * Crea e salva un nuovo invito per un utente verso un team.
     * Usato durante createTeam per invitare i membri iniziali.
     */
    public Invite createInvite(Long teamID, User invitedUser) {
        Team team = teamRepository.findByID(teamID);
        if (team == null)
            throw new IllegalArgumentException("Team not found");

        if (!userRepository.findByName(invitedUser.getName()).equals(invitedUser))
            throw new IllegalArgumentException("User not registered");

        Invite invite = new Invite(idCounter++, invitedUser, team);
        inviteRepository.save(invite);
        return invite;
    }

    /**
     * Accetta un invito: aggiorna lo stato e aggiunge l'utente al team.
     */
    public void acceptInvite(Long inviteID) {
        Invite invite = inviteRepository.findByID(inviteID);
        if (invite == null)
            throw new IllegalArgumentException("Invite not found");

        invite.setStatus(InviteState.ACCEPTED);
        User user = invite.getInvitedUser();
        Team team = invite.getTeam();

        team.getMembers().add(user);
        user.setCurrentTeam(team);

        inviteRepository.save(invite);
    }

    /**
     * Rifiuta un invito: aggiorna solo lo stato.
     */
    public void declineInvite(Long inviteID) {
        Invite invite = inviteRepository.findByID(inviteID);
        if (invite == null)
            throw new IllegalArgumentException("Invite not found");

        invite.setStatus(InviteState.REFUSED);
        inviteRepository.save(invite);
    }

    public List<Invite> getInvitesByUser(User user) {
        return inviteRepository.findByUser(user);
    }
}