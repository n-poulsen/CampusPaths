package n.poulsen.campuspaths.service;

import n.poulsen.campuspaths.model.CampusMap;
import n.poulsen.campuspaths.model.CampusMap.*;
import n.poulsen.campuspaths.repository.DataParserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * The Service for the CampusPaths app. Represents a campus map
 */
@Service
public class CampusMapService {

    /** The CampusMap inside this wrapper */
    private CampusMap map;

    /** A byte array containing the .jpg image of the campus map */
    private byte[] image;

    /** A data parser to use to load data into this */
    @Autowired
    private DataParserRepository parser;

    /** @spec.effects Constructs a new empty campus map service*/
    public CampusMapService(){
        map = new CampusMap();
        image = new byte[0];
    }

    /**
     * Gets the parsed data from the buildings and paths data sets from the DataParser, and
     * loads them to this CampusMap
     *
     * @spec.effects adds all buildings and paths in the data files to this campus map
     * @spec.modifies this
     * @throws ServerSideException if the data could not be parsed
     */

    @PostConstruct
    public void loadData() throws ServerSideException{
        parser.parseData();
        List<Building> buildings = parser.getBuildings();
        List<Path> paths = parser.getPaths();
        for (Building b: buildings){
            map.addBuilding(b);
        }
        for (Path p: paths){
            map.addPath(p);
        }
        image = parser.getImage();
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
     * Returns a list of all buildings in this map, sorted by short name
     *
     * @return a list of all buildings in this map, sorted by short name
     */
    public List<Building> listBuildings(){
        List<Building> buildings = map.listBuildings();
        Collections.sort(buildings, Comparator.comparing(Building::getShortName));
        return buildings;
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
        return map.shortestPath(b1, b2);
    }

}
