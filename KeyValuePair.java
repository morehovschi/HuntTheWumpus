/*
    Marius Orehovschi
    F18
    Project 8: Hash Tables
    CS 231
*/

/**
 * Data structure that stores pairs of two values that can be of different types
 * @param <Key> generic type for key
 * @param <Value> generic type for value
 */

public class KeyValuePair<Key,Value> {
    private Key key;
    private Value value;

    public KeyValuePair(Key k,Value v){
        //constructor that takes the key and the value
        key=k;
        value=v;
    }

    //getters
    public Key getKey() {
        return key;
    }
    public Value getValue() {
        return value;
    }
    //end of getters

    //setters
    public void setValue(Value value) {
        this.value = value;
    }
    //end of setters


    @Override
    public String toString() {
        //returns String representation of key-value pair
        StringBuilder builder=new StringBuilder();

        builder.append(key);
        builder.append(":");
        builder.append(value);

        return builder.toString();
    }

    public static void main(String[] args) {
        /*
        test code for KeyValuePair
         */

        KeyValuePair<String,Integer> keegan=new KeyValuePair<>("blah",1);

        KeyValuePair<String,String> valerie=new KeyValuePair<>("blah","blah");

        System.out.println(keegan);
        System.out.println(valerie);

        valerie.setValue(""+1);

        System.out.println(keegan);
        System.out.println(valerie);

        System.out.println(keegan.getKey());
        System.out.println(valerie.getValue());
    }
}
