package com.amit.journal.controller;

import com.amit.journal.model.Transaction;
import com.amit.journal.service.TransactionService;
import com.amit.journal.util.CommonUtil;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@CrossOrigin
@Tag(name = "Transaction API", description = "Transaction API")
public class TransactionController /* implements IControllerIface */ {

	@Autowired
	private TransactionService transactionService;

	@GetMapping(path = "/transactions")
	@ApiOperation(value = "Get transactions for an user")
	public List<Transaction> getTransactions() {
		return transactionService.getTransactions();
	}

	@GetMapping(path = "/transactions/{startDate}/{endDate}")
	@ApiOperation(value = "Get transactions for an user")
	public List<Transaction> getTransactions(@RequestParam(required = false) String symbol
			, @PathVariable @Parameter(description = "endDate(yyyy-MM-dd)") @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate endDate
			, @PathVariable @Parameter(description = "startDate(yyyy-MM-dd)") @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate startDate) {
		return transactionService.getTransactions(symbol, startDate, endDate);
	}

	@GetMapping(path = "/transactions/exportCsv/{startDate}/{endDate}")
	@ApiOperation(value = "Export csv for transactions for an user and date range")
	public void downloadTransactions(HttpServletResponse response,
									 @PathVariable @Parameter(description = "endDate(yyyy-MM-dd)") @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate endDate
			, @PathVariable @Parameter(description = "startDate(yyyy-MM-dd)") @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate startDate) throws IOException {
		response.setContentType("text/csv");
		response.setHeader("Content-Disposition", "attachment; file=transactions.csv");
		response.setHeader("fileName", "transactions_" +  CommonUtil.getTodayDateString() + ".csv");
		transactionService.exportTransactions(response.getWriter(), startDate, endDate);

	}

	@PostMapping(path = "/transactions/upload")
	@ApiOperation(value = "Make a POST request to upload the file",
    produces = "application/json", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> uploadFile(
			@Parameter(
	        description = "File to be uploaded", 
	        content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE)  // Won't work without OCTET_STREAM as the mediaType.
	    ) @RequestPart(value = "file") MultipartFile file) { //@RequestParam("file") MultipartFile file) {
		String message = "";

		if (file.isEmpty()) {
			message = "Please select a CSV file to upload.";
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message);
		} else {
			try {
				transactionService.saveFile(file, null);
				message = "Uploaded the file successfully: " + file.getOriginalFilename();
				return ResponseEntity.status(HttpStatus.OK).body(message);

			} catch (Exception ex) {
				System.err.println(ExceptionUtils.getStackTrace(ex));
				message = "Could not upload the file: " + file.getOriginalFilename() + "!";
				return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message);
			}
		}
	}

	@PostMapping(path = "/transactions/upload/{transactionDate}")
	@ApiOperation(value = "Make a POST request to upload the file for a date",
			produces = "application/json", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> uploadFileForDate(
			@Parameter(
					description = "File to be uploaded",
					content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE)  // Won't work without OCTET_STREAM as the mediaType.
			) @RequestPart(value = "file") MultipartFile file,
			@PathVariable @Parameter(description = "transactionDate(yyyy-MM-dd)") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate transactionDate) {
		String message = "";

		if (file.isEmpty()) {
			message = "Please select a CSV file to upload.";
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message);
		} else {
			try {
				transactionService.saveFile(file, transactionDate);
				message = "Uploaded the file successfully: " + file.getOriginalFilename();
				return ResponseEntity.status(HttpStatus.OK).body(message);

			} catch (Exception ex) {
				System.err.println(ExceptionUtils.getStackTrace(ex));
				message = "Could not upload the file: " + file.getOriginalFilename() + "!";
				return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message);
			}
		}
	}

}
