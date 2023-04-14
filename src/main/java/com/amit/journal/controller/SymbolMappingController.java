package com.amit.journal.controller;


import com.amit.journal.model.SymbolMapping;
import com.amit.journal.service.SymbolMapperService;
import com.amit.journal.service.TransactionSummaryService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


@RestController
@CrossOrigin
@Tag(name = "SymbolMapping API", description = "SymbolMapping API")
public class SymbolMappingController {
    private static final Logger LOG = LogManager.getLogger(SymbolMappingController.class);

    @Autowired
    private SymbolMapperService symbolMapperService;
    @Autowired
    private TransactionSummaryService transactionSummaryService;
    @GetMapping(path = "/mapData/{symbol}/{mappedSymbol}")
    @ApiOperation(value = "Map symbol with another available symbol")
    public String updateMapping( @PathVariable @Parameter(required = true) String symbol, @PathVariable @Parameter(required = true) String mappedSymbol) {
        symbolMapperService.saveMapping(symbol, mappedSymbol);
        return "Success";
    }
    @GetMapping(path = "/getMapData/{symbol}")
    @ApiOperation(value = "get symbol mapped with another available symbol")
    public SymbolMapping getMapping(@PathVariable @Parameter(required = true) String symbol) {
        return symbolMapperService.getMapping(symbol);
    }

    @GetMapping(path = "/updateSummaryForMappedSymbol/{symbol}/{mappedSymbol}")
    @ApiOperation(value = "update summary for mapped symbols")
    public String getHoldings( @PathVariable @Parameter(required = true) String symbol, @PathVariable @Parameter(required = true) String mappedSymbol) {
        transactionSummaryService.updateSummaryForSymbolMap(symbol, mappedSymbol);
        return "Success";
    }
}
