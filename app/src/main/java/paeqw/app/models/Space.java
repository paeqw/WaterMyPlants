package paeqw.app.models;

import java.util.ArrayList;
import java.util.List;

import paeqw.app.exceptions.CouldNotFindException;

public class Space {
    private String spaceName;
    private List<Plant> plantList;

    public Space() {
        this.plantList = new ArrayList<>();
    }

    public Space(String spaceName) {
        this.spaceName = spaceName;
        this.plantList = new ArrayList<>();
    }

    public void addPlant(Plant plant) {
        plantList.add(plant);
    }

    public void setSpaceName(String spaceName) {
        this.spaceName = spaceName;
    }

    public void setPlantList(List<Plant> plantList) {
        this.plantList = plantList;
    }

    public void removePlant(Plant plant) throws CouldNotFindException {
        if (plantList.contains(plant)) plantList.remove(plant);
        else throw new CouldNotFindException("Could not find plant with given name");
    }
    public void removePlant(String name) throws CouldNotFindException {
        List<Plant> searchResult = searchPlant(name);
        if (searchResult.size() > 0) for (Plant el : searchResult) plantList.remove(el);
    }
    public List<Plant> searchPlant(String name) throws CouldNotFindException {
        List<Plant> searchResult = new ArrayList<>();
        for (Plant el : plantList) {
            if (el.getName().toLowerCase().startsWith(name.toLowerCase())) searchResult.add(el);
        }
        if(searchResult.size() > 0) return searchResult;
        throw new CouldNotFindException("Could not find plant with given name");
    }
    public List<Plant> getPlantList() {
        return plantList;
    }

    public String getSpaceName() {
        return spaceName;
    }

    @Override
    public String toString() {
        return spaceName;
    }
}
