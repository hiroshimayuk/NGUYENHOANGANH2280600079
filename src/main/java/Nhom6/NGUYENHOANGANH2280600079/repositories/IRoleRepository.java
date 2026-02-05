package Nhom6.NGUYENHOANGANH2280600079.repositories;

import Nhom6.NGUYENHOANGANH2280600079.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IRoleRepository extends JpaRepository<Role, Long> {
    Role findRoleById(Long id);
}