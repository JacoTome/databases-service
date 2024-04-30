package musico.services.databases.repositories;

import musico.services.databases.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Users, Integer> {
    Users findFirstByUsername(String username);
    Users findFirstByEmail(String email);
    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);
}
