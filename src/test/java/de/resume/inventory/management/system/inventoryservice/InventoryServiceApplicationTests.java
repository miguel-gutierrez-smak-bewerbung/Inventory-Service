package de.resume.inventory.management.system.inventoryservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.liquibase.enabled=false",
        "spring.jpa.hibernate.ddl-auto=none"
})
@Sql( scripts = "classpath:/schemaPostgreSQL.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS
)
@SpringBootTest
class InventoryServiceApplicationTests {

    @Test
    void contextLoads() {
    }

}
