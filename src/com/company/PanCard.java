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

    public PanCard(final Color color, final Value value) {
        this.color = color;
        this.value = value;
    }

    public Color getColor(){
        return this.color;
    }

    public Value getValue(){
        return this.value;
    }

    public String toString() {
        return color + "_" + value;
    }
}
