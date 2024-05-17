package paeqw.app.models;

import java.util.List;

public class CareGuide {
    private int id;
    private int species_id;
    private String common_name;
    private List<String> scientific_name;
    private List<Section> section;

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSpeciesId() {
        return species_id;
    }

    public void setSpeciesId(int species_id) {
        this.species_id = species_id;
    }

    public String getCommonName() {
        return common_name;
    }

    public void setCommonName(String common_name) {
        this.common_name = common_name;
    }

    public List<String> getScientificName() {
        return scientific_name;
    }

    public void setScientificName(List<String> scientific_name) {
        this.scientific_name = scientific_name;
    }

    public List<Section> getSection() {
        return section;
    }

    public void setSection(List<Section> section) {
        this.section = section;
    }

    public static class Section {
        private int id;
        private String type;
        private String description;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
