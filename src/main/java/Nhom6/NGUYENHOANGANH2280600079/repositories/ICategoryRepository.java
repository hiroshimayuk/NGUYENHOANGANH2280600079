package Nhom6.NGUYENHOANGANH2280600079.repositories;

import Nhom6.NGUYENHOANGANH2280600079.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICategoryRepository extends JpaRepository<Category, Long> {
}