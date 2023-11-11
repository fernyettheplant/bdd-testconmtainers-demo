package dev.fern.integrationtestdemo.glue;

import com.azure.cosmos.CosmosAsyncClient;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.models.CosmosContainerResponse;
import com.azure.cosmos.models.CosmosDatabaseResponse;
import io.cucumber.java.AfterAll;
import io.cucumber.java.BeforeAll;
import io.cucumber.spring.CucumberContextConfiguration;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.CosmosDBEmulatorContainer;
import org.testcontainers.utility.DockerImageName;

@Slf4j
@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("bdd")
public class SpringBootTestLoader {
    static CosmosDBEmulatorContainer cosmosDBEmulatorContainer;

    @BeforeAll
    public static void setup() {
        log.info("Set up");

        cosmosDBEmulatorContainer = new CosmosDBEmulatorContainer(
                DockerImageName.parse("mcr.microsoft.com/cosmosdb/linux/azure-cosmos-emulator:latest"));

        cosmosDBEmulatorContainer.start();

        Path keyStoreFile;

        try {
            // Create a temporary file for the keystore
            keyStoreFile = Files.createTempFile("azure-cosmos-emulator", ".keystore");

            // Build a new KeyStore using the emulator
            KeyStore keyStore = cosmosDBEmulatorContainer.buildNewKeyStore();

            // Get the emulator key as a char array
            char[] emulatorKey = cosmosDBEmulatorContainer.getEmulatorKey().toCharArray();

            // Create a FileOutputStream to write the KeyStore to the file

            try (FileOutputStream fos = new FileOutputStream(keyStoreFile.toFile())) {
                // Store the KeyStore in the file using the emulator key
                keyStore.store(fos, emulatorKey);
            }
            // Always ensure the FileOutputStream is closed
        } catch (IOException | KeyStoreException | NoSuchAlgorithmException | CertificateException e) {
            throw new RuntimeException();
        }

        System.setProperty("javax.net.ssl.trustStore", keyStoreFile.toString());
        System.setProperty("javax.net.ssl.trustStorePassword", cosmosDBEmulatorContainer.getEmulatorKey());
        System.setProperty("javax.net.ssl.trustStoreType", "PKCS12");

        CosmosAsyncClient client = new CosmosClientBuilder()
                .gatewayMode()
                .endpointDiscoveryEnabled(false)
                .endpoint(cosmosDBEmulatorContainer.getEmulatorEndpoint())
                .key(cosmosDBEmulatorContainer.getEmulatorKey())
                .buildAsyncClient();

        CosmosDatabaseResponse databaseResponse =
                client.createDatabaseIfNotExists("Azure").block();
        CosmosContainerResponse containerResponse = client.getDatabase("Azure")
                .createContainerIfNotExists("ServiceContainer", "/name")
                .block();

        client.readAllDatabases().toStream().forEach(x -> log.info("DB Created {}", x.getId()));

        client.getDatabase("Azure")
                .readAllContainers()
                .toStream()
                .forEach(x -> log.info("Container in Azure is {}", x.getId()));
    }

    @AfterAll
    public static void tearDown() {
        log.info("kill");

        cosmosDBEmulatorContainer.stop();
    }
}
