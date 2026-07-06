package unicam.hackhub.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import unicam.hackhub.model.Invite;
import unicam.hackhub.service.InviteService;

@RestController
@RequestMapping("/api/handler/invites")
public class InviteHandler {

    private final InviteService inviteService;

    public InviteHandler(InviteService inviteService) {
        this.inviteService = inviteService;
    }

    @PostMapping("/send")
    public ResponseEntity<Void> sendInvite(@RequestBody Invite invite) {
        if (invite == null || invite.getTeam() == null || invite.getInvitedUser() == null) {
            return ResponseEntity.badRequest().build();
        }

        inviteService.createInvite(invite.getTeam().getId(), invite.getInvitedUser());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/accept")
    public ResponseEntity<Void> acceptInvite(@RequestBody Invite invite) {
        if (invite == null || invite.getId() == null) {
            return ResponseEntity.badRequest().build();
        }
        try {
            inviteService.acceptInvite(invite.getId());
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/decline")
    public ResponseEntity<Void> declineInvite(@RequestBody Invite invite) {
        if (invite == null || invite.getId() == null) {
            return ResponseEntity.badRequest().build();
        }
        try {
            inviteService.declineInvite(invite.getId());
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}