package com.colinwd;

import com.google.common.collect.Sets;
import com.google.gson.Gson;

import java.util.HashSet;
import java.util.Set;

public class RequestHandler {

    private static final Gson GSON = new Gson();
    private static final TagDatabase tagDatabase = TagDatabase.getInstance();

    public static String handleRequest(String input) {
        try {
            Request request = GSON.fromJson(input, Request.class);

            Set<Tag> add = new HashSet<>(request.tagsToAdd());
            Set<Tag> remove = new HashSet<>(request.tagsToRemove());
            Sets.SetView<Tag> tagsInAddAndNotRemove = Sets.difference(add, remove);

            String userId = request.getUserId();

            tagDatabase.add(tagsInAddAndNotRemove);
            tagDatabase.remove(remove);

            Response response = new Response();
            response.setUserId(userId);
            response.setTags(tagDatabase.getTagValuesFor(userId));

            return GSON.toJson(response);
        } catch (Exception e) {
            return "{ \"error\": \"" + e.getMessage() + "\" }";
        }
    }
}
