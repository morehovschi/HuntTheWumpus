/*
    Marius Orehovschi
    F18
    Project 8: Hash Tables
    CS 231

    Slightly modified for Project 10: Hunt The Wumpus --only hash(int key) was modified
*/

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Closed hash map that stores key-value pairs in array slots and uses linear
 * probing to handle collisions
 */

public class ClosedHashmap<K,V> implements MapSet<K,V> {
    private KeyValuePair<K,V>[] array;
    private boolean[] beenUsed;//keeps track of whether array slot at index has been used

    private int numItems;
    private int numCollisions;//stores number if collisions
    private KeyValuePair<K,V> lastAdded;

    public ClosedHashmap(int capacity){
        //constructor that initiates arrays to parameter capacity
        array=(KeyValuePair<K, V>[]) Array.newInstance(KeyValuePair.class,capacity);
        beenUsed=new boolean[capacity];

        numItems=0;
        numCollisions=0;
    }

    public ClosedHashmap(){
        //overloaded constructor that sets capacity to 20 by default
        this(20);
    }

    public int getNumCollisions() {
        //getter for numCollisions
        return numCollisions;
    }

    public KeyValuePair<K, V>[] getArray() {
        //getter for array of key-value pairs
        return array;
    }

    public boolean[] getBeenUsed() {
        //getter for beenUsed
        return beenUsed;
    }

    public KeyValuePair<K, V> getLastAdded() {
        return lastAdded;
    }

    public V put(K new_key, V new_value) {
        /*
        puts key-value pair with parameter new_key and new_value and returns null;
        if key already contained in array, replaces old value with new_value and returns old value
         */

        /*
        helper method that doubles the size of the arrays if at least half of the slots are full
         */
        ensureCapacity();
        lastAdded=new KeyValuePair<>(new_key,new_value);

        //make an index for new_key
        int location=hash(new_key);

        //if array at index is null, assign it a new key-value pair with new_key and new_value
        if(array[location]==null){
            array[location]=new KeyValuePair<>(new_key,new_value);

            //housekeeping
            beenUsed[location]=true;
            numItems++;
            return null;
        }

        //if key is already contained at location, replace old value with new_value and return old
        if(array[location].getKey().equals(new_key)){
            V old_value=array[location].getValue();
            array[location].setValue(new_value);
            return old_value;
        }

        //while the current location has been used
        while (beenUsed[location]==true){
            //if corresponding key is found, replace old value with new and return old
            if(array[location].getKey().equals(new_key)){
                V old_value=array[location].getValue();
                array[location].setValue(new_value);
                return old_value;
            }

            //move to next location
            location=(location+1)% array.length;
        }

        /*
        if here, the slot for new_key was occupied, but new_key did not exist in array previously;
        put new key-value pair in array and count it as a collision
         */
        array[location]=new KeyValuePair<>(new_key,new_value);
        beenUsed[location]=true;
        numItems++;
        numCollisions++;
        return null;
    }

    public KeyValuePair lastAdded(){
        return lastAdded;
    }

    private void ensureCapacity() {
        /*
        if more at least half of the seats in array are full, double its size and
        copy over all elements
         */

        if(numItems < array.length/2){
            return;
        }

        //save current array
        KeyValuePair<K,V>[] oldArray=array.clone();

        //initiate arrays to two empty arrays double the size of the previous ones
        array=(KeyValuePair<K, V>[]) Array.newInstance(KeyValuePair.class,oldArray.length*2);
        beenUsed=new boolean[oldArray.length*2];

        //update tracker fields
        numItems=0;
        numCollisions=0;

        //copy over all elements
        for(KeyValuePair<K,V> item:oldArray){
            if(item!=null){
                this.put(item.getKey(),item.getValue());
            }
        }
    }

    public int hash(K key){
        /*
        returns a hashcode that can used as index of this.array;
        special case: key is of type Vertex (modification for Project 10: Hunt the Wumpus)
         */


        if(key.getClass()==Vertex.class){
            Vertex vKey=(Vertex)key;

            int positionHash=vKey.getX()*10+vKey.getY();

            return Math.abs(positionHash)% array.length;
        }

        return Math.abs(key.hashCode())% array.length;
    }

    public boolean containsKey(K key) {
        //returns true if parameter key is in this.array
        return get(key)!=null;
    }

    public V get(K key) {
        //returns value corresponding to parameter key or null

        //make hashcode for parameter key
        int location=hash(key);

        //if array slot at location is null, return null
        if(array[location]==null){
            return null;
        }

        //if array slot at location contains parameter key, return value at location
        if(array[location].getKey().equals(key)){
            return array[location].getValue();
        }

        /*
        if here, array slot at location is full, but key does not correspond to parameter;
        start linear probing to look for parameter key
         */
        while (beenUsed[location]==true){
            //if key is equal to parameter key, return value
            if(array[location]!=null&& array[location].getKey().equals(key)){
                return array[location].getValue();
            }

            //move to next slot
            location=(location+1)% array.length;
        }

        //if here, key is not found in array
        return null;
    }

    public ArrayList<K> keySet() {
        /*
        returns an ArrayList with all keys in ClosedHashmap
         */

        ArrayList<K> kSet=new ArrayList<>();

        //loop through each array slot
        for (int i = 0; i < array.length;i++) {
            //skip null slots
            if(array[i]==null){
                continue;
            }

            kSet.add(array[i].getKey());
        }

        return kSet;
    }

    public ArrayList<V> values() {
        /*
        returns an ArrayList with all values in ClosedHashmap
         */

        ArrayList<V> vSet=new ArrayList<>();

        //loop through each array slot
        for (int i = 0; i < array.length;i++) {
            //skip null slots
            if(array[i]==null){
                continue;
            }

            vSet.add(array[i].getValue());
        }

        return vSet;
    }

    public ArrayList<KeyValuePair<K, V>> entrySet() {
        /*
        returns an ArrayList with all key-value pairs in ClosedHashmap
         */

        ArrayList<KeyValuePair<K,V>> eSet=new ArrayList<>();

        //loop through each array slot
        for (int i = 0; i < array.length;i++) {
            //skip null slots
            if(array[i]==null){
                continue;
            }

            eSet.add(array[i]);
        }

        return eSet;
    }

    public int size() {
        //getter for numItems
        return numItems;
    }

    public void clear() {
        /*
        deletes all references in ClosedHashmap and resets numItems, numCollisions,
        and beenUsed
         */

        for (int i = 0; i < array.length; i++) {
            array[i]=null;
        }

        numItems=0;
        numCollisions=0;
        beenUsed=new boolean[array.length];
    }

    public V remove(K key){
        /*
        removes key-value pair with parameter key and returns its value
         */

        //make array index
        int location=hash(key);

        //if slot at location is empty and it hasn't been used, key not in array
        if(array[location]==null&& beenUsed[location]==false){
            return null;
        }

        //if the key at location equals parameter key
        if(array[location].getKey().equals(key)){
            //save value
            V value=array[location].getValue();

            //set array slot at location to null
            array[location]=null;

            //housekeeping
            numItems--;
            return value;
        }

        //start searching with linear probing
        while (beenUsed[location]==true){
            //if key was found, it is not at hash index -> it introduced a collision when it was put
            if(array[location]!=null&& array[location].getKey().equals(key)){
                //save value
                V value=array[location].getValue();

                //set array slot at location to null
                array[location]=null;

                //housekeeping
                numItems--;
                numCollisions--;
                return value;
            }

            //move to next slot
            location=(location+1)% array.length;
        }

        //key not found
        return null;
    }

    @Override
    public String toString() {
        StringBuilder builder=new StringBuilder();

        builder.append("|-----------------------------------------------------------|\n");
        for (int i = 0; i < this.keySet().size() - 1; i++) {
            builder.append(keySet().get(i));
            builder.append(" hash: ");
            builder.append(hash(keySet().get(i)));
            builder.append("\n");
        }
        builder.append(keySet().get(keySet().size()-1));
        builder.append("\n|-----------------------------------------------------------|\n");

        return builder.toString();
    }

    public static void main(String[] args) {

    }
}
