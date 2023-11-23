package dev.fern.integrationtestdemo.glue;

import io.cucumber.java.AfterAll;
import io.cucumber.java.BeforeAll;
import io.cucumber.spring.CucumberContextConfiguration;
import java.time.Duration;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@Slf4j
@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("bdd")
public class SpringBootTestLoader {
    private static PostgreSQLContainer<?> psqlContainer;
    private static KafkaContainer kafkaContainer;

    @BeforeAll
    @SneakyThrows
    public static void setUp() {
        log.info("Setting up...");

        psqlContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres"));
        kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka"));

        psqlContainer
                .withDatabaseName("db1")
                .withUsername("user1")
                .withPassword("passge")
                .start();
        kafkaContainer.withEmbeddedZookeeper().start();
        Thread.sleep(Duration.ofMinutes(1).toMillis());
    }

    @AfterAll
    public static void tearDown() {
        log.info("kill");

        kafkaContainer.stop();
        psqlContainer.stop();
    }
}
