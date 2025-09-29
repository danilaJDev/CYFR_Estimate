package ae.cyfr.estimateapp.repository;

import ae.cyfr.estimateapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ae.cyfr.estimateapp.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    @Query("SELECT u FROM User u JOIN FETCH u.estimates WHERE u.role = :role")
    List<User> findByRoleWithEstimates(Role role);

    List<User> findByRole(Role role);
}