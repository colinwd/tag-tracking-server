package com.colinwd;

import java.util.ArrayList;
import java.util.Collection;

public class Response {
    private String userId;
    private Collection<String> tags = new ArrayList<>();

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Collection<String> getTags() {
        return tags;
    }

    public void setTags(Collection<String> tags) {
        this.tags = tags;
    }
}
