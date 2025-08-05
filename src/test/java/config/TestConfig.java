package config;

import mocks.WireMockServerSetup;

public class TestConfig {
    public static final String BASE_URL = "http://localhost:9090";

    public static void startWireMock() {
        WireMockServerSetup.startServer();
    }

    public static void stopWireMock() {
        WireMockServerSetup.stopServer();
    }
}

