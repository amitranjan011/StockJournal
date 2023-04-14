package com.amit.journal.service;

import com.amit.journal.domain.repo.SymbolMapperDAOImpl;
import com.amit.journal.model.SymbolMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SymbolMapperServiceImpl implements SymbolMapperService {

    @Autowired
    SymbolMapperDAOImpl symbolMapperDAOImpl;
    @Override
    public void saveMapping(String symbol, String mappedSymbol) {
        SymbolMapping symbolMapping = new SymbolMapping();
        symbolMapping.setSymbol(symbol);
        symbolMapping.setMappedSymbol(mappedSymbol);

        SymbolMapping revSymbolMapping = new SymbolMapping();
        revSymbolMapping.setSymbol(mappedSymbol);
        revSymbolMapping.setMappedSymbol(symbol);

        symbolMapperDAOImpl.persist(symbolMapping);
        symbolMapperDAOImpl.persist(revSymbolMapping);
    }

    @Override
    public SymbolMapping getMapping(String symbol) {
        SymbolMapping symbolMapping = symbolMapperDAOImpl.getMapping(symbol);
        return symbolMapping;
    }

}
