package com.amit.journal.model;

import org.springframework.data.annotation.Id;

import java.util.Map;

public class SymbolMapping extends UserBase {
    @Id
    private String id;
    private String symbol;

    private String mappedSymbol;
//    private Map<String, String> infoMap;


    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getMappedSymbol() {
        return mappedSymbol;
    }

    public void setMappedSymbol(String mappedSymbol) {
        this.mappedSymbol = mappedSymbol;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "SymbolMapping{" +
                "id='" + id + '\'' +
                ", symbol='" + symbol + '\'' +
                ", mappedSymbol='" + mappedSymbol + '\'' +
                '}';
    }
}
