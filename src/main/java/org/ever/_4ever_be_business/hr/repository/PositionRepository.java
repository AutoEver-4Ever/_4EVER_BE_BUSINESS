package org.ever._4ever_be_business.hr.repository;

import org.ever._4ever_be_business.hr.entity.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Position Repository
 */
@Repository
public interface PositionRepository extends JpaRepository<Position, String>, PositionRepositoryCustom {
}
