package de.resume.inventory.management.system.inventoryservice.repositories;

import de.resume.inventory.management.system.inventoryservice.models.entities.StockEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockRepository extends JpaRepository<StockEntity, String> {
}
