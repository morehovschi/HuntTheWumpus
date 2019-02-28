/*
    Marius Orehovschi
    F18
    Project 10: Hunt the Wumpus
    CS 231
*/

/**
 * Basic comparator for Vertex class;
 * helper class for PQHeap
 */

import java.util.Comparator;

public class VertexComparator implements Comparator<Vertex> {

    @Override
    public int compare(Vertex o1, Vertex o2) {

        return o1.getCost() - o2.getCost();
    }
}
