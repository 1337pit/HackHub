package unicam.hackhub.repository;

import unicam.hackhub.model.Report;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ReportRepositoryImplementation implements ReportRepository {

    private final Set<Report> reports = new HashSet<>();

    @Override
    public Report findByID(Long reportID) {
        return reports.stream()
                .filter(r -> r.getId().equals(reportID))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Report> findAllReportByHackathon(Long hackathonID) {
        return reports.stream()
                .filter(r -> r.getHackathon().getId().equals(hackathonID))
                .collect(Collectors.toList());
    }

    @Override
    public Report save(Report entity) {
        reports.removeIf(r -> r.getId().equals(entity.getId()));
        reports.add(entity);
        return entity;
    }

    @Override
    public void saveAll(List<Report> entities) {
        entities.forEach(this::save);
    }
}
