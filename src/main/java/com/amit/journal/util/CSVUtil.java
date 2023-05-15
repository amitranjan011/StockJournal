package com.amit.journal.util;

import com.amit.journal.model.Transaction;
import com.amit.journal.model.TransactionSummary;
import com.amit.journal.service.TransactionServiceImpl;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.PrintWriter;
import java.util.List;

public class CSVUtil {
    private static final Logger LOG = LogManager.getLogger(CSVUtil.class);
//    public static void writeTransactionsToCsv(PrintWriter writer, List<Transaction> transactions) {
//		String[] CSV_HEADER = { "id", "firstname", "lastname" , "2", "3", "4", "5"};
//        StatefulBeanToCsv<Transaction> beanToCsv = null;
//        try (
//                CSVWriter csvWriter = new CSVWriter(writer,
//                        CSVWriter.DEFAULT_SEPARATOR,
//                        CSVWriter.NO_QUOTE_CHARACTER,
//                        CSVWriter.DEFAULT_ESCAPE_CHARACTER,
//                        CSVWriter.DEFAULT_LINE_END);
//        ) {
//			csvWriter.writeNext(CSV_HEADER);
//
//            // write List of Objects
//            ColumnPositionMappingStrategy<Transaction> mappingStrategy = new ColumnPositionMappingStrategy<Transaction>();
//
//            mappingStrategy.setType(Transaction.class);
//			mappingStrategy.setColumnMapping(CSV_HEADER);
//
//            beanToCsv = new StatefulBeanToCsvBuilder<Transaction>(writer)
//                    .withMappingStrategy(mappingStrategy)
//                    .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
//                    .build();
//
//            beanToCsv.write(transactions);
//            LOG.info("Write CSV using BeanToCsv successfully!");
//        } catch (Exception e) {
//            LOG.error("Exception while writing file for transactions : {}", ExceptionUtils.getStackTrace(e));
//        }
//    }

    public static void writeTransactionsToCsv2(PrintWriter writer, List<Transaction> transactions) {
        try {
            StatefulBeanToCsv<Transaction> csvWriter =
                    new StatefulBeanToCsvBuilder<Transaction>
                            (writer)
                            .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                            .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                            .withOrderedResults(false).build();


            csvWriter.write(transactions);
            LOG.info("Write CSV using BeanToCsv successfully!");
        } catch (Exception e) {
            LOG.error("Exception while writing file for transactions : {}", ExceptionUtils.getStackTrace(e));
        }
    }

    public static void writeTransactionSummaryToCsv(PrintWriter writer, List<TransactionSummary> summaries) {
        try {
            StatefulBeanToCsv<TransactionSummary> csvWriter =
                    new StatefulBeanToCsvBuilder<TransactionSummary>
                            (writer)
                            .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                            .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                            .withOrderedResults(false).build();


            csvWriter.write(summaries);
            LOG.info("Write CSV using BeanToCsv successfully!");
        } catch (Exception e) {
            LOG.error("Exception while writing file for TransactionSummary : {}", ExceptionUtils.getStackTrace(e));
        }
    }
}
