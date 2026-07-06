package unicam.hackhub.service;

import org.springframework.stereotype.Service;
import unicam.hackhub.model.Invite;
import unicam.hackhub.model.Team;
import unicam.hackhub.model.User;
import unicam.hackhub.model.enums.InviteState;
import unicam.hackhub.repository.InviteRepository;
import unicam.hackhub.repository.TeamRepository;
import unicam.hackhub.repository.UserRepository;

import java.util.List;
import java.util.Objects;

@Service
public class InviteService {

    private final InviteRepository inviteRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    public InviteService(InviteRepository inviteRepository,
                         TeamRepository teamRepository,
                         UserRepository userRepository) {
        this.inviteRepository = inviteRepository;
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
    }

    /**
     * Crea e salva un nuovo invito per un utente verso un team.
     */
    public Invite createInvite(Long teamID, User invitedUser) {
        Team team = teamRepository.findById(teamID)
                .orElseThrow(() -> new IllegalArgumentException("Team not found"));

        invitedUser = userRepository.findById(invitedUser.getId())
                .orElseThrow(() -> new IllegalArgumentException("User not registered"));

        Invite invite = new Invite(null, invitedUser, team);
        return inviteRepository.save(invite);
    }

    /**
     * Accetta un invito: aggiorna lo stato e aggiunge l'utente al team.
     */
    public void acceptInvite(Long inviteID) {
        Invite invite = inviteRepository.findById(inviteID)
                .orElseThrow(() -> new IllegalArgumentException("Invite not found"));

        if (invite.getStatus() != InviteState.PENDING)
            throw new IllegalArgumentException("Invite already processed");

        invite.setStatus(InviteState.ACCEPTED);
        User user = invite.getInvitedUser();
        Team team = invite.getTeam();
        if (Objects.equals(user.getCurrentTeam(), team))
            throw new IllegalArgumentException("User not found");

        team.getMembers().add(user);
        user.setCurrentTeam(team);

        inviteRepository.save(invite);
    }

    /**
     * Rifiuta un invito: aggiorna solo lo stato.
     */
    public void declineInvite(Long inviteID) {
        Invite invite = inviteRepository.findById(inviteID)
                .orElseThrow(() -> new IllegalArgumentException("Invite not found"));

        if (invite.getStatus() != InviteState.PENDING)
            throw new IllegalArgumentException("Invite already processed");

        invite.setStatus(InviteState.REFUSED);
        inviteRepository.save(invite);
    }

    public List<Invite> getInvitesByUser(User user) {
        return inviteRepository.findByInvitedUser(user);
    }
}