package paeqw.app.models;

import java.io.Serializable;
import java.util.List;

public class PlantApi implements Serializable {
    private String common_name;
    private List<String> scientific_name;
    private int id;
    private DefaultImage default_image;
    private String family;
    private String description;
    public String getCommonName() {
        return common_name;
    }

    public List<String> getScientificName() {
        return scientific_name;
    }

    public DefaultImage getDefaultImage() {
        return default_image;
    }

    public String getFamily() {
        return family;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }
}
