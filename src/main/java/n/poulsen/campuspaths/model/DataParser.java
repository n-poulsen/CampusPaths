package n.poulsen.campuspaths.model;

import n.poulsen.campuspaths.model.CampusMap.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


/**
 * Contains helper methods to parse building and path data from TSV files
 */
public class DataParser {

    /** A checked exception class for bad data files. */
    public static class MalformedDataException extends Exception {

        /**
         *  Constructs a checked exception for bad data files.
         */
        public MalformedDataException() {}

        /**
         *  Constructs a checked exception for bad data files with message.
         *
         * @param message error message
         */
        public MalformedDataException(String message) {
            super(message);
        }

        /**
         *  Constructs a checked exception for bad data files with cause.
         *
         * @param cause the cause of exception
         */
        public MalformedDataException(Throwable cause) {
            super(cause);
        }

        /**
         *  Constructs a checked exception for bad data files with message and cause.
         *
         * @param message error message
         * @param cause the cause of exception
         */
        public MalformedDataException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * Reads the campus paths dataset. Each line of the input file contains the path's origin coordinates,
     * as two rational values seperated by a comma, the path's destination coordinates in the same format,
     * and the path's distance, all separated by tabs. The first line of the file contains information on
     * the data in each t-s-v, and hence is ignored.
     *
     * @param filename the file that will be read
     * @spec.requires filename is a valid file path
     * @return a List of paths contained in the specified file
     * @throws MalformedDataException if the file is not well-formed: each line contains exactly three
     *     tokens separated by a tab, or else starting with a # symbol to indicate a comment line
     */
    public static List<Path> parsePathData(String filename) throws MalformedDataException {
        BufferedReader reader = null;
        List<Path> paths = new ArrayList<>();
        try {
            reader = Files.newBufferedReader(Paths.get(filename), Charset.defaultCharset());

            String inputLine;
            //Ignores first line, which contains TSV header (name of columns)
            reader.readLine();
            while ((inputLine = reader.readLine()) != null) {

                // Ignore comment lines.
                if (inputLine.startsWith("#")) {
                    continue;
                }

                // As seen in post 520 on Piazza
                @SuppressWarnings("StringSplitter")
                String[] tokens = inputLine.split("\t");
                if (tokens.length != 3) {
                    throw new MalformedDataException("Line should contain exactly two tabs: " + inputLine);
                }

                Coordinates origin;
                Coordinates destination;

                try{
                    origin = parseCoordinates(tokens[0]);
                    destination = parseCoordinates(tokens[1]);
                }catch(IllegalArgumentException e){
                    throw new MalformedDataException("Line coordinates not well formatted: " + inputLine, e);
                }

                Double distance = Double.parseDouble(tokens[2]);
                if (distance <= 0){
                    throw new MalformedDataException("Negative distance in file");
                }
                paths.add(new Path(origin, destination, distance));
            }
        } catch (IOException e) {
            System.err.println(e.toString());
            e.printStackTrace(System.err);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    System.err.println(e.toString());
                    e.printStackTrace(System.err);
                }
            }
        }
        return paths;
    }

    /**
     * Reads the campus buildings dataset. Each line of the input file contains the building's abbreviated
     * name, followed by the buildings full name, followed by a rational value for the building's x coordinate
     * and one for the building's y coordinate, all separated by tabs. The first line of the file contains
     * information on the data in each t-s-v, and hence is ignored.
     *
     * @param filename the file that will be read
     * @spec.requires filename is a valid file path
     * @return a list of buildings contained in the specified file
     * @throws MalformedDataException if the file is not well-formed: each line contains exactly four
     *     tokens separated by a tab, or else starting with a # symbol to indicate a comment line
     */
    public static List<Building> parseBuildingData(String filename) throws MalformedDataException {
        BufferedReader reader = null;
        List<Building> buildings = new ArrayList<>();
        try {
            reader =  Files.newBufferedReader(Paths.get(filename), Charset.defaultCharset());

            String inputLine;
            //Ignores first line, which contains TSV header (name of columns)
            reader.readLine();
            while ((inputLine = reader.readLine()) != null) {

                // Ignore comment lines.
                if (inputLine.startsWith("#")) {
                    continue;
                }

                // As seen in post 520 on Piazza
                @SuppressWarnings("StringSplitter")
                String[] tokens = inputLine.split("\t");
                if (tokens.length != 4) {
                    throw new MalformedDataException("Line should contain exactly three tabs: " + inputLine);
                }

                String shortName = tokens[0];
                String longName = tokens[1];
                Coordinates location;
                try{
                    location = parseCoordinates(tokens[2] + "," + tokens[3]);
                }catch(IllegalArgumentException e){
                    throw new MalformedDataException("Line coordinates not well formatted: " + inputLine, e);
                }

                buildings.add(new Building(shortName, longName, location));
            }
        } catch (IOException e) {
            System.err.println(e.toString());
            e.printStackTrace(System.err);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    System.err.println(e.toString());
                    e.printStackTrace(System.err);
                }
            }
        }
        return buildings;
    }

    /**
     * Parses a string containing two rational values separated by a comma into a Coordinate containing the
     * first value as the x coordinate and the second value as y coordinate.
     *
     * @param c the String we want to parse into Coordinates
     * @return the Coordinates representing c
     * @throws IllegalArgumentException if the string is not well-formed
     */
    public static Coordinates parseCoordinates(String c) throws IllegalArgumentException {
        // As seen in post 520 on Piazza
        @SuppressWarnings("StringSplitter")
        String[] tokens = c.split(",");
        if (tokens.length != 2) {
            throw new IllegalArgumentException("Coordinates not in correct format: " + c);
        }
        double x = Double.parseDouble(tokens[0]);
        double y = Double.parseDouble(tokens[1]);
        return new Coordinates(x, y);
    }

}
