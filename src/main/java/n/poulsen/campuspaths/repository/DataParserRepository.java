package n.poulsen.campuspaths.repository;

import n.poulsen.campuspaths.model.CampusMap.*;
import n.poulsen.campuspaths.service.ServerSideException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static n.poulsen.campuspaths.model.DataParser.*;

/**
 * The Repository for the CampusPaths app.
 */
@Repository
public class DataParserRepository {

    /** The paths that have been parsed by this DataParser */
    private List<Path> paths;

    /** The buildings that have been parsed by this DataParser */
    private List<Building> buildings;

    /** The byte array containing the jpg image of the campus map */
    private byte[] image;

    /** The path at which the TSV file containing the Paths data is located */
    @Value("${data.pathsPath}")
    private final static String PATHS_PATH = "data/campus_paths.tsv";

    /** The path at which the TSV file containing the Buildings data is located */
    @Value("${data.buildingsPath}")
    private final static String BUILDINGS_PATH = "data/campus_buildings.tsv";

    /** The path at which the jpg image of the campus map is located */
    @Value("${data.imagePath}")
    private final static String IMAGE_PATH = "data/campus_map.jpg";

    /** @spec.effects Constructs a new DataParser that hasn't parsed any data yet */
    public DataParserRepository(){
        paths = new ArrayList<>();
        buildings = new ArrayList<>();
        image = new byte[0];
    }

    /**
     * Returns an unmodifiable list of Paths that have been parsed by this DataParser
     *
     * @return an unmodifiable list of Paths that have been parsed by this DataParser
     */
    public List<Path> getPaths(){
        return Collections.unmodifiableList(paths);
    }

    /**
     * Returns an unmodifiable list of Buildings that have been parsed by this DataParser
     *
     * @return an unmodifiable list of Buildings that have been parsed by this DataParser
     */
    public List<Building> getBuildings(){
        return Collections.unmodifiableList(buildings);
    }

    /**
     * Returns the byte array containing the jpg image of the campus map
     *
     * @return the byte array containing the jpg image of the campus map
     */
    public byte[] getImage(){
        return Arrays.copyOf(image, image.length);
    }

    /**
     * Parses the data contained in the TSV files
     *
     * @spec.effects loads the tsv data representing paths and buildings into this.
     * @spec.modifies this
     * @throws ServerSideException if the data could not be parsed
     */
    public void parseData() throws ServerSideException{
        try{
            buildings = parseBuildingData(BUILDINGS_PATH);
            paths = parsePathData(PATHS_PATH);
            image = parseImage();
        }catch (MalformedDataException e) {
            buildings = new ArrayList<>();
            paths = new ArrayList<>();
            System.err.println("Could not load data: not in correct format");
            e.printStackTrace(System.err);
            throw new ServerSideException("Could not parse data");
        }
    }

    /**
     * Parses the image of the campus map
     *
     * @spec.effects loads the byte array containing the bytes from the jpg image of the campus map into this.
     * @spec.modifies this
     * @return the byte array containing the bytes from the jpg image of the campus map
     * @throws ServerSideException if the image could not be parsed into a byte array
     */
    private byte[] parseImage() throws ServerSideException {
        byte[] fileContent = new byte[0];
        try {
            File fi = new File(IMAGE_PATH);
            fileContent = Files.readAllBytes(fi.toPath());
        }catch (IOException e) {
            System.err.println(e.toString());
            e.printStackTrace(System.err);
            throw new ServerSideException("Could not parse image");
        }
        return fileContent;
    }

}
