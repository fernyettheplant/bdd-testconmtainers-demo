package dev.fern.integrationtestdemo.glue;

import io.cucumber.java.AfterAll;
import io.cucumber.java.BeforeAll;
import io.cucumber.spring.CucumberContextConfiguration;
import java.util.concurrent.CompletableFuture;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.CosmosDBEmulatorContainer;
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
    private static CosmosDBEmulatorContainer cosmosDBEmulatorContainer;

    @BeforeAll
    @SneakyThrows
    public static void setUp() {
        log.info("Setting up...");

        CompletableFuture<Void> psqlSetup = CompletableFuture.runAsync(() -> {
            psqlContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres"));
            psqlContainer
                    .withDatabaseName("db1")
                    .withUsername("user1")
                    .withPassword("passge")
                    .start();
        });

        CompletableFuture<Void> kafkaSetup = CompletableFuture.runAsync(() -> {
            kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka"));
            kafkaContainer.withEmbeddedZookeeper().start();
        });

        CompletableFuture<Void> cosmosSetup = CompletableFuture.runAsync(() -> {
            cosmosDBEmulatorContainer = new CosmosDBEmulatorContainer(
                    DockerImageName.parse("mcr.microsoft.com/cosmosdb/linux/azure-cosmos-emulator:latest"));

            cosmosDBEmulatorContainer.start();
        });

        CompletableFuture.allOf(psqlSetup, kafkaSetup, cosmosSetup).join();
    }

    @AfterAll
    public static void tearDown() {
        log.info("kill");

        kafkaContainer.stop();
        psqlContainer.stop();
        cosmosDBEmulatorContainer.stop();
    }
}
