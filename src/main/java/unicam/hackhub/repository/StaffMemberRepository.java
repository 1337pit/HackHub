package unicam.hackhub.repository;

import unicam.hackhub.model.Judge;
import unicam.hackhub.model.Mentor;
import unicam.hackhub.model.StaffMember;

import java.lang.reflect.Member;
import java.util.List;

public interface StaffMemberRepository {

    public Mentor[] getMentor(Long mentorID);

    public Judge getJudge(Long judgeID);

    public StaffMember findByID(Long staffMemberID);

    public StaffMember save(StaffMember entity);

    public void saveAll(List<StaffMember> entities);

}
