package paeqw.app.models;

import java.io.Serializable;
import java.util.List;

public class PlantApi implements Serializable {
    private int id;
    private String common_name;
    private List<String> scientific_name;
    private String family;
    private String description;
    private DefaultImage default_image;
    private WateringGeneralBenchmark watering_general_benchmark;
    private List<String> volume_water_requirement;
    private String maintenance;
    private String growth_rate;
    private String type;
    private Hardiness hardiness;
    private List<String> pruning_month;
    private String care_guides;

    // Getters
    public String getCommonName() { return common_name; }
    public List<String> getScientificName() { return scientific_name; }
    public String getFamily() { return family; }
    public String getDescription() { return description; }
    public DefaultImage getDefaultImage() { return default_image; }
    public WateringGeneralBenchmark getWateringGeneralBenchmark() { return watering_general_benchmark; }
    public List<String> getVolumeWaterRequirement() { return volume_water_requirement; }
    public String getMaintenance() { return maintenance; }
    public String getGrowthRate() { return growth_rate; }
    public String getType() { return type; }
    public Hardiness getHardiness() { return hardiness; }
    public List<String> getPruningMonth() { return pruning_month; }
    public String getCareGuides() { return care_guides; }

    public int getId() {
        return id;
    }
}
