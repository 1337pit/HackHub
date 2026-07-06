package unicam.hackhub.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import unicam.hackhub.model.Registration;
import unicam.hackhub.model.Team;
import unicam.hackhub.model.User;
import unicam.hackhub.service.RegistrationService;
import unicam.hackhub.service.UserService;

@RestController
@RequestMapping("/api/registrations")
public class RegistrationHandler {

    private final RegistrationService registrationService;
    private final UserService userService;

    public RegistrationHandler(RegistrationService registrationService, UserService userService) {
        this.registrationService = registrationService;
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<Registration> registerTeam(@RequestParam Long hackathonId, @RequestParam Long userId) {
        try {
            Registration registration = registrationService.registerTeam(hackathonId, userId);
            return new ResponseEntity<>(registration, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/team/{teamId}")
    public ResponseEntity<Registration> getRegistration(@PathVariable Long teamId) {
        Registration registration = registrationService.getRegistration(teamId);
        if (registration == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(registration);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Team> getTeamByUser(@PathVariable Long userId) {
        User user = userService.getUser(userId);

        Team team = registrationService.getTeamByUser(user);
        if (team == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(team);
    }
}