/*
    Marius Orehovschi
    F18
    Project 9: Priority Queues
    CS 231
*/

import java.util.Comparator;

/**
 * Basic comparator for PQHeap
 */

public class MaxHeapComparator implements Comparator<Integer> {
    @Override
    public int compare(Integer int1, Integer int2) {
        return int1-int2;
    }
}
