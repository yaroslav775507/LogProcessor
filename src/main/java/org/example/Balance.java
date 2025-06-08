package org.example;

public class Balance {
    private double value;

    public Balance() {
        this.value = 0.0;
    }

    public Balance(double initialValue) {
        this.value = initialValue;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public void increase(double amount) {
        this.value += amount;
    }

    public void decrease(double amount) {
        this.value -= amount;
    }

    @Override
    public String toString() {
        return String.format("%.2f", value);
    }
}
