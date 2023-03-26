package com.amit.journal.model;

import java.util.List;

public class TransactionSummaryKPIHolder extends UserBase {
    private TransactionKPI transactionKPI;
    private List<TransactionSummary> summaryList;

    public TransactionKPI getTransactionKPI() {
        return transactionKPI;
    }

    public void setTransactionKPI(TransactionKPI transactionKPI) {
        this.transactionKPI = transactionKPI;
    }

    public List<TransactionSummary> getSummaryList() {
        return summaryList;
    }

    public void setSummaryList(List<TransactionSummary> summaryList) {
        this.summaryList = summaryList;
    }

    @Override
    public String toString() {
        return "TransactionSummaryKPIHolder{" +
                "transactionKPI=" + transactionKPI +
                ", summaryList=" + summaryList +
                '}';
    }
}
