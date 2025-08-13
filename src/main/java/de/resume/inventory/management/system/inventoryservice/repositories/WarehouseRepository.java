package de.resume.inventory.management.system.inventoryservice.repositories;

import de.resume.inventory.management.system.inventoryservice.models.entities.WarehouseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WarehouseRepository extends JpaRepository<WarehouseEntity, String>  {
    Optional<WarehouseEntity> findByName(final String name);
}
