package paeqw.app.ui.models;

import java.time.LocalDateTime;

public class Plant {
    private String name;
    private String localisation;
    private LocalDateTime whenLastWatered;
    //todo: image

    public Plant(String name, String localisation) {
        this.name = name;
        this.localisation = localisation;
    }

    public String getName() {
        return name;
    }

    public String getLocalisation() {
        return localisation;
    }

    public LocalDateTime getWhenLastWatered() {
        return whenLastWatered;
    }

    public void setWhenLastWatered(LocalDateTime whenLastWatered) {
        this.whenLastWatered = whenLastWatered;
    }
}
