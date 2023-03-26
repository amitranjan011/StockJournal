package com.amit.journal.service;

import com.amit.journal.model.TransactionKPI;
import com.amit.journal.model.TransactionSummary;

import java.util.List;

public interface TransactionKPIService {
    TransactionKPI generateKPI(List<TransactionSummary> tramSummaryList);
}
