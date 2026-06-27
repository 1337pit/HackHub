package unicam.hackhub.repository;

import unicam.hackhub.model.*;

import java.util.List;

public interface StaffMemberRepository {

    public List<Mentor> getMentor(Long mentorID);

    public Judge getJudge(Long judgeID);

    public Organizer getOrganizer(Long organizerID);

    public StaffMember findByID(Long staffMemberID);

    public StaffMember save(StaffMember entity);

    public void saveAll(List<StaffMember> entities);

    public StaffMember getStaff(User user);
}
