package paeqw.app.models;

import java.util.List;

public class PlantApi {
    private String common_name;
    private List<String> scientific_name;
    private DefaultImage default_image;

    public String getCommonName() {
        return common_name;
    }

    public List<String> getScientificName() {
        return scientific_name;
    }

    public DefaultImage getDefaultImage() {
        return default_image;
    }
}