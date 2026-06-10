package unicam.hackhub.repository;

import unicam.hackhub.model.Judge;
import unicam.hackhub.model.Mentor;
import unicam.hackhub.model.StaffMember;
import unicam.hackhub.model.User;

import java.util.List;

public interface StaffMemberRepository {

    public List<Mentor> getMentor(Long mentorID);

    public Judge getJudge(Long judgeID);

    public StaffMember findByID(Long staffMemberID);

    public StaffMember save(StaffMember entity);

    public void saveAll(List<StaffMember> entities);

    public StaffMember getStaff(User user);
}
