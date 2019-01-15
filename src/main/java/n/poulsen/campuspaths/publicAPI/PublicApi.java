package n.poulsen.campuspaths.publicAPI;

import n.poulsen.campuspaths.service.CampusMapService;
import n.poulsen.campuspaths.model.CampusMap.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * API for the application
 */
@RestController
@CrossOrigin("http://localhost:3000")
public class PublicApi {

    /** The Model of CampusPaths, i.e. the map containing all campus buildings and paths */
    @Autowired
    private CampusMapService map;

    /**
     * Returns an iterable collection of all buildings in the map
     *
     * @return an iterable collection of all buildings in the map
     */
    @GetMapping("/buildings")
    public Iterable<Building> getBuildings(){
        return map.listBuildings();
    }

    /**
     * Returns the shortest path between two buildings
     *
     * @param b1 the building the user starts at
     * @param b2 the building the user ends at
     * @return the shortest path between two b1 and b2
     */
    @GetMapping("/shortestPath")
    public Iterable<Path> shortestPath(@RequestParam String b1, @RequestParam String b2){
        return map.shortestPath(b1, b2);
    }

    /**
     * Returns the byte array containing the jpg image of the campus map
     *
     * @return the byte array containing the jpg image of the campus map
     */
    @GetMapping("/mapImage")
    public byte[] mapImage(){
        return map.getImage();
    }

}