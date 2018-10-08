package com.colinwd;

import java.time.Instant;
import java.util.Objects;

public class Tag implements Comparable<Tag> {
    private String userId;
    private Instant timestamp;
    private String tag;
    private Action action;

    public Tag(String userId, Instant timestamp, String tag) {
        this.userId = userId;
        this.timestamp = timestamp;
        this.tag = tag;
    }

    public String getUserId() {
        return userId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getTag() {
        return tag;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public boolean isBeforeOrEqual(Tag tag) {
        return this.getTimestamp().isBefore(tag.getTimestamp()) || this.getTimestamp().equals(tag.getTimestamp());
    }

    public boolean isBefore(Tag tag) {
        return this.getTimestamp().isBefore(tag.getTimestamp());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag1 = (Tag) o;
        return Objects.equals(userId, tag1.userId) &&
                Objects.equals(tag, tag1.tag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, tag);
    }

    @Override
    public int compareTo(Tag o) {
        return this.getTimestamp().compareTo(o.getTimestamp());
    }
}
