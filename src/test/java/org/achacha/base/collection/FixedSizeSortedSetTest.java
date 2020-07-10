package org.achacha.base.collection;


import org.junit.jupiter.api.Test;

import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FixedSizeSortedSetTest {
    @Test
    void testFixedSizeNoComparator() {
        FixedSizeSortedSet<String> set = new FixedSizeSortedSet<>(3);
        set.add("Red");
        set.add("Green");
        set.add("Blue");
        set.add("Orange");
        set.add("Yellow");

        assertEquals("[Blue, Green, Orange]", set.toString());
    }

    @Test
    void testFixedSizeComparator() {
        FixedSizeSortedSet<String> set = new FixedSizeSortedSet<>(Comparator.reverseOrder(), 3);  // Reverse order
        set.add("Red");
        set.add("Green");
        set.add("Blue");
        set.add("Orange");
        set.add("Yellow");

        assertEquals("[Yellow, Red, Orange]", set.toString());
    }
}
