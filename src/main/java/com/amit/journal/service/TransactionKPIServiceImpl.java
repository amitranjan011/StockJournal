package com.amit.journal.service;

import com.amit.journal.constants.Constants;
import com.amit.journal.model.TransactionKPI;
import com.amit.journal.model.TransactionSummary;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class TransactionKPIServiceImpl implements TransactionKPIService {

    private static final Logger LOG = LogManager.getLogger(TransactionKPIServiceImpl.class);
    @Override
    public TransactionKPI generateKPI(List<TransactionSummary> tramSummaryList) {
        Supplier<Stream<TransactionSummary>> summaryStream = tramSummaryList::stream;

        Comparator<TransactionSummary> comparator = Comparator.comparing(TransactionSummary::getPctReturn);
        TransactionSummary best = summaryStream.get().filter(summary -> summary.getPctReturn() < Constants.HOLDING_UNSOLD_RETURN_PERCENT).max(comparator).orElse(null);
        TransactionSummary worst = summaryStream.get().filter(summary -> summary.getPctReturn() < Constants.HOLDING_UNSOLD_RETURN_PERCENT).min(comparator).orElse(null);

        List<TransactionSummary> winning = summaryStream.get().filter(summary -> summary.getPctReturn() < Constants.HOLDING_UNSOLD_RETURN_PERCENT && summary.getPctReturn() > 0).collect(Collectors.toList());
        List<TransactionSummary> losing = summaryStream.get().filter(summary -> summary.getPctReturn() < 0).collect(Collectors.toList());

        double avgGainPct = winning.stream().mapToDouble(TransactionSummary::getPctReturn).summaryStatistics().getAverage();
        double avgLossPct = losing.stream().mapToDouble(TransactionSummary::getPctReturn).summaryStatistics().getAverage();
        double avgHoldDays = summaryStream.get().mapToInt(TransactionSummary::getDaysHeld).summaryStatistics().getAverage();

        TransactionKPI transactionKPI = new TransactionKPI();
        transactionKPI.setAvgGainPct(avgGainPct);
        transactionKPI.setAvgLossPct(avgLossPct);
        transactionKPI.setBestStock(best);
        transactionKPI.setWorstStock(worst);
        transactionKPI.setAvgHoldDays(avgHoldDays);
        return transactionKPI;
    }
}
