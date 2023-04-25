package com.amit.journal.model;

public class PEData {
    public double getInitPe() {
        return initPe;
    }

    public void setInitPe(double initPe) {
        this.initPe = initPe;
    }

    public double getCurrentPe() {
        return currentPe;
    }

    public void setCurrentPe(double currentPe) {
        this.currentPe = currentPe;
    }

    private double initPe;
    private double currentPe;

    @Override
    public String toString() {
        return "PEData{" +
                "initPe=" + initPe +
                ", currentPe=" + currentPe +
                '}';
    }
}
