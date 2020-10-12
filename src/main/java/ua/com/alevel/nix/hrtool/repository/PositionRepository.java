package ua.com.alevel.nix.hrtool.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.alevel.nix.hrtool.model.position.Position;

public interface PositionRepository extends JpaRepository<Position, Long> {
}
