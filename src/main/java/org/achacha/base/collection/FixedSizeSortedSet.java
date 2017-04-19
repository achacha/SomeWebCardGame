package org.achacha.base.collection;

import java.util.Comparator;
import java.util.TreeSet;

public class FixedSizeSortedSet<E> extends TreeSet<E>
{
    private static final long serialVersionUID = 1L;

    private final Comparator<? super E> comparator;
    private final int maxSize;

    /**
     * Fixed size set
     * Comparator by default will sort lowest to highest (default sort) and one keep lowest
     * For String type lower ASCII value is kept over higher ASCII value (as per #String.compareTo)
     * @param maxSize int maximum elements to keep in set, anything over is discarded
     * @see Comparable
     * @see Comparator
     */
    public FixedSizeSortedSet(int maxSize) {
        this(null, maxSize);
    }

    /**
     * Fixed size set of maxSize with camparator used to sort
     * @param comparator Comparator for sorting
     * @param maxSize int max size of set
     */
    public FixedSizeSortedSet(Comparator<? super E> comparator, int maxSize) {
        super(comparator);
        this.comparator = comparator;
        this.maxSize = maxSize;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean add(E e) {
        if(size() >= maxSize)
        {
            E smallest = last();
            int comparison;
            if(comparator == null)
                comparison = ((Comparable<E>)e).compareTo(smallest);
            else
                comparison = comparator.compare(e, smallest);

            if(comparison < 0)
            {
                remove(smallest);
                return super.add(e);
            }
            return false;
        }
        else
        {
            return super.add(e);
        }
    }
}