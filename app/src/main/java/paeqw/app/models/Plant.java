package paeqw.app.models;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;


public class Plant {
    private String name;
    private Long whenLastWatered;
    private String imageUrl;
    private int wateringInterval;

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
        this.whenLastWatered = whenLastWatered.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();;
        this.imageUrl = imageUrl;
    }

    public Plant(String name, LocalDateTime whenLastWatered, String imageUrl, int wateringInterval) {
        this.name = name;
        this.whenLastWatered = whenLastWatered.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();;
        this.imageUrl = imageUrl;
        this.wateringInterval = wateringInterval;
    }

    public Plant(String name, String imageUrl, int wateringInterval) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.wateringInterval = wateringInterval;
    }

    public String getName() {
        return name;
    }

    public Long getWhenLastWatered() {
        return whenLastWatered;
    }

    public void setWhenLastWatered(long whenLastWatered) {
        this.whenLastWatered = whenLastWatered;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getWateringInterval() {
        return wateringInterval;
    }

    public void setWateringInterval(int wateringInterval) {
        this.wateringInterval = wateringInterval;
    }

    public void setName(String name) {
        this.name = name;
    }
}
