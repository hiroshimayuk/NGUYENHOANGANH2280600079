package Nhom6.NGUYENHOANGANH2280600079.repositories;

import Nhom6.NGUYENHOANGANH2280600079.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface IUserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}