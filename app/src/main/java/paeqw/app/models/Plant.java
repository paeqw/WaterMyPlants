package paeqw.app.models;

import java.time.LocalDateTime;

public class Plant {
    private String name;
    private LocalDateTime whenLastWatered;
    private String imageUrl;

    public Plant() {
    }

    public Plant(String name) {
        this.name = name;
    }

    public Plant(String name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public Plant(String name, LocalDateTime whenLastWatered, String imageUrl) {
        this.name = name;
        this.whenLastWatered = whenLastWatered;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getWhenLastWatered() {
        return whenLastWatered;
    }

    public void setWhenLastWatered(LocalDateTime whenLastWatered) {
        this.whenLastWatered = whenLastWatered;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
