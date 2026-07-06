package unicam.hackhub.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "organizers")
public class Organizer extends StaffMember {

    public Organizer() {}

    public Organizer(Long id, String name) {
        super(id, name);
    }
}