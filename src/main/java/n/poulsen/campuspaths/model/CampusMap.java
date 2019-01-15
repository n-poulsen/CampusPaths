package n.poulsen.campuspaths.model;

import n.poulsen.campuspaths.model.DLMGraph.*;
import java.util.*;

import static n.poulsen.campuspaths.model.DLMGraph.dijkstra;
import static n.poulsen.campuspaths.model.DataParser.parseBuildingData;
import static n.poulsen.campuspaths.model.DataParser.parsePathData;

/**
 * <b>Campus</b> represents a campus's map, through its
 * buildings and paths.
 */
public class CampusMap {

    /** When TRUE, this variable enables checkReps() at the beginning and end of every method*/
    private final static boolean DEBUGGING = false;

    /** A DLMGraph representing all campus paths */
    private DLMGraph<Coordinates, Double> campusMap;

    /** Maps building's abbreviated name to their other attributes */
    private Map<String, Building> buildings;

    // Abstraction function:
    //    CampusMap m represents a campus map. All buildings in the map have their abbreviated name as a key of the buildings map,
    //    and the node representing the building is buildings.get(shortName), and also a node in campusMap. The full name of all
    //    buildings is the value associated to the key that is the abbreviated name of the building in shortToLongName.
    //
    //
    // Representation invariant for every DLMGraph g:
    //    campusMap != null &&
    //    buildings != null &&
    //    forall DEdge e in campusMap: e.getLabel() > 0
    //    forall (s, b) in buildings: s != null && b != null
    //    forall (s, b) in buildings: campusMap.contains(b.location)
    //

    /** @spec.effects Constructs a new empty campus map */
    public CampusMap(){
        campusMap = new DLMGraph<>();
        buildings = new HashMap<>();
        checkRep();
    }

    /**
     * Reads the campus buildings dataset. Each line of the input file contains the building's abbreviated
     * name, followed by the buildings full name, followed by a rational value for the building's x coordinate
     * and one for the building's y coordinate, all separated by tabs. The first line of the file contains
     * information on the data in each t-s-v, and hence is ignored.
     *
     * @param filePath the file that will be read
     * @spec.requires filePath is a valid file path
     * @spec.requires filePath != null
     * @spec.effects adds all buildings listed in the given file to this campus map
     * @spec.modifies this
     * @throws IllegalArgumentException if the data in the file given is not correctly formatted
     */
    public void loadBuildingData(String filePath){
        checkRep();
        try{
            List<Building> allB = parseBuildingData(filePath);
            for (Building b: allB){
                if (!contains(b.shortName)){
                    addBuilding(b);
                }
            }
        }catch (DataParser.MalformedDataException e){
            throw new IllegalArgumentException("Unable to parse data: not in correct format", e);
        }
        checkRep();
    }

    /**
     * Reads the campus paths dataset, from a TSV file. Each line of the input file contains the path's
     * origin coordinates, as two rational values seperated by a comma, the path's destination coordinates
     * in the same format, and the path's distance, all separated by tabs. The first line of the file
     * contains information on the data in each t-s-v, and hence is ignored.
     *
     * @param filePath the path to the file that will be read
     * @spec.requires filePath != null
     * @spec.requires filePath is a valid file path
     * @spec.effects adds all paths listed in the given file to this campus map
     * @spec.modifies this
     * @throws IllegalArgumentException if the data in the file given is not correctly formatted
     */
    public void loadPathData(String filePath){
        checkRep();
        try{
            List<Path> allP = parsePathData(filePath);
            for (Path p: allP){
                addPath(p);
            }
        }catch (DataParser.MalformedDataException e){
            throw new IllegalArgumentException("Unable to parse data: not in correct format", e);
        }
        checkRep();
    }

    /**
     * Adds a building to this campus map
     *
     * @param b the building to add to this CampusMap
     * @spec.requires b != null
     * @spec.requires there is no building with abbreviated name shortName in this CampusMap yet.
     * @spec.effects Adds the Building b to this campus map
     * @spec.modifies this
     */
    public void addBuilding(Building b){
        checkRep();
        if (b == null){
            throw new NullPointerException("Null argument");
        }
        Node<Coordinates> n = new Node<>(b.location);
        buildings.put(b.shortName, b);
        campusMap.addNode(n);
        checkRep();
    }

    /**
     * Adds a path to this campus map
     *
     * @param p the path to add to this
     * @spec.requires p != null
     * @spec.effects Adds the Path p to this campus map
     * @spec.modifies this
     */
    public void addPath(Path p){
        checkRep();
        Node<Coordinates> s = new Node<>(p.getOrigin());
        if (!campusMap.contains(s)) {
            campusMap.addNode(s);
        }
        Node<Coordinates> d = new Node<>(p.getDestination());
        if (!campusMap.contains(d)) {
            campusMap.addNode(d);
        }
        campusMap.addEdge(new DEdge<>(s, d, p.distance));
        campusMap.addEdge(new DEdge<>(d, s, p.distance));
        checkRep();
    }

    /**
     * Returns true iff the specified building has been added to this campus map
     *
     * @param b the building's abbreviated name, for which we want to know if it has been added
     * @spec.requires b != null
     * @return true iff the b has been added to this campus map
     */
    public boolean contains(String b){
        checkRep();
        return buildings.containsKey(b);
    }

    /**
     * Returns the building associated to the given abbreviated name if it exists in this map, or
     * null if no building in this map has the given abbreviated string
     *
     * @param b the abbreviated name for which we want the associated building
     * @spec.requires b != null
     * @return the building in this map with abbreviated name b, or null if none exists
     */
    public Building getBuilding(String b){
        checkRep();
        return buildings.get(b);
    }

    /**
     * Returns a list of all buildings in this map
     *
     * @return a list of all buildings in this map
     */
    public List<Building> listBuildings(){
        checkRep();
        List<Building> result = new ArrayList<>();
        for (String s: buildings.keySet()){
            result.add(buildings.get(s));
        }
        checkRep();
        return result;
    }

    /**
     * Returns the shortest path between two buildings in this campus map
     *
     * @param b1 the building at which the path starts abbreviated name
     * @param b2 the building at which the path ends abbreviated name
     * @spec.requires b1 != null
     * @spec.requires b2 != null
     * @spec.requires this.contains(b1)
     * @spec.requires this.contains(b2)
     * @return the shortest path between b1 and b2, or null if no path exists or one of the buildings isn't in the map
     */
    public List<Path> shortestPath(String b1, String b2){
        checkRep();
        if (b1 == null || b2 == null){
            throw new NullPointerException();
        }
        Building start = buildings.get(b1);
        Building dest = buildings.get(b2);
        // If either the start or dest isn't a building in our map, return null
        if (start == null || dest == null){
            return null;
        }
        List<DEdge<Coordinates, Double>> path = dijkstra(campusMap, new Node<>(start.location), new Node<>(dest.location));
        if (path == null){
            return null;
        }
        List<Path> result = new ArrayList<>();
        for(DEdge<Coordinates, Double> e: path){
            result.add(new Path(e.getParentNode().getLabel(), e.getChildNode().getLabel(), e.getLabel()));
        }
        checkRep();
        return result;
    }

    /** Checks that the representation invariant holds (if any). */
    private void checkRep(){
        assert(campusMap != null);
        assert(buildings != null);
        if (DEBUGGING){
            for (String b: buildings.keySet()){
                assert(b != null);
                assert(buildings.get(b) != null);
                assert(campusMap.contains(new Node<>(buildings.get(b).location)));
            }
            for (DEdge<Coordinates, Double> e: campusMap.getEdges()){
                assert(e.getLabel() > 0);
            }
        }
    }

    /**
     * <b>Building</b> is an immutable representation of a building on campus, through its
     * long name, abbreviated name, and location .
     *
     * <b>Specification fields</b>:
     *   @spec.specfield longName: the building's full name
     *   @spec.specfield shortName: the building's abbreviated name
     *   @spec.specfield location: the building's location
     *
     * <b>Abstract invariant</b>:
     *   Two buildings are equal if they are at the same location, have the same
     *   abbreviated name and the same full name.
     */
    public static class Building{

        /** This building's abbreviated name */
        private final String shortName;

        /** This building's full name */
        private final String longName;

        /** This building's location */
        private final Coordinates location;

        // Abstraction function:
        //    Building b represents a building named longName, which can be abbreviated shortName,
        //    on campus at location location.
        //
        // Representation invariant for every Building b:
        //    b.location != null &&
        //    b.shortName != null &&
        //    b.longName != null
        //

        /**
         * Constructs a new Building
         *
         * @param shortName this building's abbreviated name
         * @param longName this building's full name
         * @param location this building's location
         * @spec.requires shortName != null
         * @spec.requires longName != null
         * @spec.requires location != null
         * @spec.effects Constructs a new Building with full name longName, abbreviated name shortName and location location
         */
        public Building(String shortName, String longName, Coordinates location){
            this.shortName = shortName;
            this.longName = longName;
            this.location = location;
            checkRep();
        }

        /**
         * Returns this building's location
         *
         * @return this building's location
         */
        public Coordinates getLocation(){
            checkRep();
            return location;
        }

        /**
         * Returns this building's abbreviated name
         *
         * @return this building's abbreviated name
         */
        public String getShortName() {
            checkRep();
            return shortName;
        }

        /**
         * Returns this building's full name
         *
         * @return this building's full name
         */
        public String getLongName() {
            checkRep();
            return longName;
        }

        /**
         * Standard equality operation.
         *
         * @param obj The object to be compared for equality.
         * @return true iff 'obj' is an instance of a Path and 'this' and 'obj' represent
         * the same path.
         */
        @Override
        public boolean equals(Object obj) {
            checkRep();
            if (obj instanceof Building){
                Building b = (Building) obj;
                return b.shortName.equals(this.shortName) && b.longName.equals(this.longName) && b.location.equals(this.location);
            }
            checkRep();
            return false;
        }

        /**
         * Standard hashCode function.
         *
         * @return an int that all objects equal to this will also return.
         */
        @Override
        public int hashCode() {
            checkRep();
            return Objects.hash(shortName, longName, location);
        }

        /**
         * Checks that the representation invariant holds (if any).
         */
        private void checkRep() {
            assert(shortName != null);
            assert(longName != null);
            assert(location != null);
        }


    }

    /**
     * <b>Path</b> is an immutable representation of a path on campus.
     * Each path is between two coordinates on campus, and has a distance.
     *
     * <b>Specification fields</b>:
     *   @spec.specfield origin: the path's starting point
     *   @spec.specfield destination: the path's end point
     *   @spec.specfield distance: the path's length
     *
     * <b>Abstract invariant</b>:
     *   Two paths are equal if they have the same origin, destination and distance.
     */
    public static class Path {

        /** This path's starting point */
        private final Coordinates origin;

        /** This path's end point */
        private final Coordinates destination;

        /** This path's length */
        private final double distance;

        // Abstraction function:
        //    Path p represents a path between origin and destination,
        //    of length distance.
        //
        // Representation invariant for every path p:
        //    p.origin != null &&
        //    p.destination != null &&
        //    p.distance > 0
        //

        /**
         * Constructs a new Path
         *
         * @param origin The location at which the path starts
         * @param destination The location at which the path starts
         * @param distance the length of the path
         * @spec.requires origin != null
         * @spec.requires destination != null
         * @spec.requires distance greater than 0
         * @spec.effects Constructs a new Path from Origin to destination of length distance
         */
        public Path(Coordinates origin, Coordinates destination, double distance){
            this.origin = origin;
            this.destination = destination;
            this.distance = distance;
            checkRep();
        }

        /**
         * Returns this paths origin
         *
         * @return this paths origin
         */
        public Coordinates getOrigin() {
            checkRep();
            return origin;
        }

        /**
         * Returns this paths destination
         *
         * @return this paths destination
         */
        public Coordinates getDestination() {
            checkRep();
            return destination;
        }

        /**
         * Returns this paths length
         *
         * @return this paths length
         */
        public double getDistance() {
            checkRep();
            return distance;
        }

        /**
         * Standard equality operation.
         *
         * @param obj The object to be compared for equality.
         * @return true iff 'obj' is an instance of a Path and 'this' and 'obj' represent
         * the same path.
         */
        @Override
        public boolean equals(Object obj) {
            checkRep();
            if (obj instanceof Path){
                Path p = (Path) obj;
                return p.origin.equals(this.origin) && p.destination.equals(this.destination) && p.distance == this.distance;
            }
            checkRep();
            return false;
        }

        /**
         * Standard hashCode function.
         *
         * @return an int that all objects equal to this will also return.
         */
        @Override
        public int hashCode() {
            checkRep();
            return Objects.hash(origin, destination, distance);
        }

        /**
         * Checks that the representation invariant holds (if any).
         */
        private void checkRep() {
            assert (origin != null);
            assert (destination != null);
            assert (distance > 0);
        }

    }

}
