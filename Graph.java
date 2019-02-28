/*
    Marius Orehovschi
    F18
    Project 10: Hunt the Wumpus
    CS 231
*/

/**
 * Graph with vertices;
 * can calculate shortest distance from one vertex to all others
 */

import java.awt.*;
import java.util.*;

/**
 * Graph implementation with additional methods for a game of
 * Hunt The Wumpus
 */

public class Graph {
    //hash map that stores vertices according to position as vertex, vertex (key=value)
    private ClosedHashmap<Vertex,Vertex> vertexPositions;

    //reference to the hunter's location
    private Vertex hunter;

    //helper booleans for game end assessment
    private boolean endGame;
    private boolean won;

    public Graph(){
        //initialize ArrayList
        vertexPositions =new ClosedHashmap<>();
    }

    //getters
    public boolean endGame() {
        return endGame;
    }
    public boolean won() {
        return won;
    }
    public ArrayList<Vertex> getVertices() {
        return vertexPositions.keySet();
    }
    public Vertex getHunter() {
        return hunter;
    }
    public int vertexCount(){
        //return number of vertices in list
        return vertexPositions.size();
    }
    public ClosedHashmap<Vertex, Vertex> getVertexPositions() {
        return vertexPositions;
    }
    public int getHorizontalSize(){
        return maxDir()[Vertex.Direction.EAST.ordinal()]+
                maxDir()[Vertex.Direction.WEST.ordinal()]+1;
    }
    public int getVerticalSize(){
        return maxDir()[Vertex.Direction.NORTH.ordinal()]+
                maxDir()[Vertex.Direction.SOUTH.ordinal()]+1;
    }//setters

    public void addEdge(Vertex v1, Vertex.Direction dir, Vertex v2){
        //adds a bi-directional connection between parameter vertices

        //add v1 if not already in list
        if(!vertexPositions.containsKey(v1)){
            vertexPositions.put(v1,v1);
        }

        //add v2 if not already in list
        if(!vertexPositions.containsKey(v2)){
            vertexPositions.put(v2,v2);
        }

        //connect vertices
        v1.connect(v2,dir);
        v2.connect(v1,Vertex.opposite(dir));
    }

    public void grow(Vertex v0, Vertex.Direction dir){
        /*
        grows the graph by one vertex at parameter connexion vertex in
        parameter direction
         */

        //if graph is empty, add a new Vertex with coordinates 0,0
        if(vertexCount()==0){
            Vertex initial=new Vertex(0,0);
            vertexPositions.put(initial,initial);
            return;
        }

        //figure out the new x-coordinate, depending on parameter direction
        int newX=((dir==Vertex.Direction.EAST)? v0.getX()+1:v0.getX());
        newX=((dir==Vertex.Direction.WEST)? v0.getX()-1:newX);

        //figure out the new x-coordinate, depending on parameter direction
        int newY=((dir==Vertex.Direction.NORTH)? v0.getY()-1:v0.getY());
        newY=((dir== Vertex.Direction.SOUTH)? v0.getY()+1:newY);

        /*
        find out if vertex with new coordinates is in hash map;
        method returns null if not
         */
        Vertex newVertex = vertexPositions.get(new Vertex(newX,newY));

        //if vertex is already in map
        if(newVertex != null){
            //get hash map reference of vertex
            v0 = vertexPositions.get(v0);

            //connect new vertex to connection vertex
            v0.connect(newVertex,dir);
            newVertex.connect(v0,Vertex.opposite(dir));

            //put updated vertices in hash map
            vertexPositions.put(v0,v0);
            vertexPositions.put(newVertex,newVertex);

            return;
        }

        //if here, vertex with new coordinates is not already in hash map; make a new one
        newVertex=new Vertex(newX,newY);

        //connect new vertex with connection vertex
        v0.connect(newVertex,dir);
        newVertex.connect(v0,Vertex.opposite(dir));

        //put updated vertices in hash map
        vertexPositions.put(v0,v0);
        vertexPositions.put(newVertex,newVertex);
    }

    public void growSquarely(int side){
        //make a square-shaped graph with parameter side length

        vertexPositions.clear();

        //iterate side^2 times
        for (int y = 0; y <side; y++) {
            for (int x = 0; x <side; x++) {
                //make new vertex and put it in hash map
                Vertex addable=new Vertex(x,y);
                vertexPositions.put(addable,addable);

                //connect all vertices not in first column to western neighbor
                if(x!=0){
                    addEdge(addable, Vertex.Direction.WEST, vertexPositions.get(new Vertex(x-1,y)));
                }

                //connect all vertices not in first row to northern neighbor
                if(y!=0){
                    addEdge(addable, Vertex.Direction.NORTH, vertexPositions.get(new Vertex(x,y-1)));
                }
            }
        }
    }

    public void growRandomly(int targetSize){
        //grow in a random direction

        Random randy=new Random();

        //make size up to 1.5 times lerger than parameter
        targetSize=targetSize+randy.nextInt(targetSize/2);

        //add first vertex
        grow(null,null);

        //safety against infinite loops
        int iterationBound=0;

        //while not enough vertices
        while (vertexCount()<targetSize){
            //vertex that the new one will be connected to
            Vertex last= vertexPositions.getLastAdded().getValue();

            //make list of directions
            ArrayList<Vertex.Direction> directions=new ArrayList<>();

            //4 times
            for (int i = 0; i < 4; i++) {
                //arbitrarily grow or not grow in direction i
                if(randy.nextBoolean()){
                    directions.add(Vertex.Direction.values()[i]);
                }
            }

            //attache new vertex to connection vertex
            for(Vertex.Direction direction:directions){
                grow(last,direction);
                iterationBound++;
            }
            iterationBound++;

            if(iterationBound>100){
                break;
            }
        }
    }

    public void addWumpus() {
        /*
        choose a random location and run Dijkstra's algorithm;
        the presence of wumpus or lack thereof is defined by cost==0
         */

        Random randy=new Random();

        Vertex location= vertexPositions.keySet().get(randy.nextInt(vertexPositions.size()));
        vertexPositions.put(location,location);

        shortestPath(location);
    }

    public void addHunter(){
        //add hunter at a random location

        //choose random location
        Random randy=new Random();
        Vertex location= vertexPositions.keySet().get(randy.nextInt(vertexPositions.size()));
        int cost=location.getCost();

        /*
        keep updating the location randomly until cost < 3
        i.e. wumpus is more than 2 blocks away
         */
        while (cost<3){
            location= vertexPositions.keySet().get(randy.nextInt(vertexPositions.size()));
            cost=location.getCost();
        }

        //set hunter reference to chosen location
        location.setContainsHunter(true);
        hunter=location;
    }

    public void moveHunter(Vertex.Direction dir){
        //move hunter by changing the hunter reference

        //if game has ended, unable to move
        if(endGame){
            return;
        }

        //move north
        if(dir == Vertex.Direction.NORTH && hunter.getNeighborArray()[Vertex.Direction.NORTH.ordinal()]!=null){
            Vertex destination = vertexPositions.get(new Vertex(hunter.getX(),hunter.getY()-1));

            hunter.setContainsHunter(false);
            destination.setContainsHunter(true);
            hunter=destination;
        }

        //move east
        if(dir == Vertex.Direction.EAST && hunter.getNeighborArray()[Vertex.Direction.EAST.ordinal()]!=null){
            Vertex destination= vertexPositions.get(new Vertex(hunter.getX()+1,hunter.getY()));

            hunter.setContainsHunter(false);
            destination.setContainsHunter(true);
            hunter=destination;
        }

        //move south
        if(dir == Vertex.Direction.SOUTH && hunter.getNeighborArray()[Vertex.Direction.SOUTH.ordinal()]!=null){
            Vertex destination= vertexPositions.get(new Vertex(hunter.getX(),hunter.getY()+1));

            hunter.setContainsHunter(false);
            destination.setContainsHunter(true);
            hunter=destination;
        }

        //move west
        if(dir == Vertex.Direction.WEST && hunter.getNeighborArray()[Vertex.Direction.WEST.ordinal()]!=null){
            Vertex destination= vertexPositions.get(new Vertex(hunter.getX()-1,hunter.getY()));

            hunter.setContainsHunter(false);
            destination.setContainsHunter(true);
            hunter=destination;
        }

        //helper method that assesses whether game has ended
        checkEndGame();
    }

    private void checkEndGame() {
        //helper method that assesses whether game has ended

        //get a list of all vertex costs
        ArrayList<Integer> vertexCostList=new ArrayList<>();
        for(Vertex v: vertexPositions.keySet()){
            vertexCostList.add(v.getCost());
        }

        /*
        if list does not contain 0, player wins
        (the cost of the wumpus vertex is set to 0 when hunter
        succeeds to defeat it
         */
        if(!vertexCostList.contains(0)){
            endGame=true;
            won=true;
            return;
        }

        //if list does contain 0, player has lost
        if(hunter.getCost()==0){
            endGame=true;
        }
    }

    public void shortestPath(Vertex v0){
        /*
        Dijkstra's algorithm implementation that sets all vertices to visited
        and calculates distance from parameter vertex to all others
         */
        if(vertexCount()<2){
            return;
        }

        //refresh all vertices
        for(Vertex v: vertexPositions.keySet()){
            v.setMarked(false);
            v.setCost(Integer.MAX_VALUE);
            vertexPositions.put(v,v);
        }

        PriorityQueue<Vertex> pq=new PriorityQueue<>();

        //start with parameter vertex
        v0.setCost(0);
        pq.add(v0);

        //as long as there are vertices in the queue
        while (!pq.isEmpty()){
            //process closest vertex
            Vertex v=pq.remove();
            //set to visited
            v.setMarked(true);

            //iterate over the neighbors of vertex
            for(Vertex w:v.getNeighbors()){
                //update cost if necessary and add neighbor to queue
                if(v.getCost()+1<w.getCost() && !w.isMarked()){
                    w.setCost(v.getCost()+1);
                    pq.add(w);
                }
            }
        }
    }//end shortest path

    public int[] maxDir(){
        /*
        returns an array of size 4 that has the maximum number of vertices
        in each of the cardinal directions
         */

        int[] maxDir=new int[]{0,0,0,0};

        for(Vertex vertex:getVertices()){
            //if x is less than max west
            if(vertex.getX()<0 && vertex.getX()< -maxDir[3]){
                //update max west
                maxDir[3]= -vertex.getX();
            }

            //if x is less than max west
            if(vertex.getX()>0 && vertex.getX()>maxDir[1]){
                //update max west
                maxDir[1]=vertex.getX();
            }

            //if x is less than max west
            if(vertex.getY()<0 && vertex.getY()< -maxDir[0]){
                //update max west
                maxDir[0]= -vertex.getY();
            }

            //if x is less than max west
            if(vertex.getY()>0 && vertex.getY()>maxDir[2]){
                //update max west
                maxDir[2]=vertex.getY();
            }
        }

        return maxDir;
    }

    public void draw(Graphics g,int x,int y,int scale){
        //draws graph on parameter Graphics object

        //if hunter has stepped into vertex with wumpus
        if(hunter.getCost()==0){
            reveal();
        }

        //draw graph recursively starting at vertex that was added first
        vertexPositions.get(new Vertex(0,0)).draw(g,x,y,scale,new ClosedHashmap<>());
    }

    public void shoot(Vertex.Direction dir) {
        //shoot the wumpus

        //find out target vertex according to parameter direction
        Vertex target=hunter.getNeighborArray()[dir.ordinal()];

        //if target does not exist in graph, do not do anything
        if(target==null){
            return;
        }

        /*
        if target is equal to vertex containing wumpus
        (wumpus location defined by cost ==0)
         */
        if(target.getCost()==0) {
            /*
            eliminate wumpus by reassigning the cost of the only
            vertex with cost 0
             */
            target.setCost(1);
            vertexPositions.put(target, target);

            won = true;
        }

        endGame=true;
        //draw hidden vertices
        reveal();
    }

    public void reveal() {
        //draws all vertices

        //iterate over vertices and draw eacf one
        for (int i = 0; i < vertexPositions.size(); i++) {
            Vertex vertex= vertexPositions.keySet().get(i);
            vertex.setVisible(true);

            //update vertex and put it back in hash map
            vertexPositions.put(vertex,vertex);
        }
    }

    public static String arrayString(Object[] source){
        /*
        helper method for debugging;
        returns String with all array elements
         */

        StringBuilder builder=new StringBuilder();

        builder.append("[");

        for (int i = 0; i < source.length - 1; i++) {
            builder.append(source[i]);
            builder.append(", ");
        }
        builder.append(source[source.length-1]);
        builder.append("]");

        return builder.toString();
    }

    public static void main(String[] args) {
        //test code for Graph class

        Graph g=new Graph();

        g.grow(new Vertex("a"),null);

        g.grow(g.vertexPositions.get(new Vertex(0,0)), Vertex.Direction.EAST);

        Random randy=new Random();
        Vertex random=new Vertex(randy.nextInt(g.getHorizontalSize()),randy.nextInt(g.getVerticalSize()));

        //run Dijkstra's algorithm starting at vertex with index 0
        g.shortestPath(g.vertexPositions.get(new Vertex(0,0)));

        System.out.println(g.vertexPositions.keySet().get(3).getCost());
    }
}
