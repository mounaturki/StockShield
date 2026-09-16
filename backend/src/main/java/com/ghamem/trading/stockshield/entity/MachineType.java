package com.ghamem.trading.stockshield.entity;

public enum MachineType {
    GLACON("Machine à glaçons"),
    GLACE("Machine à glace");

    private final String label;

    MachineType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
