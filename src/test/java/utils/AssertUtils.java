package utils;

import org.testng.Assert;
import io.restassured.response.Response;
import java.util.Map;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.util.Set;

public class AssertUtils {
    public static void assertStatusCode(Response response, int expectedStatus) {
        Assert.assertEquals(response.getStatusCode(), expectedStatus,
                "Expected status code: " + expectedStatus + ", but got: " + response.getStatusCode());
    }

    public static void assertJsonFieldEquals(Response response, String field, Object expectedValue) {
        Object actualValue = response.jsonPath().get(field);
        Assert.assertEquals(actualValue, expectedValue,
                "Expected value for field '" + field + "': " + expectedValue + ", but got: " + actualValue);
    }

    public static void assertJsonSchema(Response response, String schemaResourcePath) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);
            InputStream schemaStream = AssertUtils.class.getClassLoader().getResourceAsStream(schemaResourcePath);
            if (schemaStream == null) {
                throw new AssertionError("Schema file not found: " + schemaResourcePath);
            }
            JsonSchema schema = factory.getSchema(schemaStream);
            Set<ValidationMessage> errors = schema.validate(mapper.readTree(response.getBody().asString()));
            Assert.assertTrue(errors.isEmpty(), "JSON schema validation errors: " + errors);
        } catch (Exception e) {
            throw new AssertionError("Schema validation failed: " + e.getMessage(), e);
        }
    }
}
