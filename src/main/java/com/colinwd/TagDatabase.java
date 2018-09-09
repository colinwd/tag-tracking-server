package com.colinwd;

import java.util.*;
import java.util.stream.Collectors;

class TagDatabase {

    private static TagDatabase instance;

    private Map<String, Set<Tag>> tagDb = new HashMap<>();

    private TagDatabase() {
    }

    static TagDatabase getInstance() {
        if (instance == null) {
            instance = new TagDatabase();
        }

        return instance;
    }

    Set<Tag> getTagsFor(String userId) {
        if (tagDb.containsKey(userId)) {
            return tagDb.get(userId).stream().filter(t -> t.getAction() == Action.ADD).collect(Collectors.toSet());
        } else {
            return new HashSet<>();
        }
    }

    Set<String> getTagValuesFor(String userId) {
        if (tagDb.containsKey(userId)) {
            return getTagsFor(userId).stream().map(Tag::getTag).collect(Collectors.toSet());
        } else {
            return new HashSet<>();
        }
    }

    void add(Collection<Tag> tagsToAdd) {
        tagsToAdd.forEach(this::add);
    }

    void add(Tag tag) {
        String userId = tag.getUserId();
        createUserIfNotExists(userId);
        Set<Tag> userTags = getAllTagsFor(userId);

        if (userTags.contains(tag)) {
            //replace only if timestamp is newer
            boolean removed = userTags.removeIf(t -> Objects.equals(t, tag) && t.isBefore(tag));
            if (removed) {
                tag.setAction(Action.ADD);
                put(userId, tag);
            }
        } else {
            tag.setAction(Action.ADD);
            put(userId, tag);
        }
    }

    void remove(Collection<Tag> tagsToRemove) {
        tagsToRemove.forEach(this::remove);
    }

    void remove(Tag tag) {
        String userId = tag.getUserId();
        createUserIfNotExists(userId);
        Set<Tag> userTags = getAllTagsFor(userId);
        if (userTags.contains(tag)) {
            boolean removed = userTags.removeIf(t -> Objects.equals(t, tag) && t.isBeforeOrEqual(tag));
            if (removed) {
                tag.setAction(Action.REMOVE);
                put(userId, tag);
            }
        } else {
            tag.setAction(Action.REMOVE);
            put(userId, tag);
        }
    }

    void clear() {
        tagDb = new HashMap<>();
    }

    private void put(String userId, Tag tag) {
        Set<Tag> userTags = tagDb.get(userId);
        userTags.add(tag);
    }

    private Set<Tag> getAllTagsFor(String userId) {
        if (tagDb.containsKey(userId)) {
            return tagDb.get(userId);
        } else {
            return new HashSet<>();
        }
    }

    private void createUserIfNotExists(String userId) {
        if (!userExists(userId)) {
            tagDb.put(userId, new HashSet<>());
        }
    }

    private boolean userExists(String userId) {
        return tagDb.containsKey(userId);
    }
}
