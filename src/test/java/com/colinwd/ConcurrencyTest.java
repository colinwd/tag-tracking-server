package com.colinwd;

import com.colinwd.client.TestClient;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class ConcurrencyTest {

    private static final Gson GSON = new Gson();

    /**
     * Occasional failures are expected here as the clients are contending for a shared database that is intended
     * to reflect current state, not the state after their request.
     * If a client A removes a tag right after client B added, it will reflect that removal and client B will think the
     * add did not succeed.
     * @throws InterruptedException
     */
    @Test
    void testMultipleClients() throws InterruptedException {
        List<Thread> threadPool = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            System.out.println("Starting client " + i + "...");
            Thread thread = new Thread(makeRequest());
            threadPool.add(thread);
        }

        threadPool.forEach(Thread::run);
        for (Thread thread : threadPool) {
            try {
                thread.join();
            } finally {
                //whatever
            }
        }
    }

    private Runnable makeRequest() {
        return () -> {
            TestClient client = new TestClient();

            while (true) {
                try {
                    client.connect("localhost", 27015);
                    Request request = RandomRequestGenerator.generate();

                    Set<String> add = request.tagsToAdd().stream().map(Tag::getTag).collect(Collectors.toSet());
                    Set<String> remove = request.tagsToRemove().stream().map(Tag::getTag).collect(Collectors.toSet());
                    add.removeAll(remove);

                    String response = client.send(GSON.toJson(request));
                    Response result = GSON.fromJson(response, Response.class);
                    HashSet<String> returnedTags = new HashSet<>(result.getTags());

                    if (!returnedTags.containsAll(add)) {
                        System.out.println("Uh oh! Tags don't match for " + request.getUserId());
                        System.out.println("Tried to add: " + add);
                        System.out.println("Received: " + returnedTags);
                    } else {
                        System.out.println("Success! Added " + add + " to " + returnedTags);
                    }

                    for (String tag : remove) {
                        if (returnedTags.contains(tag)) {
                            System.out.println("Uh oh! Removed tag still there for " + request.getUserId());
                            System.out.println("Tried to remove: " + tag);
                            System.out.println("Received: " + returnedTags);
                        } else {
                            System.out.println("Success! Removed " + tag + " from " + returnedTags);
                        }
                    }

                    Thread.sleep(1000);
                } catch (IOException | InterruptedException e) {
                    //whatever
                }
            }
        };
    }

    static class RandomRequestGenerator {
        private static final String[] USERS = new String[] { "colin", "max", "cory", "russell" };
        private static final String[] TAGS = new String[] { "a", "b", "c", "d", "e", "f", "g" };

        static Request generate() {
            String userName = getUserName();

            int tagCount = new Random().nextInt(4) + 1; //don't do zero tags

            List<String> tags = getTags(tagCount);
            int tagSplitIndex = new Random().nextInt(tags.size());
            List<String> addTags = tags.subList(0, tagSplitIndex);
            List<String> removeTags = tags.subList(tagSplitIndex, tags.size());

            return new RequestBuilder()
                    .userId(userName)
                    .addTags(addTags)
                    .removeTags(removeTags)
                    .timestamp(Instant.now())
                    .build();
        }

        private static Instant getTimestampWithSkew() {
            int skew = new Random().nextInt(1000);
            boolean addTime = new Random().nextBoolean();

            if (addTime) {
                return Instant.now().plus((long) skew, ChronoUnit.MILLIS);
            } else {
                return Instant.now().minus((long) skew, ChronoUnit.MILLIS);
            }
        }

        private static List<String> getTags(int tagCount) {
            List<String> tags = new ArrayList<>();

            for (int i = 0; i < tagCount; i++) {
                int tagIndex = new Random().nextInt(6);
                String tag = TAGS[tagIndex];
                tags.add(tag);
            }

            return tags;
        }

        private static String getUserName() {
            int userIndex = new Random().nextInt(4);
            return USERS[userIndex];
        }
    }
}
