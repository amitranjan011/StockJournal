package com.amit.journal.controller;

import com.amit.journal.model.Holding;
import com.amit.journal.service.HoldingService;
import com.amit.journal.service.HoldingServiceImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@CrossOrigin
@Tag(name = "Holding API", description = "Holding API")
public class HoldingController {
    private static final Logger LOG = LogManager.getLogger(HoldingController.class);
    @Autowired
    private HoldingService holdingService;

    @PostMapping(path ="/holdings/upload")
    @ApiOperation(value = "Make a POST request to upload the holding csv file",
            produces = "application/json", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Make a POST request to upload the holding csv file for a particular date in yyyy-MM-dd",
            description = "Make a POST request to upload the holding csv file for a particular date in yyyy-MM-dd"
            , responses = {@ApiResponse(responseCode = "200", description = "successful operation"
            , content = @Content(schema = @Schema(implementation = ResponseEntity.class)))})
    public ResponseEntity<String> uploadHoldingFile(
            @Parameter(
                    description = "File to be uploaded",
                    content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE)  // Won't work without OCTET_STREAM as the mediaType.
            ) @RequestPart(value = "file") MultipartFile file,
            @RequestParam(required = false, defaultValue = "0") double cash,
            @RequestParam(required = false, defaultValue = "0") double newFundAdded) { //@RequestParam("file") MultipartFile file) {
        String message = "";

        if (file.isEmpty()) {
            message = "Please select a CSV file to upload.";
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message);
        } else {
            try {
                holdingService.saveFile(file, null, cash, newFundAdded);
                message = "Uploaded the file successfully: " + file.getOriginalFilename();
                return ResponseEntity.status(HttpStatus.OK).body(message);

            } catch (Exception ex) {
                LOG.error("Exception while saving file for holding upload for file: {} : {}"
                        ,file.getOriginalFilename(), ExceptionUtils.getStackTrace(ex));
                message = "Could not upload the file: " + file.getOriginalFilename() + "!";
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message);
            }
        }
    }

    @PostMapping(path = "/holdings/upload/{holdingDate}")
    @ApiOperation(value = "Make a POST request to upload the holding csv file",
            produces = "application/json", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Make a POST request to upload the holding csv file for a particular date in yyyy-MM-dd",
            description = "Make a POST request to upload the holding csv file for a particular date in yyyy-MM-dd"
            , responses = {@ApiResponse(responseCode = "200", description = "successful operation"
            , content = @Content(schema = @Schema(implementation = ResponseEntity.class))
    )})
    public ResponseEntity<String> uploadHoldingFileForDate(
            @Parameter(
                    description = "File to be uploaded",
                    content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE)  // Won't work without OCTET_STREAM as the mediaType.
            ) @RequestPart(value = "file") MultipartFile file,
            @RequestParam(required = false, defaultValue = "0") double cash,
            @RequestParam(required = false, defaultValue = "0") double newFundAdded,
            @PathVariable @Parameter(description = "holdingDate(yyyy-MM-dd)") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate holdingDate) { //@RequestParam("file") MultipartFile file) {
        String message = "";

        if (file.isEmpty()) {
            message = "Please select a CSV file to upload.";
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message);
        } else {
            try {
                holdingService.saveFile(file, holdingDate, cash, newFundAdded);
                message = "Uploaded the file successfully: " + file.getOriginalFilename();
                return ResponseEntity.status(HttpStatus.OK).body(message);

            } catch (Exception ex) {
                LOG.error("Exception while saving file for holding upload for file: {} : {}"
                        ,file.getOriginalFilename()
                        , ExceptionUtils.getStackTrace(ex));
                message = "Could not upload the file: " + file.getOriginalFilename() + "!";
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message);
            }
        }
    }

    @GetMapping(path = "/holdings")
    @ApiOperation(value = "Get holdings for an user")
    public List<Holding> getHoldings() {
        return holdingService.getAllHoldings();
    }

    @GetMapping(path = "/holdings/{startDate}/{endDate}")
    @ApiOperation(value = "Get holdings for an user by date range in yyyy-MM-dd format")
    public List<Holding> getHoldings(
            @PathVariable @Parameter(description = "startDate(yyyy-MM-dd)") @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate startDate
            , @PathVariable @Parameter(description = "endDate(yyyy-MM-dd)") @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate endDate) {
        return holdingService.getHoldingsByDateRange(startDate, endDate);
    }

    @GetMapping(path = "/week/holdings")
    @ApiOperation(value = "Get holdings for an user")
    public List<Holding> getWeekHoldings() {
        return holdingService.getAllWeekHoldings();
    }

    @GetMapping(path = "/month/holdings")
    @ApiOperation(value = "Get monthly holdings for an user")
    public List<Holding> getMonthHoldings() {
        return holdingService.getAlMonthHoldings();
    }
}
