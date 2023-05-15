package com.amit.journal.controller;

import com.amit.journal.model.Transaction;
import com.amit.journal.model.TransactionSummaryKPIHolder;
import com.amit.journal.service.TransactionSummaryService;
import com.amit.journal.util.CommonUtil;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;

@RestController
@CrossOrigin
@Tag(name = "TransactionSummary API", description = "TransactionSummary API")
public class TransactionSummaryController {
    private static final Logger LOG = LogManager.getLogger(TransactionSummaryController.class);
    @Autowired
    private TransactionSummaryService transactionSummaryService;

    @GetMapping(value = "/transactions/summary")
    @ApiOperation(value = "Get All transactions summary for an user by symbol (optional) ")
    @Operation(summary = "Get transactions summary for an user by symbol ",
            description = "Get transactions summary for an user by symbol "
            , responses = {@ApiResponse(responseCode = "200", description = "successful operation"
            , content = @Content(schema = @Schema(implementation = TransactionSummaryKPIHolder.class)))})
    public TransactionSummaryKPIHolder getSummaryRecords(@RequestParam(required = false) String symbol) {
        return transactionSummaryService.getSummaryRecordsAndKPI(symbol, null, null);
    }

    @GetMapping(value = "/transactions/summary/{startDate}/{endDate}")
    @ApiOperation(value = "Get transactions summary for an user by symbol and date range")
    @Operation(summary = "Get transactions summary for an user by symbol and date range. date in yyyy-MM-dd",
            description = "Get transactions summary for an user by symbol and date range"
            , responses = {@ApiResponse(responseCode = "200", description = "successful operation"
            , content = @Content(schema = @Schema(implementation = TransactionSummaryKPIHolder.class)))})
    public TransactionSummaryKPIHolder getSummaryRecordsByDate(@RequestParam(required = false) String symbol
            , @PathVariable @Parameter(description = "endDate(yyyy-MM-dd)") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate
            , @PathVariable @Parameter(description = "startDate(yyyy-MM-dd)") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate) {
        return transactionSummaryService.getSummaryRecordsAndKPI(symbol, startDate, endDate);
    }

    @GetMapping(value = "/transactions/summary/LTP/update")
    @ApiOperation(value = "Update LTP and related unrealized data details  ")
    @Operation(summary = "Update LTP and related unrealized data details ",
            description = "Update LTP and related unrealized data details"
            , responses = {@ApiResponse(responseCode = "200", description = "successful operation")})
    public void updateLTPData() {
        transactionSummaryService.updateAdditionalInfo();
    }

    @GetMapping(value = "/transactions/summary/LTP/{symbol}")
    @ApiOperation(value = "Get LTP for a symbol ")
    @Operation(summary = "Get LTP for a symbol  ",
            description = "Get LTP for a symbol "
            , responses = {@ApiResponse(responseCode = "200", description = "successful operation")})
    public double getLatestPrice(@PathVariable String symbol) {
        return transactionSummaryService.getLatestPrice(symbol);
    }

    @GetMapping(value = "/transactions/updateBatchId")
    @ApiOperation(value = "update batchId of summary ")
    @Operation(summary = "update batchId of summary  ",
            description = "update batchId of summary "
            , responses = {@ApiResponse(responseCode = "200", description = "successful operation")})
    public String getLatestPrice() {
        transactionSummaryService.updateSummaryBatchId();
        return "Success";
    }


    @GetMapping(value = "/transactions/summary/resetLatestData")
    @ApiOperation(value = "Reset latest summary records from summary history  ")
    @Operation(summary = "Reset latest summary records from summary history ",
            description = "Reset latest summary records from summary history"
            , responses = {@ApiResponse(responseCode = "200", description = "successful operation")})
    public void resetSummaryData() {
        transactionSummaryService.resetSummaryData();
    }

    @PostMapping(path = "/transactions/summary/update/Stoploss")
    @ApiOperation(value = "Update stoploss and other related data (strategy, comments, action)",
            produces = "application/json", consumes = "application/json")
    public ResponseEntity<String> updateStopLoss(@RequestBody Transaction transaction) { //@RequestParam("file") MultipartFile file) {
        String message = "";
        try {
            LOG.error("@@@@@@@@@@@@@@@@ updateStopLoss called now  : {}", transaction);
            transactionSummaryService.updateStopLoss(transaction);
            message = "Updated stoploss for symbol : " + transaction.getSymbol() + " to : " + transaction.getStopLoss();
            return ResponseEntity.status(HttpStatus.OK).body(message);
        } catch (Exception ex) {
            LOG.error("Exception : {}", CommonUtil.getStackTrace(ex));
            message = "Exception updating stoploss";
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message);
        }
    }


    @GetMapping(path = "/transactions/summary/exportCsv/{type}")
    @ApiOperation(value = "Export csv for transactions for an user and date range")
    public void downloadTransactions(HttpServletResponse response,
                                     @PathVariable @Parameter(description = "Type of position") String type) throws IOException {
        String name = type + " -transactionsummary-" + CommonUtil.getTodayDateString() + ".csv";
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; file=" + name);
        response.setHeader("fileName", name);
        transactionSummaryService.exportTransactionSummary(response.getWriter(), type);

    }
}
