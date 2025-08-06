package config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class TestConfig {
    public static final String BASE_URL;
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = TestConfig.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input != null) {
                properties.load(input);
                BASE_URL = properties.getProperty("base.url", "http://localhost:9090");
            } else {
                BASE_URL = "http://localhost:9090";
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.properties", e);
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    public static String getEmployeesEndpoint() {
        return properties.getProperty("employees.endpoint");
    }

    public static String getEmployeePayloadPath() {
        return properties.getProperty("payload.employee");
    }

    public static String getEmployeeSchemaPath() {
        return properties.getProperty("schema.employee");
    }

    public static String getEmployeesTestDataPath() {
        return properties.getProperty("testdata.employees");
    }

    public static int getWireMockPort() {
        return Integer.parseInt(properties.getProperty("wiremock.port", "9090"));
    }
}
