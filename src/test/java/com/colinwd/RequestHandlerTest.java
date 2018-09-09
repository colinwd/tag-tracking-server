package com.colinwd;

import com.google.gson.Gson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Tag("integration")
public class RequestHandlerTest {

    private static final Gson GSON = new Gson();

    @BeforeEach
    void beforeEach() {
        TagDatabase.getInstance().clear();
    }

    @Test
    public void homeworkExampleTest() {
        String userId = "Secret Squirrel";
        Request alpha = new RequestBuilder()
                .userId(userId)
                .addTags("beyhive_member", "timbers_army")
                .timestamp(Instant.parse("2018-08-10T06:49:04.440Z"))
                .build();

        Response alphaResponse = makeRequest(alpha);
        Assertions.assertEquals(userId, alphaResponse.getUserId());
        Assertions.assertTrue(alphaResponse.getTags().contains("beyhive_member"));
        Assertions.assertTrue(alphaResponse.getTags().contains("timbers_army"));

        //two cases in one to ensure ordering as in homework document
        Request beta = new RequestBuilder()
                .userId(userId)
                .addTags("belieber")
                .removeTags("timbers_army")
                .timestamp(Instant.parse("2018-08-10T06:49:04.550Z"))
                .build();

        Response betaResponse = makeRequest(beta);
        Assertions.assertEquals(userId, alphaResponse.getUserId());
        Assertions.assertTrue(betaResponse.getTags().contains("beyhive_member"));
        Assertions.assertTrue(betaResponse.getTags().contains("belieber"));
        Assertions.assertFalse(betaResponse.getTags().contains("timbers_army"));
    }

    @Test
    void testPartTwoFlowOne() {
        String userId = "1";
        Instant timestamp = Instant.now();

        Request firstAdd = new RequestBuilder()
                .userId(userId)
                .addTags("a")
                .timestamp(timestamp)
                .build();
        Response firstAddResponse = makeRequest(firstAdd);
        Assertions.assertTrue(firstAddResponse.getTags().contains("a"));

        Request secondAdd = new RequestBuilder()
                .userId(userId)
                .addTags("a")
                .timestamp(timestamp.minus(10, ChronoUnit.NANOS))
                .build();
        Response secondAddResponse = makeRequest(secondAdd);
        Assertions.assertTrue(secondAddResponse.getTags().contains("a"));

        Request remove = new RequestBuilder()
                .userId(userId)
                .removeTags("a")
                .timestamp(timestamp.minus(5, ChronoUnit.NANOS))
                .build();
        Response removeResponse = makeRequest(remove);
        Assertions.assertTrue(removeResponse.getTags().contains("a"));
    }

    @Test
    public void testPartTwoFlowTwo() {
        String userId = "1";
        Instant timestamp = Instant.now();

        Request remove = new RequestBuilder()
                .userId(userId)
                .removeTags("a")
                .timestamp(timestamp)
                .build();
        Response removeResponse = makeRequest(remove);
        Assertions.assertFalse(removeResponse.getTags().contains("a"));

        Request add = new RequestBuilder()
                .userId(userId)
                .addTags("a")
                .timestamp(timestamp.minus(10, ChronoUnit.SECONDS))
                .build();
        Response addResponse = makeRequest(add);
        Assertions.assertFalse(addResponse.getTags().contains("a"));
    }

    private Response makeRequest(Request request) {
        String responseJson = RequestHandler.handleRequest(GSON.toJson(request));
        return GSON.fromJson(responseJson, Response.class);
    }
}
