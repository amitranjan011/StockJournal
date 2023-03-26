package com.amit.journal.service;

import com.amit.journal.constants.Constants;
import com.amit.journal.model.TransactionKPI;
import com.amit.journal.model.TransactionSummary;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class TransactionKPIServiceImpl implements TransactionKPIService {


    @Override
    public TransactionKPI generateKPI(List<TransactionSummary> tramSummaryList) {
        Supplier<Stream<TransactionSummary>> summaryStream = tramSummaryList::stream;

        Comparator<TransactionSummary> comparator = Comparator.comparing(TransactionSummary::getPctReturn);
        TransactionSummary best = summaryStream.get().filter(summary -> summary.getPctReturn() < Constants.HOLDING_UNSOLD_RETURN_PERCENT).max(comparator).get();
        TransactionSummary worst = summaryStream.get().min(comparator).get();

        List<TransactionSummary> winning = summaryStream.get().filter(summary -> summary.getPctReturn() < Constants.HOLDING_UNSOLD_RETURN_PERCENT && summary.getPctReturn() > 0).collect(Collectors.toList());
        List<TransactionSummary> losing = summaryStream.get().filter(summary -> summary.getPctReturn() < 0).collect(Collectors.toList());

//        DoubleSummaryStatistics statistics = winning.stream().mapToDouble(TransactionSummary::getPctReturn).summaryStatistics();
        double avgGainPct = winning.stream().mapToDouble(TransactionSummary::getPctReturn).summaryStatistics().getAverage();
        double avgLossPct = losing.stream().mapToDouble(TransactionSummary::getPctReturn).summaryStatistics().getAverage();

        TransactionKPI transactionKPI = new TransactionKPI();
        transactionKPI.setAvgGainPct(avgGainPct);
        transactionKPI.setAvgLossPct(avgLossPct);
        transactionKPI.setBestStock(best);
        transactionKPI.setWorstStock(worst);
        return transactionKPI;
    }
}