package paeqw.app.models;

import java.time.LocalDateTime;

public class Plant {
    private String name;
    private LocalDateTime whenLastWatered;
    //todo: image
    public Plant(String name) {
        this.name = name;
    }
    public Plant(String name, LocalDateTime whenLastWatered) {
        this.name = name;
        this.whenLastWatered = whenLastWatered;
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
}
