package unicam.hackhub.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "staff_members")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class StaffMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(nullable = false)
    protected String name;

    @Column
    protected String email;

    @ManyToOne
    @JoinColumn(name = "hackathon_id")
    @JsonBackReference
    protected Hackathon hackathon;

    public StaffMember() {}

    public StaffMember(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}