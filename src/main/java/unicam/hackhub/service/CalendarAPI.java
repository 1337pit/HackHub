package unicam.hackhub.service;

import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class CalendarAPI {

    // Mantiene in memoria i giorni in cui il mentore è già occupato
    private final Set<LocalDate> busyDates = new HashSet<>();
    // Mantiene traccia degli ID degli eventi generati per consentirne la cancellazione
    private final Set<String> activeEventIds = new HashSet<>();

    /**
     * Calcola le date libere simulando un controllo in-memory.
     */
    public List<LocalDate> getFreeDates(LocalDate from, LocalDate to) {
        List<LocalDate> freeDates = new ArrayList<>();
        LocalDate current = from;

        while (!current.isAfter(to)) {
            if (!busyDates.contains(current)) {
                freeDates.add(current);
            }
            current = current.plusDays(1);
        }
        return freeDates;
    }

    /**
     * Simula la creazione di un evento generando un ID univoco.
     */
    public String createEvent(LocalDate date) {
        // Se il giorno è già occupato, non permette la prenotazione
        if (busyDates.contains(date)) {
            throw new IllegalArgumentException("Date already busy in fake calendar");
        }

        String fakeEventID = "fake-" + UUID.randomUUID().toString();
        busyDates.add(date);
        activeEventIds.add(fakeEventID);
        return fakeEventID;
    }

    /**
     * Libera la data associata all'evento simulato (passando la data stessa).
     * Nota: Per semplicità di cancellazione in questo mock eliminiamo direttamente
     * la data o l'eventID dalla memoria.
     */
    public void deleteEvent(String googleEventID, LocalDate date) {
        activeEventIds.remove(googleEventID);
        busyDates.remove(date); // Rende la data nuovamente disponibile
    }
}