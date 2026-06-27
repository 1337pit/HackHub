package unicam.hackhub.repository;

import unicam.hackhub.model.Report;

import java.util.List;

public interface ReportRepository {

    Report findByID(Long reportID);

    List<Report> findAllReportByHackathon(Long hackathonID);

    Report save(Report entity);

    void saveAll(List<Report> entities);
}
