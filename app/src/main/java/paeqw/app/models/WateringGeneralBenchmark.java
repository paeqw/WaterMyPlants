package paeqw.app.models;

import android.util.Log;

public class WateringGeneralBenchmark {
    private String value;
    private String unit;
    public String getValue() { return value; }
    public String getUnit() { return unit; }
    public int getWhenWater() {
        int[] tab = getNumbersFromValue();
        Log.e("asl;kjdlaksjdlkjaslkdjjasldjkalskjlkjaldkjlasjdlaj",tab[0]+ " " + tab[1]);
        return (tab[0] + tab[1])/2;
    }

    private int[] getNumbersFromValue() {
        try {
            String[] parts = value.split("-");

            int number1 = Integer.parseInt(parts[0]);
            int number2 = Integer.parseInt(parts[1]);

            return new int[]{number1, number2};
        } catch (Exception e) {
            Log.e("WaterYourPlants.WateringGeneralBenchmark", "Oj nie");
        }
        return new int[0];
    }
}
