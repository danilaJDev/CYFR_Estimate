package ae.cyfr.estimateapp.repository;

import java.util.List;
import ae.cyfr.estimateapp.model.Subsection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubsectionRepository extends JpaRepository<Subsection, Long> {

    List<Subsection> findAllSubsectionBySectionId(Long sectionId);
}
