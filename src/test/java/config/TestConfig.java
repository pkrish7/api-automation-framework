package config;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
public class TestConfig {
    public static final String BASE_URL;
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = TestConfig.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input != null) {
                log.info("Loading configuration from config.properties");
                properties.load(input);
                BASE_URL = properties.getProperty("base.url", "http://localhost:9090");
                log.info("BASE_URL set to: {}", BASE_URL);
            } else {
                log.info("config.properties not found, using default BASE_URL");
                BASE_URL = "http://localhost:9090";
            }
        } catch (IOException e) {
            log.error("Failed to load config.properties", e);
            throw new RuntimeException("Failed to load config.properties", e);
        }
    }

    public static String getProperty(String key) {
        String value = properties.getProperty(key);
        log.info("Fetching property '{}': {}", key, value);
        return value;
    }

    public static String getEmployeesEndpoint() {
        String value = properties.getProperty("employees.endpoint");
        if (value == null || value.isEmpty()) {
            log.error("Property employees.endpoint not found or empty");
            throw new IllegalStateException("Property employees.endpoint not found or empty");
        }
        log.info("employees.endpoint: {}", value);
        return value;
    }

    public static String getEmployeePayloadPath() {
        String value = properties.getProperty("payload.employee");
        if (value == null || value.isEmpty()) {
            log.error("Property payload.employee not found or empty");
            throw new IllegalStateException("Property payload.employee not found or empty");
        }
        log.info("payload.employee: {}", value);
        return value;
    }

    public static String getEmployeeSchemaPath() {
        String value = properties.getProperty("schema.employee");
        if (value == null || value.isEmpty()) {
            log.error("Property schema.employee not found or empty");
            throw new IllegalStateException("Property schema.employee not found or empty");
        }
        log.info("schema.employee: {}", value);
        return value;
    }

    public static String getEmployeesTestDataPath() {
        String env = properties.getProperty("env", "dev");
        String value = properties.getProperty("testdata.employees");
        if (value == null || value.isEmpty()) {
            log.error("Property testdata.employees not found or empty");
            throw new IllegalStateException("Property testdata.employees not found or empty");
        }

        if (env == null || env.isEmpty()) {
            log.warn("Environment not specified, using default 'dev'");
            env = "dev";
        }

        log.info("Using environment: {}", env);

        if (value.startsWith("testdata/" + env + "/")) {
            log.info("testdata.employees path is already environment-specific: {}", value);
            return value;
        }

        String fileName = value.substring(value.lastIndexOf('/') + 1);
        String fullPath = String.join("/", "testdata", env, fileName);
        log.info("Computed environment-specific testdata.employees path: {}", fullPath);
        return fullPath;
    }

    public static int getWireMockPort() {
        String value = properties.getProperty("wiremock.port");
        if (value == null || value.isEmpty()) {
            log.error("Property wiremock.port not found or empty");
            throw new IllegalStateException("Property wiremock.port not found or empty");
        }
        int port = Integer.parseInt(value);
        log.info("wiremock.port: {}", port);
        return port;
    }
}
