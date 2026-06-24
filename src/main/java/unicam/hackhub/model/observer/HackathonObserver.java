package unicam.hackhub.model.observer;

import unicam.hackhub.model.HackathonState;

public interface HackathonObserver {

    /**
     * Viene chiamato quando lo stato dell'Hackathon cambia.
     * @param newState il nuovo stato dell'Hackathon
     */
    void update(HackathonState newState);

}