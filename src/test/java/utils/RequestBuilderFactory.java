package utils;

import io.restassured.specification.RequestSpecification;
import java.util.Map;
import static io.restassured.RestAssured.given;
import config.TestConfig;

public class RequestBuilderFactory {
    public static RequestSpecification createRequest(String endpoint, Map<String, String> headers, Object payload) {
        RequestSpecification req = given()
            .baseUri(TestConfig.BASE_URL)
            .basePath(endpoint);
        if (headers != null) req.headers(headers);
        if (payload != null) req.body(payload);
        return req;
    }
}

