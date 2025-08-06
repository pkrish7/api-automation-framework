package config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import mocks.WireMockServerSetup;

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
}
