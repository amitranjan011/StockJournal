package com.amit.journal.controller;

import com.amit.journal.model.TransactionSummaryKPIHolder;
import com.amit.journal.service.TransactionSummaryService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@CrossOrigin
@Tag(name = "TransactionSummary API", description = "TransactionSummary API")
public class TransactionSummaryController {

    @Autowired
    private TransactionSummaryService transactionSummaryService;
    @GetMapping(value = "/transactions/summary")
    @ApiOperation(value = "Get All transactions summary for an user by symbol (optional) ")
    @Operation(summary = "Get transactions summary for an user by symbol ",
            description = "Get transactions summary for an user by symbol "
            , responses = { @ApiResponse(responseCode = "200", description = "successful operation"
            , content = @Content(schema = @Schema(implementation = TransactionSummaryKPIHolder.class)) )} )
    public TransactionSummaryKPIHolder getSummaryRecords(@RequestParam(required = false) String symbol) {
        return transactionSummaryService.getSummaryRecordsAndKPI(symbol, null, null);
    }

    @GetMapping(value = "/transactions/summary/{startDate}/{endDate}")
    @ApiOperation(value = "Get transactions summary for an user by symbol and date range")
    @Operation(summary = "Get transactions summary for an user by symbol and date range. date in yyyy-MM-dd",
            description = "Get transactions summary for an user by symbol and date range"
            , responses = { @ApiResponse(responseCode = "200", description = "successful operation"
            , content = @Content(schema = @Schema(implementation = TransactionSummaryKPIHolder.class)) )} )
    public TransactionSummaryKPIHolder getSummaryRecordsByDate(@RequestParam(required = false) String symbol
            , @PathVariable @Parameter(description = "endDate(yyyy-MM-dd)") @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate endDate
            , @PathVariable @Parameter(description = "startDate(yyyy-MM-dd)") @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate startDate) {
        return transactionSummaryService.getSummaryRecordsAndKPI(symbol, startDate, endDate);
    }
    @GetMapping(value = "/transactions/summary/update")
    @ApiOperation(value = "Get LTP and related unrealized data details  ")
    @Operation(summary = "Get LTP and related unrealized data details ",
            description = "Get LTP and related unrealized data details"
            , responses = { @ApiResponse(responseCode = "200", description = "successful operation"
            , content = @Content(schema = @Schema(implementation = TransactionSummaryKPIHolder.class)) )} )
    public void updateLTPData() {
         transactionSummaryService.updateAdditionalInfo();
    }
}
