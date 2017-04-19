package org.achacha.base.collection;


import org.junit.Assert;
import org.junit.Test;

import java.util.Comparator;

public class TestFixedSizeSortedSet {
    @Test
    public void testFixedSizeNoComparator() {
        FixedSizeSortedSet<String> set = new FixedSizeSortedSet<>(3);
        set.add("Red");
        set.add("Green");
        set.add("Blue");
        set.add("Orange");
        set.add("Yellow");

        Assert.assertEquals("[Blue, Green, Orange]", set.toString());
    }

    @Test
    public void testFixedSizeComparator() {
        FixedSizeSortedSet<String> set = new FixedSizeSortedSet<>(Comparator.reverseOrder(), 3);  // Reverse order
        set.add("Red");
        set.add("Green");
        set.add("Blue");
        set.add("Orange");
        set.add("Yellow");

        Assert.assertEquals("[Yellow, Red, Orange]", set.toString());
    }
}
