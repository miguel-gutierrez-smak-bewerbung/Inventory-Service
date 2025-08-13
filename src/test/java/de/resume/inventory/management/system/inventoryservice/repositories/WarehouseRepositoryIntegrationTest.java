package de.resume.inventory.management.system.inventoryservice.repositories;

import de.resume.inventory.management.system.inventoryservice.models.entities.WarehouseEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(excludeAutoConfiguration = LiquibaseAutoConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.liquibase.enabled=false",
        "spring.jpa.hibernate.ddl-auto=none"
})
@Sql( scripts = "classpath:/schemaPostgreSQL.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS
)
class WarehouseRepositoryIntegrationTest {

    @Autowired
    private WarehouseRepository repository;

    @Test
    void findByName_returns_entity_when_present() {
        final WarehouseEntity expected = new WarehouseEntity();
        expected.setName("Main Warehouse");
        expected.setStreet("Street 1");
        expected.setZip("12345");
        expected.setCity("Cologne");
        expected.setCountry("Germany");

        repository.saveAndFlush(expected);

        final Optional<WarehouseEntity> actual = repository.findByName("Main Warehouse");
        assertThat(actual).isPresent();
        assertThat(actual.get())
                .usingRecursiveComparison()
                .ignoringFields("createdAt", "updatedAt")
                .isEqualTo(expected);
    }

    @Test
    void findByName_returns_empty_when_absent() {
        assertThat(repository.findByName("does-not-exist")).isEmpty();
    }
}