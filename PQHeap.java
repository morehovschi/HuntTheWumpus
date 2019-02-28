/*
    Marius Orehovschi
    F18
    Project 9: Priority Queues
    CS 231


*/

import java.util.Comparator;

public class PQHeap<T> {
    private Object[] heap;
    private int numItems;
    private Comparator<T> comp;

    public PQHeap(Comparator<T> comparator,int capacity){
        //constructor that sets this.heap and this.comp
        heap=new Object[capacity];
        comp=comparator;
        numItems=0;
    }

    public PQHeap(Comparator<T> comparator){
        //overloaded constructor that sets capacity to 15 by default
        this(comparator,15);
    }

    public boolean isEmpty(){

        return size()==0;
    }

    public int size(){
        //getter for numItems
        return numItems;
    }

    private static int parent(int idx){
        //calculates index of parent in heap given parameter idx
        return (idx-1)/2;
    }

    private static int leftChild(int idx){
        //calculates index of left child in heap given parameter idx
        return (2*idx)+1;
    }

    private static int rightChild(int idx){
        //calculates index of left child in heap given parameter idx
        return (2*idx)+2;
    }

    private void swap(int idx1,int idx2){
        //swaps items at idx1 and idx2 in heap
        Object first=heap[idx1];
        heap[idx1]=heap[idx2];
        heap[idx2]=first;
    }

    public void add(T obj){
        /*
        adds parameter object to heap
         */

        //make sure there is space
        ensureCapacity();

        heap[numItems++]=obj;

        reheapUP();
    }

    private void reheapUP() {
        //helper method that puts added element at correct place in heap
        if(numItems==1){
            return;
        }

        int swappableIdx=numItems-1;

        //keep swapping
        while(true){
            int result=comp.compare((T)heap[swappableIdx],(T)heap[parent(swappableIdx)]);

            if(result<=0){
                //if result is non-negative, done swapping; break and return
                break;
            }

            //if here, need to swap swappableIdx with parent
            swap(swappableIdx,parent(swappableIdx));
            //update swappableIdx
            swappableIdx=parent(swappableIdx);
        }
    }


    private void ensureCapacity(){
        /*
        checks if there is space for more than 1 item; if there isn't, replaces
        heap with new array double the size and copies over elements
         */

        //if there's space, no work to do
        if(numItems<heap.length){
            return;
        }

        Object[] newHeap=new Object[heap.length*2];

        for(int i = 0; i < heap.length; i++) {
            newHeap[i]=heap[i];
        }

        heap=newHeap;
    }

    public T remove(){
        //removes element at index 0 and calls helper method to restore heap order

        if(numItems==0){
            return null;
        }

        //save removable data in local variable
        T returnable=(T)heap[0];

        //helper method that restores heap order
        reheapDown();

        return returnable;
    }

    private void reheapDown() {
        //helper method that restores heap order

        if(numItems==1){
            //decrement numItems and set heap at index 0 to null
            heap[--numItems]=null;
            return;
        }

        //swap root and lowest child
        swap((numItems-1),0);

        //get rid of old root
        heap[--numItems]=null;

        //set swappableIdx to root and keep swapping until heap is in order
        int swappableIdx=0;
        while(true){
            //set left result to 0 by default and update it to actual value if it exists
            int leftResult=0;
            //if left child exists in array and is not null
            if(leftChild(swappableIdx)<heap.length && heap[leftChild(swappableIdx)]!=null){
                leftResult=comp.compare((T)heap[swappableIdx],(T)heap[leftChild(swappableIdx)]);
            }

            //set right result to 0 by default and update it to actual value if it exists
            int rightResult=0;
            //if right child exists in array and is not null
            if(rightChild(swappableIdx)<heap.length && heap[rightChild(swappableIdx)]!=null){
                rightResult=comp.compare((T)heap[swappableIdx],(T)heap[rightChild(swappableIdx)]);
            }

            //set result to the lowest of the 2 results
            int result=Math.min(leftResult,rightResult);

            //if true, child nodes are both smaller; done swapping; break and return
            if(result>=0){
                break;
            }

            //if here, swappableIdx needs to be swapped with one of its child nodes
            if(result==leftResult){
                //swap with left child
                swap(swappableIdx,leftChild(swappableIdx));
                swappableIdx=leftChild(swappableIdx);
            } else {
                //swap with right child
                swap(swappableIdx,rightChild(swappableIdx));
                swappableIdx=rightChild(swappableIdx);
            }
            }//end while loop
    }//end reheapDown()

    @Override
    public String toString() {
        //returns String representation of heap
        StringBuilder builder=new StringBuilder();

        builder.append("[ ");
        //append items in all array slots that are not null
        for(Object o:heap){
            if(o==null){
                continue;
            }

            builder.append(o);
            builder.append(" ");
        }
        builder.append("]");

        return builder.toString();
    }

    public static void main(String[] args) {
        /*
        Tests for PQHeap
         */

        PQHeap<Integer> heap=new PQHeap<>(new MaxHeapComparator());

        System.out.println("\nTesting add()");

        heap.add(4);
        heap.add(9);
        heap.add(8);
        heap.add(7);
        heap.add(6);
        heap.add(3);
        heap.add(4);
        heap.add(10);

        System.out.println(heap);
        System.out.println();

        System.out.println("\nTesting remove()");
        for (int i = 0; i < 8; i++) {
            System.out.println(heap.remove());
        }

    }
}
