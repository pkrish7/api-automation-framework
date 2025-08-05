package mocks;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class WireMockServerSetup {
    private static WireMockServer wireMockServer;

    public static void startServer() {
        wireMockServer = new WireMockServer(WireMockConfiguration.options().port(9090));
        wireMockServer.start();
        WireMock.configureFor("localhost", 9090);
        setupStubs();
    }

    public static void stopServer() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

    private static void setupStubs() {
        // GET /employees
        WireMock.stubFor(WireMock.get(urlEqualTo("/employees"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                    .withBody("[{\"id\":1,\"name\":\"John Doe\",\"role\":\"Developer\"},{\"id\":2,\"name\":\"Jane Smith\",\"role\":\"Tester\"}]")
            )
        );

        WireMock.stubFor(WireMock.get(urlEqualTo("/employees/1"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\":1,\"name\":\"John Doe\",\"role\":\"Developer\"}")
                )
        );

        WireMock.stubFor(WireMock.get(urlEqualTo("/employees/999"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\":\"Employee not found\"}")
                )
        );

        // POST /employees
        WireMock.stubFor(post(urlEqualTo("/employees"))
            .willReturn(aResponse()
                .withStatus(201)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"id\":3,\"name\":\"Alice Brown\",\"role\":\"Manager\"}")
            )
        );

        // POST /employees - 400 Bad Request
        WireMock.stubFor(post(urlEqualTo("/employees"))
                .withRequestBody(equalToJson("{\"name\":\"\"}"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\":\"Invalid payload\"}")
                )
        );

        // PUT /employees/1
        stubFor(put(urlEqualTo("/employees/1"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"id\":1,\"name\":\"John Doe Updated\",\"role\":\"Lead\"}")
            )
        );

        // PUT /employees/999 - 404 Not Found
        stubFor(put(urlEqualTo("/employees/999"))
            .willReturn(aResponse()
                .withStatus(404)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"error\":\"Employee not found\"}")
            )
        );

        // PUT /employees/1 - 400 Bad Request for invalid payload
        stubFor(put(urlEqualTo("/employees/1"))
                .withRequestBody(equalToJson("{\"name\":\"\"}"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\":\"Invalid payload\"}")
                )
        );

        // DELETE /employees/1 - Success
        stubFor(delete(urlEqualTo("/employees/1"))
            .willReturn(aResponse()
                .withStatus(204)
            )
        );

        // DELETE /employees/999 - Not Found
        stubFor(delete(urlEqualTo("/employees/999"))
            .willReturn(aResponse()
                .withStatus(404)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"error\":\"invalid id\"}")
            )
        );
    }
}
