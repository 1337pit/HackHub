package unicam.hackhub.repository;

import unicam.hackhub.model.Hackathon;
import unicam.hackhub.model.Judge;
import unicam.hackhub.model.Mentor;
import unicam.hackhub.model.StaffMember;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StaffMemberRepositoryImplementation implements StaffMemberRepository {

    private final Set<StaffMember> staffMembers = new HashSet<StaffMember>();

    public Mentor[] getMentor(Long mentorID) {
        return staffMembers.stream()
                .filter(s -> s instanceof Mentor)
                .map(s -> (Mentor) s)
                .filter(m -> m.getId().equals(mentorID))
                .findFirst()
                .map(mentor -> new Mentor[]{mentor})
                .orElse(null);
    }

    @Override
    public Judge getJudge(Long judgeID) {
        return staffMembers.stream()
                .filter(s -> s instanceof Judge)
                .map(s -> (Judge) s)
                .filter(j -> j.getId().equals(judgeID))
                .findFirst()
                .orElse(null);
    }

    @Override
    public StaffMember findByID(Long staffMemberID) {
        return staffMembers.stream()
                .filter(s -> s.getId().equals(staffMemberID))
                .findFirst()
                .orElse(null);
    }

    @Override
    public StaffMember save(StaffMember staffMember) {
        staffMembers.add(staffMember);
        return staffMember;
    }

    @Override
    public void saveAll(List<StaffMember> entities) {
        for(StaffMember staffMember : entities){
            staffMembers.add(staffMember);
        }
        System.out.println("Staff members saved");
    }

}
