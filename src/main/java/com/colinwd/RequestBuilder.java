package com.colinwd;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Arrays;
import java.util.List;

public class RequestBuilder {

    private Request request = new Request();

    public Request build() {
        return this.request;
    }

    public RequestBuilder userId(String userId) {
        this.request.setUserId(userId);
        return this;
    }

    public RequestBuilder addTags(String... tags) {
        this.request.setAdd(Arrays.asList(tags));
        return this;
    }

    public RequestBuilder addTags(List<String> tags) {
        this.request.setAdd(tags);
        return this;
    }

    public RequestBuilder removeTags(String... remove) {
        this.request.setRemove(Arrays.asList(remove));
        return this;
    }

    public RequestBuilder removeTags(List<String> tags) {
        this.request.setRemove(tags);
        return this;
    }

    public RequestBuilder timestamp(Instant timestamp) {
        DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendInstant(3).toFormatter();
        this.request.setTimestamp(formatter.format(timestamp));
        return this;
    }
}
