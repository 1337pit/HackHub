package unicam.hackhub.repository;

import unicam.hackhub.model.StaffMember;

import java.util.List;

public interface StaffMemberRepository {

    public void findByID(Long staffMemberID);

    public StaffMember save(StaffMember entity);

    public void saveAll(List<StaffMember> entities);

}
