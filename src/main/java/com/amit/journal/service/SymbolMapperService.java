package com.amit.journal.service;

import com.amit.journal.model.SymbolMapping;

public interface SymbolMapperService {
    void saveMapping(String symbol, String mappedSymbol);

    SymbolMapping getMapping(String symbol);
}
