package com.colinwd;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Request {

    private String userId;
    private List<String> add = new ArrayList<>();
    private List<String> remove = new ArrayList<>();
    private String timestamp;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<String> getAdd() {
        return add;
    }

    public void setAdd(List<String> add) {
        this.add = add;
    }

    public List<String> getRemove() {
        return remove;
    }

    public void setRemove(List<String> remove) {
        this.remove = remove;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    Set<Tag> tagsToAdd() {
        return add.stream().map(s -> new Tag(userId, Instant.parse(timestamp), s)).collect(Collectors.toSet());
    }

    Set<Tag> tagsToRemove() {
        return remove.stream().map(s -> new Tag(userId, Instant.parse(timestamp), s)).collect(Collectors.toSet());
    }
}
