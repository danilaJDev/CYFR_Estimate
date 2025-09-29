package ae.cyfr.estimateapp.repository;

import ae.cyfr.estimateapp.model.EstimateGroup;
import ae.cyfr.estimateapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EstimateGroupRepository extends JpaRepository<EstimateGroup, Long> {
    List<EstimateGroup> findByUser(User user);
}