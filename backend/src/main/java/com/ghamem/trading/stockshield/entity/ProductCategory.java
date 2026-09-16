package com.ghamem.trading.stockshield.entity;

public enum ProductCategory {
    PANAME_STK("Paname STK"),
    PANAME_ROCHE("Paname Roché"),
    PANAME_BRKT("Paname BRKT"),
    PANAME_CNE("Paname CNE"),
    VRAC("Vrac"),
    TROPIC("Tropic"),
    ESKIMO("Eskimo"),
    KIMO("Kimo"),
    CAPP("Capp"),
    KIMCONE("Kimcone"),
    GLACON("Glacon"),
    MAGNUM("Magnum"),
    FLASH("Flash"),
    ZONDA("Zonda");

    private final String label;

    ProductCategory(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
