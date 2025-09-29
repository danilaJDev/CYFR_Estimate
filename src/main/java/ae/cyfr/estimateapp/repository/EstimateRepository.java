package ae.cyfr.estimateapp.repository;

import ae.cyfr.estimateapp.model.Estimate;
import ae.cyfr.estimateapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EstimateRepository extends JpaRepository<Estimate, Long> {
    List<Estimate> findByUser(User user);
}