package unicam.hackhub.repository;

import unicam.hackhub.model.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StaffMemberRepositoryImplementation implements StaffMemberRepository {

    private final Set<StaffMember> staffMembers = new HashSet<StaffMember>();

    @Override
    public List<Mentor> getMentor(Long mentorID) {
        for (StaffMember s : staffMembers) {
            if (s instanceof Mentor && s.getId().equals(mentorID)) {
                return List.of((Mentor) s);
            }
        }
        return null;
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

    @Override
    public StaffMember getStaff(User user) {
        return staffMembers.stream()
                .filter(s -> s instanceof Mentor || s instanceof Judge)
                .filter(s -> s.getId().equals(user.getId()))
                .findFirst()
                .orElse(null);
    }

}
