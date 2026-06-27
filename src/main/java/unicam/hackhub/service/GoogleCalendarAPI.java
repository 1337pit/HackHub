package unicam.hackhub.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import com.google.api.client.json.JsonFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GoogleCalendarAPI {

    private static final String APPLICATION_NAME = "IDS Hackathon Calendar";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES =
            Collections.singletonList(CalendarScopes.CALENDAR);

    private final String credentialsFilePath;

    public GoogleCalendarAPI(String credentialsFilePath) {
        this.credentialsFilePath = credentialsFilePath;
    }

    private Credential getCredentials(NetHttpTransport httpTransport) throws IOException {
        File credentialsFile = new File(credentialsFilePath);
        if (!credentialsFile.exists())
            throw new IOException("credentials.json non trovato in: " + credentialsFilePath);

        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
                JSON_FACTORY, new InputStreamReader(new FileInputStream(credentialsFile)));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    private Calendar getCalendarService() throws IOException, GeneralSecurityException {
        NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        return new Calendar.Builder(httpTransport, JSON_FACTORY, getCredentials(httpTransport))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    /**
     * Restituisce le date LIBERE del mentor nel range indicato.
     * Interroga il calendario Google del mentor e restituisce
     * i giorni senza eventi esistenti.
     */
    public List<LocalDate> getFreeDates(LocalDate from, LocalDate to)
            throws IOException, GeneralSecurityException {

        DateTime timeMin = new DateTime(from.toString() + "T00:00:00+01:00");
        DateTime timeMax = new DateTime(to.toString() + "T23:59:59+01:00");

        Events events = getCalendarService().events().list("primary")
                .setTimeMin(timeMin)
                .setTimeMax(timeMax)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();

        // Costruisce la lista di date occupate
        List<LocalDate> busyDates = new ArrayList<>();
        for (Event event : events.getItems()) {
            String dateStr = event.getStart().getDate() != null
                    ? event.getStart().getDate().toString()
                    : event.getStart().getDateTime().toString().substring(0, 10);
            busyDates.add(LocalDate.parse(dateStr));
        }

        // Restituisce le date libere nel range
        List<LocalDate> freeDates = new ArrayList<>();
        LocalDate current = from;
        while (!current.isAfter(to)) {
            if (!busyDates.contains(current))
                freeDates.add(current);
            current = current.plusDays(1);
        }

        return freeDates;
    }

    /**
     * Crea un evento su Google Calendar e restituisce l'ID dell'evento creato.
     * L'ID viene salvato nella RequestSupport per poter cancellare/modificare
     * l'evento in futuro.
     */
    public String createEvent(LocalDate date, String teamName, String mentorName)
            throws IOException, GeneralSecurityException {

        Event event = new Event()
                .setSummary("Supporto HackHub - " + teamName)
                .setDescription("Call di supporto con il team " + teamName
                        + " e il mentor " + mentorName);

        DateTime start = new DateTime(date.toString() + "T09:00:00+01:00");
        DateTime end   = new DateTime(date.toString() + "T10:00:00+01:00");

        event.setStart(new EventDateTime().setDateTime(start));
        event.setEnd(new EventDateTime().setDateTime(end));

        Event created = getCalendarService()
                .events()
                .insert("primary", event)
                .execute();

        return created.getId(); // salvato in RequestSupport.googleEventID
    }

    /**
     * Cancella un evento precedentemente creato.
     * Utile se il team o il mentor annullano la call.
     */
    public void deleteEvent(String googleEventID)
            throws IOException, GeneralSecurityException {
        getCalendarService().events().delete("primary", googleEventID).execute();
    }
}