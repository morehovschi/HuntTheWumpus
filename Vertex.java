/*
    Marius Orehovschi
    F18
    Project 10: Hunt the Wumpus
    CS 231
*/

/**
 * Building block of a graph
 */

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/**
 * Building block for Graph class
 */

public class Vertex implements Comparable<Vertex> {
    private Vertex[] neighbors;
    private int[] position;

    //distance from a specific vertex
    private int cost;

    //whether or not this has been visited in an iteration of Dijkstra's algorithm
    private boolean marked;

    //unique identifier
    private String label;

    private boolean visible;

    private boolean containsHunter;

    private Direction shootingDirection;

    public Vertex(String label){
        //constructor that sets label field to parameter label and position to 0,0

        //4 neighbors in array, 1 for each cardinal direction (N,E,S,W)
        neighbors=new Vertex[4];
        cost=Integer.MAX_VALUE;//i.e. infinite

        this.label=label;
        this.position=new int[]{0,0};
    }

    public Vertex(String label,int x,int y){
        /*
        constructor that sets label and position to
        parameter values
         */

        this(label);
        this.position[0]=x;
        this.position[1]=y;
    }

    public Vertex(int x,int y){
        //constructor that sets label position to 0,0 and label to random string

        this(random4CharacterString(),x,y);
    }

    private static String random4CharacterString() {
        //helper method that returns a random 4-character String

        Random randy=new Random();
        StringBuilder builder=new StringBuilder();

        //repeat 4 times
        for (int i = 0; i < 4; i++) {
            //add a random letter
            builder.append((char)('a'+randy.nextInt(26)));
        }

        return builder.toString();
    }

    //start of getters
    public int getCost() {
        return cost;
    }
    public boolean isMarked() {
        return marked;
    }
    public String getLabel(){
        return label;
    }
    public int getX(){
        return position[0];
    }
    public int getY(){
        return position[1];
    }
    public int[] getPosition() {
        return position;
    }
    public Vertex[] getNeighborArray(){
        return neighbors;
    }//end of getters

    //start of setters
    public void setNeighbors(Vertex[] neighbors) {
        this.neighbors = neighbors;
    }
    public void setCost(int cost) {
        this.cost = cost;
    }
    public void setMarked(boolean marked) {
        this.marked = marked;
    }
    public void setContainsHunter(boolean containsHunter) {
        this.containsHunter = containsHunter;
    }
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    public void setShootingDirection(Direction d){
        shootingDirection=Direction.NORTH;
    }
    //end of setters


    public void connect(Vertex other, Direction d){
        //connects this to another vertex in parameter direction

        neighbors[d.ordinal()]=other;
    }

    public Vertex getNeighbor(Direction d){
        //returns neighbor in parameter direction

        return neighbors[d.ordinal()];
    }

    public ArrayList<Vertex> getNeighbors(){
        //returns a list of all neighbors (non-null slots in array)

        ArrayList<Vertex> returnable=new ArrayList<>();//helper

        //check every slot in neighbor array
        for(Vertex v:neighbors){
            //if slot not null, add it to helper list
            if(v!=null){
                returnable.add(v);
            }
        }

        return returnable;
    }

    @Override
    public int compareTo(Vertex o) {
        //returns difference between this vertex's cost and parameter vertex's

        return this.cost - o.getCost();
    }


    public void draw(Graphics g,int x,int y,int scale,ClosedHashmap<Vertex,Vertex> visited){
        /*
        draw vertex on Graphics object;
        parameter visited -- hash map of visited vertices
         */

        if(containsHunter){
            visible=true;
        }

        //save initial color
        Color init=g.getColor();

        //color in red any vertex that is within 2 blocks of wumpus
        if(cost<3){

            g.setColor(new Color(250,0,10));
        }

        //if cost==0, wumpus is at this vertex; draw wumpus
        if(cost==0 && visible){
            g.fillOval((int)(x+scale*0.25),(int)(y+scale*0.25),(int)(scale*0.5),(int)(scale*0.5));
        }

        //draw the vertex if visible
        if(visible){g.drawRect(x+2,y+2,scale-4,scale-4);}

        //restore initial color
        g.setColor(init);

        //draw hunter if at this vertex
        if(containsHunter){
            g.fillOval((int) (x + scale * 0.4), (int) (y + scale * 0.4), (int) (scale * 0.2), (int) (scale * 0.2));
        }

        //add current vertex to hash map of visited vertices
        visited.put(this,this);

        /*
        recursively call draw of each neighbor vertex
         */

        if(neighbors[Direction.NORTH.ordinal()]!=null){
            //draw passages between any neighboring vertices if visible
            if (visible)
                g.fillRect((int)(x+scale*0.3),(int)(y-scale*0.025),(int)(scale*0.4),(int)(scale*0.1));

            if(!visited.containsKey(neighbors[Direction.NORTH.ordinal()])) {
                neighbors[Direction.NORTH.ordinal()].draw(g, x, y - scale, scale, visited);
            }
        }

        if(neighbors[Direction.EAST.ordinal()]!=null){
            //draw passages between any neighboring vertices if visible
            if (visible)
                g.fillRect((int)(x+scale*0.925),(int)(y+scale*0.3),(int)(scale*0.1),(int)(scale*0.4));

            if(!visited.containsKey(neighbors[Direction.EAST.ordinal()])) {
                neighbors[Direction.EAST.ordinal()].draw(g, x+scale, y, scale, visited);
            }
        }

        if(neighbors[Direction.SOUTH.ordinal()]!=null){
            //draw passages between any neighboring vertices if visible
            if (visible)
                g.fillRect((int)(x+scale*0.3),(int)(y+scale*0.925),(int)(scale*0.4),(int)(scale*0.1));

            if(!visited.containsKey(neighbors[Direction.SOUTH.ordinal()])) {
                neighbors[Direction.SOUTH.ordinal()].draw(g, x, y + scale, scale, visited);
            }
        }

        if(neighbors[Direction.WEST.ordinal()]!=null){
            //draw passages between any neighboring vertices if visible
            if (visible)
                g.fillRect((int)(x-scale*0.025),(int)(y+scale*0.3),(int)(scale*0.1),(int)(scale*0.4));

            if(!visited.containsKey(neighbors[Direction.WEST.ordinal()])) {
                neighbors[Direction.WEST.ordinal()].draw(g, x-scale, y, scale, visited);
            }
        }

    }

    @Override
    public String toString() {
        //returns String representation of this vertex

        StringBuilder builder=new StringBuilder();

        //add delimitation and this vertex's unique name
        builder.append("{ ");
        builder.append(this.getLabel());

        //add neighbors
        builder.append("( ");
        for (int i = 0; i < neighbors.length-1; i++) {
            if(neighbors[i]!=null){
                builder.append(neighbors[i].getLabel());
                builder.append(", ");
            } else {
                builder.append(" , ");
            }
        }
        if(neighbors[neighbors.length-1]!=null){
            builder.append(neighbors[neighbors.length-1].getLabel());
            builder.append(" )");
        } else {
            builder.append(" )");
        }//done adding neighbors

        builder.append(" cost: ");
        builder.append(cost);

        builder.append(", marked: ");
        builder.append(marked);

        builder.append(" }");

        return builder.toString();
    }

    @Override
    public boolean equals(Object o) {

        if(this.getClass()!=o.getClass()){
            return false;
        }

        Vertex other=(Vertex)o;
        if(this.getX()!=other.getX() || this.getY()!=other.getY()){
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        //hash code based on vertex position
        return getX()*100+getY();
    }

    public static void main(String[] args) {
        //test method for the Vertex class

        Vertex v1=new Vertex(0,0);
        Vertex v2=new Vertex(0,0);

        v1.connect(v2,Direction.NORTH);

        System.out.println(v1);
    }




    //helper enum and related methods
    public enum Direction {NORTH,EAST,SOUTH,WEST}

    public static Direction opposite(Direction d){
        //returns direction opposite of parameter direction

        //index the slot 2 to the right (in a circular array)
        int idx=(d.ordinal()+2)%Direction.values().length;
        return Direction.values()[idx];
    }
    public static void testOpposite(){
        //interactive test method for opposite()

        Scanner reader=new Scanner(System.in);

        System.out.println("Direction: ");
        String line=reader.nextLine();

        System.out.println("Opposite: "+opposite(Direction.valueOf(line)));
    }//end of enum-related methods
}
