package unicam.hackhub.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import unicam.hackhub.model.observer.HackathonObserver;
import unicam.hackhub.model.state.EvaluationState;

@Setter
@Getter
@Entity
@Table(name = "judges")
public class Judge extends StaffMember implements HackathonObserver {

    public Judge() {}

    public Judge(Long id, String name) {
        super(id, name);
    }

    @Override
    public void update(HackathonState newState) {
        if (newState instanceof EvaluationState)
            System.out.println("Judge [" + name + "] notified: valuation phase started.");
    }
}