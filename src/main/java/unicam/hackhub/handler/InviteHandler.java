package unicam.hackhub.handler;

import unicam.hackhub.model.Invite;
import unicam.hackhub.model.Report;
import unicam.hackhub.model.User;
import unicam.hackhub.service.InviteService;

import java.util.List;

public class InviteHandler {

    private final InviteService inviteService;

    public InviteHandler(InviteService inviteService) {
        this.inviteService = inviteService;
    }

    /**
     * Gestisce la richiesta di creazione invito
     * Usato nel caso d'uso "Invita al Team" del Membro del Team
     * @param teamID        ID del team
     * @param invitedUser   Utente da invitare nel team
     * @return L'invito creato
     */
    public Invite createInvite(Long teamID, User invitedUser){
        try {
            return inviteService.createInvite(teamID, invitedUser);
        } catch (IllegalArgumentException e) {
            System.err.println("createInvite error: " + e.getMessage());
            return null;
        }
    }

    /**
     * Gestiche la richiesta di accettazione invito
     * Usato nel caso d'uso "Risponde all'Invito" dell'Utente
     * @param inviteID  Id dell'invito accettato
     */
    public void acceptInvite(Long inviteID){
        try {
            inviteService.acceptInvite(inviteID);
        }  catch (IllegalArgumentException e) {
            System.err.println("acceptInvite error: " + e.getMessage());
        }
    }

    /**
     * Gestiche la richiesta di rifiuto invito
     * Usato nel caso d'uso "Risponde all'Invito" dell'Utente
     * @param inviteID  Id dell'invito rifiutato
     */
    public void declineInvite(Long inviteID){
        try {
            inviteService.declineInvite(inviteID);
        }   catch (IllegalArgumentException e) {
            System.err.println("declineInvite error: " + e.getMessage());
        }
    }

}
