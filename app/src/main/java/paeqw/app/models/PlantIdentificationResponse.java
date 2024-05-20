package paeqw.app.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PlantIdentificationResponse {
    @SerializedName("result")
    public Result result;

    public static class Result {
        @SerializedName("classification")
        public Classification classification;
    }

    public static class Classification {
        @SerializedName("suggestions")
        public List<Suggestion> suggestions;
    }

    public static class Suggestion {
        @SerializedName("name")
        public String name;

        @SerializedName("probability")
        public double probability;
    }
}
