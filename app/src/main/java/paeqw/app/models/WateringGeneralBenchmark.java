package paeqw.app.models;

import android.util.Log;

public class WateringGeneralBenchmark {
    private String value;
    private String unit;
    public String getValue() { return value; }
    public String getUnit() { return unit; }
    public int getWhenWater() {
        int[] tab = getNumbersFromValue();
        return (tab[0] + tab[1])/2;
    }

    private int[] getNumbersFromValue() {
        try {
            char[] tab = value.toCharArray();
            return new int[]{tab[0], tab[2]};
        } catch (Exception e) {
            Log.e("WaterYourPlants.WateringGeneralBenchmark", "Oj nie");
        }

        return new int[0];
    }
}
