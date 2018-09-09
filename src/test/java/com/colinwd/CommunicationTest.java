package com.colinwd;

import com.colinwd.client.TestClient;
import com.google.gson.Gson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("system")
public class CommunicationTest {

    private static final Gson GSON = new Gson();

    @Test
    void testCommunication() throws IOException {
        TestClient client = new TestClient();
        client.connect("localhost", 27015);
        String response = client.send(GSON.toJson(getTestRequest()));
        Response result = GSON.fromJson(response, Response.class);
        assertEquals(result.getUserId(), "Colin");
        assertEquals(result.getTags(), Collections.singletonList("timbers_army"));
    }

    private Request getTestRequest() {
        return new RequestBuilder().userId("Colin").addTags("timbers_army").timestamp(Instant.now()).build();
    }
}
