package com.company;

public class PanCard {
    enum Color {
        Spade, Club, Heart, Diamond;

        private static final Color[] colors = Color.values();

        public static Color getColor(int i) {
            return Color.colors[i];
        }
    }

    enum Value {
       Nine, Ten, Jack, Queen, King, Ace;

        private static final Value[] values = Value.values();

        public static Value getValue(int i) {
            return Value.values[i];
        }
    }

    private final Color color;
    private final Value value;
    private final int colorInt;
    private final int valueInt;

    public PanCard(final Color color, final Value value) {
        this.color = color;
        this.value = value;
        this.colorInt = getColorInt();
        this.valueInt = getValueInt();
    }

    public Color getColor(){
        return this.color;
    }

    public int getColorInt() {
        int colorNum;
        String colorName = this.color.toString();
        switch(colorName) {
            case "Spade":
                colorNum = 0;
                break;
            case "Club":
                colorNum = 1;
                break;
            case "Heart":
                colorNum = 2;
                break;
            case "Diamond":
                colorNum = 3;
                break;
            default:
                colorNum = -1;
        }
        return colorNum;
    }

    public Value getValue(){
        return this.value;
    }

    public int getValueInt() {
        int valueNum;
        String valueName = this.value.toString();
        switch(valueName) {
            case "Nine":
                valueNum = 0;
                break;
            case "Ten":
                valueNum = 1;
                break;
            case "Jack":
                valueNum = 2;
                break;
            case "Queen":
                valueNum = 3;
                break;
            case "King":
                valueNum = 4;
                break;
            case "Ace":
                valueNum = 5;
                break;
            default:
                valueNum = -1;
        }
        return valueNum;
    }

    public String toString() {
        return color + "_" + value;
    }
}
