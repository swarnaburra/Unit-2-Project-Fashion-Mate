package fashionmate_backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import fashionmate_backend.models.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    User findByEmailAndPassword(String email, String password);
}
