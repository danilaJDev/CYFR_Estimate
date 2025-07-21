package ae.cyfr.estimateapp.repository;

import java.util.List;
import ae.cyfr.estimateapp.model.Work;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkRepository extends JpaRepository<Work, Long> {

    // Получить все работы в конкретном разделе
    List<Work> findBySectionId(Long sectionId);
}
