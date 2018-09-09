package com.colinwd;

import com.google.common.collect.Sets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;

class TagDatabaseTest {

    private static TagDatabase tagDatabase = TagDatabase.getInstance();
    private Set<Tag> testTags;
    private static final String USER_ID = "Colin";

    @BeforeEach
    void beforeEach() {
        tagDatabase.clear();
        testTags = new HashSet<>();
        testTags.add(new Tag("Colin", Instant.now(),"timbers_army"));
        testTags.add(new Tag("Colin", Instant.now(),"riveter"));
        testTags.add(new Tag("Colin", Instant.now(), "musician"));
        tagDatabase.add(testTags);
    }

    @Test
    void testAddAndRetrieveTags() {
        Set<Tag> colinTags = tagDatabase.getTagsFor(USER_ID);
        Assertions.assertEquals(0, Sets.symmetricDifference(testTags, colinTags).size());
    }

    @Test
    void testRemoveAndRetrieveTags() {
        Tag tag = new Tag("Colin", Instant.now(), "timbers_army");
        tagDatabase.remove(tag);

        Set<String> colinTags = tagDatabase.getTagValuesFor(USER_ID);
        Assertions.assertFalse(colinTags.contains("timbers_army"));
    }

    @Test
    void testAddTagsUserNotExists() {
        Set<Tag> maxTags = new HashSet<>();
        maxTags.add(new Tag("Max", Instant.now(), "game_dev"));
        tagDatabase.add(maxTags);

        Set<Tag> retrievedTags = tagDatabase.getTagsFor("Max");
        Assertions.assertEquals(0, Sets.symmetricDifference(maxTags, retrievedTags).size());
    }

    @Test
    void testOlderTagIsReplacedByNewer() {
        Tag firstTag = new Tag("Colin", Instant.now(), "engineer");
        tagDatabase.add(firstTag);

        Instant newerTimestamp = Instant.now().plus(5, ChronoUnit.MILLIS);
        Tag secondTag = new Tag("Colin", newerTimestamp, "engineer");
        tagDatabase.add(secondTag);

        Set<Tag> colin = tagDatabase.getTagsFor("Colin");
        colin.stream().forEach(t -> {
            if (t == secondTag) {
                Assertions.assertEquals(t.getTimestamp(), secondTag.getTimestamp());
            }
        });
    }

    @Test
    void testNewerTagIsNotReplacedByOlder() {
        Tag firstTag = new Tag("Colin", Instant.now(), "engineer");
        tagDatabase.add(firstTag);

        Instant olderTimestamp = Instant.now().minus(5, ChronoUnit.DAYS);
        Tag secondTag = new Tag("Colin", olderTimestamp, "engineer");
        tagDatabase.add(secondTag);

        Set<Tag> colin = tagDatabase.getTagsFor("Colin");
        colin.stream().forEach(t -> {
            if (t == secondTag) {
                Assertions.assertEquals(t.getTimestamp(), firstTag.getTimestamp());
            }
        });
    }

    @Test
    void testConcurrentAddThenRemove() {
        Tag tag = new Tag("Colin", Instant.now(), "dungeon_master");

        tagDatabase.add(tag);
        tagDatabase.remove(tag);

        Assertions.assertEquals(3, tagDatabase.getTagValuesFor("Colin").size());
    }

    @Test
    void testConcurrentRemoveThenAdd() {
        Tag tag = new Tag("Colin", Instant.now(), "dungeon_master");

        tagDatabase.remove(tag);
        tagDatabase.add(tag);

        Assertions.assertEquals(3, tagDatabase.getTagValuesFor("Colin").size());
    }
}
